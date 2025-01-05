package com.raillylinker.services

import com.raillylinker.util_components.JwtTokenUtil
import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.controllers.RentalReservationController
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationInfo
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockInfo
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockReservationInfo
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_Native_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_PaymentRefund_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_Payment_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductCategory_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductImage_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductInfo_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductReservationInfo_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductReservationPayment_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockCategory_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockImage_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockInfo_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockReservationInfo_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberEmail_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberPhone_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberProfile_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMember_Repository
import com.raillylinker.redis_map_components.redis1_main.Redis1_Lock_RentableProductInfo
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
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.floor

@Service
class RentalReservationService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val jwtTokenUtil: JwtTokenUtil,

    private val db1NativeRepository: Db1_Native_Repository,
    private val db1RaillyLinkerCompanyPaymentRepository: Db1_RaillyLinkerCompany_Payment_Repository,
    private val db1RaillyLinkerCompanyPaymentRefundRepository: Db1_RaillyLinkerCompany_PaymentRefund_Repository,
    private val db1RaillyLinkerCompanyRentableProductCategoryRepository: Db1_RaillyLinkerCompany_RentableProductCategory_Repository,
    private val db1RaillyLinkerCompanyRentableProductImageRepository: Db1_RaillyLinkerCompany_RentableProductImage_Repository,
    private val db1RaillyLinkerCompanyRentableProductInfoRepository: Db1_RaillyLinkerCompany_RentableProductInfo_Repository,
    private val db1RaillyLinkerCompanyRentableProductReservationInfoRepository: Db1_RaillyLinkerCompany_RentableProductReservationInfo_Repository,
    private val db1RaillyLinkerCompanyRentableProductReservationPaymentRepository: Db1_RaillyLinkerCompany_RentableProductReservationPayment_Repository,
    private val db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository: Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory_Repository,
    private val db1RaillyLinkerCompanyRentableProductStockCategoryRepository: Db1_RaillyLinkerCompany_RentableProductStockCategory_Repository,
    private val db1RaillyLinkerCompanyRentableProductStockImageRepository: Db1_RaillyLinkerCompany_RentableProductStockImage_Repository,
    private val db1RaillyLinkerCompanyRentableProductStockInfoRepository: Db1_RaillyLinkerCompany_RentableProductStockInfo_Repository,
    private val db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository: Db1_RaillyLinkerCompany_RentableProductStockReservationInfo_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberRepository: Db1_RaillyLinkerCompany_TotalAuthMember_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberEmailRepository: Db1_RaillyLinkerCompany_TotalAuthMemberEmail_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberPhoneRepository: Db1_RaillyLinkerCompany_TotalAuthMemberPhone_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberProfileRepository: Db1_RaillyLinkerCompany_TotalAuthMemberProfile_Repository,

    private val redis1LockRentableProductInfo: Redis1_Lock_RentableProductInfo
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
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData =
            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        val rentalStartDatetime = ZonedDateTime.parse(
            inputVo.rentalStartDatetime,
            DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
        ).toLocalDateTime()

        val rentalEndDatetime = ZonedDateTime.parse(
            inputVo.rentalEndDatetime,
            DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
        ).toLocalDateTime()

        if (rentalStartDatetime.isAfter(rentalEndDatetime)) {
            // 대여 시작 일시가 끝 일시보다 클 경우 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // Redis 공유 락 처리 (예약과 관련된 정보 수정시에는 모두 공유락을 적용해야 합니다.)
        return redis1LockRentableProductInfo.tryLockRepeat<RentalReservationController.PostProductReservationOutputVo?>(
            "${inputVo.rentableProductInfoUid}",
            7000L,
            {
                val rentableProductInfo =
                    db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                        inputVo.rentableProductInfoUid,
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
                    httpServletResponse.setHeader("api-result-code", "3")
                    return@tryLockRepeat null
                }

                if (!rentableProductInfo.nowReservable) {
                    // 현 시점 예약 가능 설정이 아닐 때 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "4")
                    return@tryLockRepeat null
                }

                val nowDatetime = LocalDateTime.now()

                if (nowDatetime.isBefore(rentableProductInfo.firstReservableDatetime)) {
                    // 현재 시간이 예약 가능 일시보다 작음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "5")
                    return@tryLockRepeat null
                }

                // 대여 시작 / 끝 시간 차이(분)
                val rentalStartAndEndTimeDiff = ChronoUnit.MINUTES.between(
                    rentalStartDatetime,
                    rentalEndDatetime
                )

                // 설정 시간을 단위 시간으로 나눈 값 = 단위 설정 횟수
                val calcTimeUnitCount =
                    rentalStartAndEndTimeDiff.toDouble() / rentableProductInfo.reservationUnitMinute.toDouble()
                val calcTimeUnitCountUp = ceil(calcTimeUnitCount).toLong()
                val calcTimeUnitCountDown = floor(calcTimeUnitCount).toLong()

                if (calcTimeUnitCountUp < rentableProductInfo.minimumReservationUnitCount) {
                    // 대여 시작 일시와 끝 일시의 차이를 단위 예약 시간으로 나누었을 때, 단위 예약 최소 횟수보다 작을 때 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "6")
                    return@tryLockRepeat null
                }

                if (rentableProductInfo.maximumReservationUnitCount != null &&
                    calcTimeUnitCountDown > rentableProductInfo.maximumReservationUnitCount!!
                ) {
                    // 대여 시작 일시와 끝 일시의 차이를 단위 예약 시간으로 나누었을 때, 단위 예약 최대 횟수보다 클 때 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "7")
                    return@tryLockRepeat null
                }

                // 예약할 재고 정보 리스트
                val rentableProductStockEntityList: MutableList<Db1_RaillyLinkerCompany_RentableProductStockInfo> =
                    mutableListOf()

                for (stockUid in inputVo.rentableProductStockInfoUidList) {
                    val rentableProductStockEntity =
                        db1RaillyLinkerCompanyRentableProductStockInfoRepository.findByUidAndRowDeleteDateStr(
                            stockUid,
                            "/"
                        )

                    if (rentableProductStockEntity == null) {
                        // 재고 리스트 중 없는 개체가 있습니다. -> return
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "9")
                        return@tryLockRepeat null
                    }

                    if (!rentableProductStockEntity.nowReservable) {
                        //  재고 리스트 중 대여 가능 설정이 아닌 상품이 있습니다. -> return
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "10")
                        return@tryLockRepeat null
                    }

                    if (rentalStartDatetime.isBefore(rentableProductStockEntity.firstRentableDatetime)) {
                        // 재고 리스트 중 대여 가능 최초 일시가 더 큰 개체가 있습니다. -> return
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "11")
                        return@tryLockRepeat null
                    }

                    if (rentalEndDatetime.isAfter(rentableProductStockEntity.lastRentableDatetime)) {
                        // 재고 리스트 중 대여 가능 마지막 일시가 더 작은 개체가 있습니다. -> return
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "12")
                        return@tryLockRepeat null
                    }

//                    if (db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository
//                            .existsByRentableProductStockInfoAndRowDeleteDateStrAndNextReadyDatetime(
//                                rentableProductStockEntity
//                            )
//                    ) {
//                        // 재고 리스트 중 현재 예약 중인 개체가 있습니다. -> return
//                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
//                        httpServletResponse.setHeader("api-result-code", "13")
//                        return@tryLockRepeat null
//                    }

                    rentableProductStockEntityList.add(rentableProductStockEntity)
                }

                // 예약 정보 입력
                val newReservationInfo = db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(
                    Db1_RaillyLinkerCompany_RentableProductReservationInfo(
                        rentableProductInfo,
                        memberData,
                        rentalStartDatetime,
                        rentalEndDatetime,
                        nowDatetime, // 고객에게 이때까지 결제를 해야 한다고 통보한 기한 임시 입력
                        nowDatetime, // 예약 결제 확인 기한 임시 입력
                        nowDatetime, // 관리자 승인 기한 임시 입력
                        nowDatetime, // 예약 취소 가능 기한 임시 입력
                        rentableProductInfo.productName,
                        rentableProductInfo.productIntro,
                        rentableProductInfo.frontRentableProductImage,
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

                if (reservationApprovalDeadlineDatetime.isAfter(reservationCancelDeadlineDatetime)) {
                    // 취소 가능 기한의 계산 결과가 관리자 승인 기한보다 작습니다. -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "8")
                    return@tryLockRepeat null
                }

                // 예약 신청 일시를 기준으로 만들어진 기한 관련 데이터 입력
                newReservationInfo.customerPaymentDeadlineDatetime = customerPaymentDeadlineDatetime
                newReservationInfo.paymentCheckDeadlineDatetime = paymentCheckDeadlineDatetime
                newReservationInfo.reservationApprovalDeadlineDatetime = reservationApprovalDeadlineDatetime
                newReservationInfo.reservationCancelDeadlineDatetime = reservationCancelDeadlineDatetime

                db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(newReservationInfo)

                // 개별 상품 예약 정보 입력
                for (rentableProductStockEntity in rentableProductStockEntityList) {
                    db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.save(
                        Db1_RaillyLinkerCompany_RentableProductStockReservationInfo(
                            rentableProductStockEntity,
                            newReservationInfo,
                            null
                        )
                    )
                }

                httpServletResponse.status = HttpStatus.OK.value()
                return@tryLockRepeat RentalReservationController.PostProductReservationOutputVo(
                    newReservationInfo.uid!!,
                    newReservationInfo.customerPaymentDeadlineDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    newReservationInfo.reservationCancelDeadlineDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
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
        inputVo: RentalReservationController.PostCancelProductReservationInputVo
    ): RentalReservationController.PostCancelProductReservationOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData =
            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        val reservationEntity: Db1_RaillyLinkerCompany_RentableProductReservationInfo? =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndTotalAuthMemberAndRowDeleteDateStr(
                inputVo.rentableProductReservationInfoUid,
                memberData,
                "/"
            )

        if (reservationEntity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val nowDatetime = LocalDateTime.now()

        // 예약 취소 신청 가능 상태 확인
        if (nowDatetime.isAfter(reservationEntity.reservationCancelDeadlineDatetime)) {
            // 예약 취소 가능 기한 초과 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                reservationEntity,
                "/"
            )

        var notApproved = true
        var notPaid = true
        var requestCancel = false
        var notRequestCancelDenyLatest = true
        var notRequestCancelLatest = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                5 -> {
                    // 예약 취소 거부
                    if (notRequestCancelLatest) {
                        // 예약 취소 거부 내역이 최신인지
                        notRequestCancelDenyLatest = false
                    }
                }

                4 -> {
                    // 예약 취소 승인 상태 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return null
                }

                3 -> {
                    // 예약 취소 신청
                    requestCancel = true
                    if (notRequestCancelDenyLatest) {
                        // 예약 취소 신청 내역이 최신인지
                        notRequestCancelLatest = false
                    }
                }

                2 -> {
                    // 예약 신청 거부 상태 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "4")
                    return null
                }

                1 -> {
                    // 관리자 예약 신청 승인 상태
                    notApproved = false
                }

                0 -> {
                    // 결제 확인 상태
                    notPaid = false
                }
            }
        }

        if (notPaid && nowDatetime.isAfter(reservationEntity.paymentCheckDeadlineDatetime)) {
            // 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일) -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return null
        }

        if (requestCancel) {
            // 기존 예약 취소 신청 내역이 있고,
            if (notRequestCancelDenyLatest && !notRequestCancelLatest) {
                // 예약 취소 신청 상태 -> return
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "6")
                return null
            }
        }

        // 예약 취소 신청 정보 추가
        val reservationCancelEntity = db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                reservationEntity,
                3,
                inputVo.cancelReason
            )
        )

        // 상태에 따라 예약 취소 자동 승인 정보 추가
        val autoCancelCompleteEntityUid =
            if (notPaid || notApproved) {
                // 결제 확인 완료 아님 || 예약 신청 승인 아님 상태라면 자동 취소 승인 처리
                db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                    Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                        reservationEntity,
                        4,
                        inputVo.cancelReason
                    )
                ).uid
            } else {
                null
            }

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationController.PostCancelProductReservationOutputVo(
            reservationCancelEntity.uid!!,
            autoCancelCompleteEntityUid
        )
    }


    // ----
    // (대여품 조기반납 신고 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentalProductEarlyReturn(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationController.PostRentalProductEarlyReturnInputVo
    ): RentalReservationController.PostRentalProductEarlyReturnOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData =
            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        val reservationEntity: Db1_RaillyLinkerCompany_RentableProductReservationInfo? =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndTotalAuthMemberAndRowDeleteDateStr(
                inputVo.rentableProductReservationInfoUid,
                memberData,
                "/"
            )

        if (reservationEntity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val anchorDatetime = ZonedDateTime.parse(
            inputVo.stateChangeDatetime,
            DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
        ).toLocalDateTime()

        val nowDatetime = LocalDateTime.now()

        if (anchorDatetime.isBefore(nowDatetime)) {
            // 조기 반납 기준 일시가 현재보다 앞으로 설정됨 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        if (anchorDatetime.isAfter(reservationEntity.rentalEndDatetime)) {
            // 조기 반납 기준 일시가 대여 마지막 일시보다 뒤로 설정됨 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        if (anchorDatetime.isBefore(reservationEntity.rentalStartDatetime)) {
            // 조기 반납 기준 일시가 대여 시작일보다 앞으로 설정됨 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                reservationEntity,
                "/"
            )

        if (historyList.isEmpty()) {
            // 결재 대기 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return null
        }

        var notPaid = true
        var notApproved = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                4 -> {
                    // 예약 취소 승인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "7")
                    return null
                }

                1 -> {
                    // 예약 승인
                    notApproved = false
                }

                0 -> {
                    notPaid = false
                }
            }
        }

        if (notPaid) {
            // 미결제 상태 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "6")
            return null
        }

        if (notApproved) {
            // 예약 신청 승인 처리가 되지 않음 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "8")
            return null
        }

        // 조기 반납 신고 정보 입력
        val stateChangeEntity = db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                reservationEntity,
                6,
                inputVo.earlyReturnReason
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationController.PostRentalProductEarlyReturnOutputVo(
            stateChangeEntity.uid!!
        )
    }
}