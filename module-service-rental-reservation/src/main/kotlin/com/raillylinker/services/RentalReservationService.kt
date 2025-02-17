package com.raillylinker.services

import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.controllers.RentalReservationController
import com.raillylinker.jpa_beans.db1_main.entities.*
import com.raillylinker.jpa_beans.db1_main.repositories.*
import com.raillylinker.redis_map_components.redis1_main.Redis1_Lock_RentalProductInfo
import com.raillylinker.util_components.JwtTokenUtil
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class RentalReservationService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val jwtTokenUtil: JwtTokenUtil,

    private val db1RaillyLinkerCompanyRentableProductImageRepository: Db1_RaillyLinkerCompany_RentalProductImage_Repository,
    private val db1RaillyLinkerCompanyRentableProductInfoRepository: Db1_RaillyLinkerCompany_RentalProduct_Repository,
    private val db1RaillyLinkerCompanyRentableProductReservationInfoRepository: Db1_RaillyLinkerCompany_RentalProductReservation_Repository,
    private val db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository: Db1_RaillyLinkerCompany_RentalProductReservationHistory_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberRepository: Db1_RaillyLinkerCompany_TotalAuthMember_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberEmailRepository: Db1_RaillyLinkerCompany_TotalAuthMemberEmail_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberPhoneRepository: Db1_RaillyLinkerCompany_TotalAuthMemberPhone_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberProfileRepository: Db1_RaillyLinkerCompany_TotalAuthMemberProfile_Repository,
    private val db1RaillyLinkerCompanyRentalProductReservationImageRepository: Db1_RaillyLinkerCompany_RentalProductReservationImage_Repository,

    private val redis1LockRentalProductInfo: Redis1_Lock_RentalProductInfo
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // (현 프로젝트 동작 서버의 외부 접속 주소)
    // 프로필 이미지 로컬 저장 및 다운로드 주소 지정을 위해 필요
    // !!!프로필별 접속 주소 설정하기!!
    // ex : http://127.0.0.1:8080
    private val externalAccessAddress: String
        get() {
            return when (activeProfile) {
                "prod80" -> {
                    "http://127.0.0.1"
                }

                "dev8080" -> {
                    "http://127.0.0.1:8080"
                }

                else -> {
                    "http://127.0.0.1:8080"
                }
            }
        }


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (상품 예약 신청하기 <>)
    // 동시 대량 요청이 예상되며 Database 의 데이터 중 Race Condition 을 유발하는 부분이 있으므로 적절히 처리 및 테스트가 필요
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postProductReservation(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationController.PostProductReservationInputVo
    ): RentalReservationController.PostProductReservationOutputVo? {
        if (inputVo.rentalUnitCount < 0) {
            // 대여 단위 예약 횟수가 음수면 안됩니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "12")
            return null
        }

        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData =
            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        val nowDatetime = LocalDateTime.now()

        val rentalStartDatetime = ZonedDateTime.parse(
            inputVo.rentalStartDatetime,
            DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_z")
        ).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

        if (nowDatetime.isAfter(rentalStartDatetime)) {
            // 대여 시작 일시가 현재 일시보다 작을 경우 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "11")
            return null
        }

        // Redis 공유 락 처리 (예약과 관련된 정보 수정시에는 모두 공유락을 적용해야 합니다.)
        return redis1LockRentalProductInfo.tryLockRepeat(
            "${inputVo.rentalProductUid}",
            7000L,
            {
                val rentableProductInfo =
                    db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                        inputVo.rentalProductUid,
                        "/"
                    )

                if (rentableProductInfo == null) {
                    // 상품 정보가 없는 경우 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat null
                }

                if (rentableProductInfo.versionSeq != inputVo.rentableProductVersionSeq) {
                    // 고객이 본 정보와 상품 정보의 버전 시퀀스가 다른 경우 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return@tryLockRepeat null
                }

                if (!rentableProductInfo.nowReservable) {
                    // 현 시점 예약 가능 설정이 아닐 때 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return@tryLockRepeat null
                }

                if (nowDatetime.isBefore(rentableProductInfo.firstReservableDatetime)) {
                    // 현재 시간이 예약 가능 일시보다 작음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "4")
                    return@tryLockRepeat null
                }

                if (inputVo.rentalUnitCount < rentableProductInfo.minimumReservationUnitCount) {
                    // rentalUnitCount 가 단위 예약 최소 횟수보다 작을 때 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "5")
                    return@tryLockRepeat null
                }

                if (rentableProductInfo.maximumReservationUnitCount != null &&
                    inputVo.rentalUnitCount > rentableProductInfo.maximumReservationUnitCount!!
                ) {
                    // rentalUnitCount 가 단위 예약 최대 횟수보다 클 때 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "6")
                    return@tryLockRepeat null
                }

                val inputRentalEndDatetime = ZonedDateTime.parse(
                    inputVo.rentalEndDatetime,
                    DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_z")
                ).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

                val rentalEndDatetime =
                    rentalStartDatetime.plusMinutes(rentableProductInfo.reservationUnitMinute * inputVo.rentalUnitCount)

                if (!(inputRentalEndDatetime.isEqual(rentalEndDatetime))) {
                    // 고객이 보낸 대여 시간이 일치하지 않습니다. -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "7")
                    return@tryLockRepeat null
                }

                if (rentalStartDatetime.isBefore(rentableProductInfo.firstRentalDatetime)) {
                    // 예약 상품 대여 가능 최초 일시가 대여 시작일보다 큽니다. -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "8")
                    return@tryLockRepeat null
                }

                if (rentalEndDatetime.isAfter(rentableProductInfo.lastRentalDatetime)) {
                    // 예약 상품 대여 가능 마지막 일시가 대여 마지막일보다 작습니다. -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "9")
                    return@tryLockRepeat null
                }

                if (db1RaillyLinkerCompanyRentableProductReservationInfoRepository
                        .existsByRentalProductAndRowDeleteDateStrAndProductReadyDatetime(
                            rentableProductInfo,
                            "/",
                            null
                        )
                ) {
                    // 예약 상품이 현재 예약 중입니다.(예약 준비 시간이 결정되지 않음)-> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "10")
                    return@tryLockRepeat null
                }

                // 예약 정보 입력
                val newReservationInfo =
                    db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(
                        Db1_RaillyLinkerCompany_RentalProductReservation(
                            rentableProductInfo,
                            memberData,
                            rentalStartDatetime,
                            rentalEndDatetime,
                            nowDatetime, // 고객에게 이때까지 결제를 해야 한다고 통보한 기한 임시 입력
                            nowDatetime, // 예약 결제 확인 기한 임시 입력
                            nowDatetime, // 관리자 승인 기한 임시 입력
                            nowDatetime, // 예약 취소 가능 기한 임시 입력
                            null,
                            rentableProductInfo.productName,
                            rentableProductInfo.productIntro,
                            rentableProductInfo.addressCountry,
                            rentableProductInfo.addressMain,
                            rentableProductInfo.addressDetail
                        )
                    )

                // 예약 신청 일시를 기준으로 기한 관련 데이터 계산 및 검증
                val reservationDatetime = newReservationInfo.rowCreateDate!!
                val customerPaymentDeadlineDatetime =
                    reservationDatetime.plusMinutes(rentableProductInfo.customerPaymentDeadlineMinute)
                val paymentCheckDeadlineDatetime =
                    reservationDatetime.plusMinutes(rentableProductInfo.paymentCheckDeadlineMinute)
                val reservationApprovalDeadlineDatetime =
                    reservationDatetime.plusMinutes(rentableProductInfo.approvalDeadlineMinute)
                val reservationCancelDeadlineDatetime =
                    rentalStartDatetime.minusMinutes(rentableProductInfo.cancelDeadlineMinute)

                // 예약 신청 일시를 기준으로 만들어진 기한 관련 데이터 입력
                newReservationInfo.customerPaymentDeadlineDatetime = customerPaymentDeadlineDatetime
                newReservationInfo.paymentCheckDeadlineDatetime = paymentCheckDeadlineDatetime
                newReservationInfo.approvalDeadlineDatetime = reservationApprovalDeadlineDatetime
                newReservationInfo.cancelDeadlineDatetime = reservationCancelDeadlineDatetime

                db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(newReservationInfo)

                // 예약 히스토리 정보 입력
                db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                    Db1_RaillyLinkerCompany_RentalProductReservationHistory(
                        newReservationInfo,
                        0,
                        "예약 신청"
                    )
                )

                // 이미지 테이블 백업 저장
                for (productImage in rentableProductInfo.rentalProductImageList) {
                    db1RaillyLinkerCompanyRentalProductReservationImageRepository.save(
                        Db1_RaillyLinkerCompany_RentalProductReservationImage(
                            newReservationInfo,
                            productImage.imageFullUrl,
                            productImage.priority
                        )
                    )
                }

                httpServletResponse.status = HttpStatus.OK.value()
                return@tryLockRepeat RentalReservationController.PostProductReservationOutputVo(
                    newReservationInfo.uid!!,
                    newReservationInfo.customerPaymentDeadlineDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_z")),
                    newReservationInfo.cancelDeadlineDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_z"))
                )
            }
        )
    }


    // ----
    // (예약 취소 신청 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postCancelProductReservation(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentalProductReservationUid: Long,
        inputVo: RentalReservationController.PostCancelProductReservationInputVo
    ): RentalReservationController.PostCancelProductReservationOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        val reservationEntity: Db1_RaillyLinkerCompany_RentalProductReservation? =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentalProductReservationUid,
                "/"
            )

        if (reservationEntity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        if (reservationEntity.totalAuthMember.uid != memberUid) {
            // 고객이 진행중인 예약이 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val nowDatetime = LocalDateTime.now()

        // 예약 취소 신청 가능 상태 확인
        if (nowDatetime.isAfter(reservationEntity.cancelDeadlineDatetime)) {
            // 예약 취소 가능 기한 초과 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentalProductReservationAndRowDeleteDateStrOrderByRowCreateDateDesc(
                reservationEntity,
                "/"
            )

        var notApproved = true
        var notPaid = true
        var paymentNotChecked = true


        var notRequestCancel = true
//        var notRequestCancelDeny = true
//        var notRequestCancelCancel = true
        var notCancelChecked = true
        for (history in historyList) {
            when (history.historyCode.toInt()) {
                2 -> {
                    // 예약 취소 승인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return null
                }

                3 -> {
                    // 예약 신청 거부 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "4")
                    return null
                }

                4 -> {
                    // 관리자 예약 신청 승인 상태
                    notApproved = false
                }

                6 -> {
                    // 결제 확인
                    if (paymentNotChecked) {
                        notPaid = false
                        paymentNotChecked = false
                    }
                }

                7 -> {
                    // 결제 확인 취소
                    if (paymentNotChecked) {
                        paymentNotChecked = false
                    }
                }

                17 -> {
                    // 예약 취소 신청 취소
                    if (notCancelChecked) {
                        notCancelChecked = false
                        // 예약 취소 거부 내역이 최신인지
//                        notRequestCancelCancel = false
                    }
                }

                8 -> {
                    // 예약 취소 거부
                    if (notCancelChecked) {
                        notCancelChecked = false
                        // 예약 취소 거부 내역이 최신인지
//                        notRequestCancelDeny = false
                    }
                }

                1 -> {
                    // 예약 취소 신청
                    if (notCancelChecked) {
                        notCancelChecked = false
                        // 예약 취소 거부 내역이 최신인지
                        notRequestCancel = false
                    }
                }
            }
        }

        if (notPaid && nowDatetime.isAfter(reservationEntity.paymentCheckDeadlineDatetime)) {
            // 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일) -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return null
        }

        if (!notRequestCancel) {
            // 예약 취소 신청 상태 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "6")
            return null
        }

        // 예약 취소 신청 정보 추가
        val reservationCancelEntity = db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentalProductReservationHistory(
                reservationEntity,
                1,
                inputVo.cancelReason
            )
        )

        // 상태에 따라 예약 취소 자동 승인 정보 추가
        val autoCancelCompleteEntityUid =
            if (!notPaid && (!notApproved || nowDatetime.isAfter(reservationEntity.approvalDeadlineDatetime))) {
                // 결제 확인 상태 && (예약 승인 상태 || 예약 승인 기한 초과)
                null
            } else {
                // 예약 완전 승인 상태가 아니라면 자동 취소 승인 처리
                db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                    Db1_RaillyLinkerCompany_RentalProductReservationHistory(
                        reservationEntity,
                        2,
                        "자동 취소 승인 처리"
                    )
                ).uid
            }

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationController.PostCancelProductReservationOutputVo(
            reservationCancelEntity.uid!!,
            autoCancelCompleteEntityUid
        )
    }


    // ----
    // (예약 취소 신청 취소 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postCancelProductReservationCancel(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentalProductReservationUid: Long
    ): RentalReservationController.PostCancelProductReservationCancelOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        val reservationEntity: Db1_RaillyLinkerCompany_RentalProductReservation? =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentalProductReservationUid,
                "/"
            )

        if (reservationEntity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        if (reservationEntity.totalAuthMember.uid != memberUid) {
            // 고객이 진행중인 예약이 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val nowDatetime = LocalDateTime.now()

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentalProductReservationAndRowDeleteDateStrOrderByRowCreateDateDesc(
                reservationEntity,
                "/"
            )

        var notPaid = true
        var paymentNotChecked = true

        var notRequestCancel = true
//        var notRequestCancelDeny = true
//        var notRequestCancelCancel = true
        var notCancelChecked = true
        for (history in historyList) {
            when (history.historyCode.toInt()) {
                2 -> {
                    // 예약 취소 승인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return null
                }

                3 -> {
                    // 예약 신청 거부 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return null
                }

                6 -> {
                    // 결제 확인
                    if (paymentNotChecked) {
                        notPaid = false
                        paymentNotChecked = false
                    }
                }

                7 -> {
                    // 결제 확인 취소
                    if (paymentNotChecked) {
                        paymentNotChecked = false
                    }
                }

                17 -> {
                    // 예약 취소 신청 취소
                    if (notCancelChecked) {
                        notCancelChecked = false
                        // 예약 취소 취소 내역이 최신인지
//                        notRequestCancelCancel = false
                    }
                }

                8 -> {
                    // 예약 취소 거부
                    if (notCancelChecked) {
                        notCancelChecked = false
                        // 예약 취소 거부 내역이 최신인지
//                        notRequestCancelDeny = false
                    }
                }

                1 -> {
                    // 예약 취소 신청
                    if (notCancelChecked) {
                        notCancelChecked = false
                        // 예약 취소 거부 내역이 최신인지
                        notRequestCancel = false
                    }
                }
            }
        }

        if (notPaid && nowDatetime.isAfter(reservationEntity.paymentCheckDeadlineDatetime)) {
            // 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일) -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        if (notRequestCancel) {
            // 예약 취소 신청 상태가 없습니다. -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return null
        }

        // 예약 취소 신청 정보 추가
        val reservationCancelEntity = db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentalProductReservationHistory(
                reservationEntity,
                17,
                "사용자 예약 취소 신청 철회"
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationController.PostCancelProductReservationCancelOutputVo(
            reservationCancelEntity.uid!!
        )
    }


    // ----
    // (개별 상품 조기 반납 신고 <ADMIN>)
    // 관리자의 상품 반납 확인과 고객의 조기 반납 신고 간의 공유락 처리
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductStockReservationInfoEarlyReturn(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentalProductReservationUid: Long,
        inputVo: RentalReservationController.PostRentableProductStockReservationInfoEarlyReturnInputVo
    ): RentalReservationController.PostRentableProductStockReservationInfoEarlyReturnOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentalProductReservationUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        if (rentableProductStockReservationInfo.totalAuthMember.uid != memberUid) {
            // 고객이 진행중인 예약이 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val nowDatetime = LocalDateTime.now()
        if (nowDatetime.isBefore(rentableProductStockReservationInfo.rentalStartDatetime)) {
            // 상품 대여 시작을 넘지 않음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        if (nowDatetime.isAfter(rentableProductStockReservationInfo.rentalEndDatetime)) {
            // 상품 대여 마지막일을 넘음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        val reservationHistoryList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentalProductReservationAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo,
                "/"
            )

        var notPaid = true
        var paymentNotChecked = true
        var noEarlyReturn = true
        var noEarlyReturnCancel = true
        for (history in reservationHistoryList) {
            when (history.historyCode.toInt()) {
                3 -> {
                    // 예약 신청 거부 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return null
                }

                6 -> {
                    // 결제 확인
                    if (paymentNotChecked) {
                        notPaid = false
                        paymentNotChecked = false
                    }
                }

                7 -> {
                    // 결제 확인 취소
                    if (paymentNotChecked) {
                        paymentNotChecked = false
                    }
                }

                11 -> {
                    // 반납 확인
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "5")
                    return null
                }

                10 -> {
                    // 조기 반납 취소
                    if (noEarlyReturn) {
                        noEarlyReturnCancel = false
                    }
                }

                9 -> {
                    // 조기 반납 상태
                    if (noEarlyReturnCancel) {
                        noEarlyReturn = false
                    }
                }
            }
        }

        if (notPaid && nowDatetime.isAfter(rentableProductStockReservationInfo.paymentCheckDeadlineDatetime)) {
            // 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일) -> return
            // 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        if (!noEarlyReturn) {
            // 조기 반납 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "6")
            return null
        }

        // 개별 상품 조기반납 신고 내역 추가
        val newReservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentalProductReservationHistory(
                    rentableProductStockReservationInfo,
                    9,
                    inputVo.stateChangeDesc
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationController.PostRentableProductStockReservationInfoEarlyReturnOutputVo(
            newReservationStateChangeHistory.uid!!
        )
    }


    // ----
    // (개별 상품 조기 반납 신고 취소 <ADMIN>)
    // 관리자의 상품 반납 확인과 고객의 조기 반납 신고 간의 공유락 처리
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductStockReservationInfoEarlyReturnCancel(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentalProductReservationUid: Long,
        inputVo: RentalReservationController.PostRentableProductStockReservationInfoEarlyReturnCancelInputVo
    ): RentalReservationController.PostRentableProductStockReservationInfoEarlyReturnCancelOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentalProductReservationUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        if (rentableProductStockReservationInfo.totalAuthMember.uid != memberUid) {
            // 고객이 진행중인 예약이 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 상태 확인
        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentalProductReservationAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo,
                "/"
            )

        var noEarlyReturn = true
        var noEarlyReturnCancel = true
        for (history in historyList) {
            when (history.historyCode.toInt()) {
                10 -> {
                    // 조기 반납 취소
                    if (noEarlyReturn) {
                        noEarlyReturnCancel = false
                    }
                }

                9 -> {
                    // 조기 반납 상태
                    if (noEarlyReturnCancel) {
                        noEarlyReturn = false
                    }
                }
            }
        }

        if (noEarlyReturn && noEarlyReturnCancel) {
            // 조기 반납 상태 변경 내역이 없습니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        if (!noEarlyReturnCancel) {
            // 조기 반납 상태 변경 취소 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        // 개별 상품 조기 반납 취소 내역 추가
        val newReservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentalProductReservationHistory(
                    rentableProductStockReservationInfo,
                    10,
                    inputVo.stateChangeDesc
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationController.PostRentableProductStockReservationInfoEarlyReturnCancelOutputVo(
            newReservationStateChangeHistory.uid!!
        )
    }
}