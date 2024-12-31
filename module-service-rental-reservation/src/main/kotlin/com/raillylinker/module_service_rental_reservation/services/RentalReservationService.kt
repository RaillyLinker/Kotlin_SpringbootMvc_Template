package com.raillylinker.module_service_rental_reservation.services

import com.raillylinker.module_service_rental_reservation.util_components.JwtTokenUtil
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.module_service_rental_reservation.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.module_service_rental_reservation.controllers.RentalReservationController
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationInfo
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_Native_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_PaymentRefund_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_Payment_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductCategory_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductImage_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductInfo_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductReservationInfo_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductReservationPayment_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockCategory_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockImage_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockInfo_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockReservationInfo_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberEmail_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberPhone_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberProfile_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMember_Repository
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
    private val db1RaillyLinkerCompanyTotalAuthMemberProfileRepository: Db1_RaillyLinkerCompany_TotalAuthMemberProfile_Repository
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

        // 검증
        // todo 대여 시작 일시가 끝 일시보다 클 경우 -> return
        // todo 업데이트 버전 시퀀스가 다를 때 -> return
        // todo 현 시점 예약 가능 설정이 아닐 때 -> return
        // todo 현재 시간이 예약 가능 일시보다 작음 -> return
        // todo 대여 시작 일시가 예약 취소 가능 기한(현재 시간 + N)과 같거나 작음 -> return
        // todo 대여 시작 일시와 끝 일시의 차이를 단위 예약 시간으로 나누었을 때, 단위 예약 최소 횟수보다 작을 때 -> return
        // todo 대여 시작 일시와 끝 일시의 차이를 단위 예약 시간으로 나누었을 때, 단위 예약 최대 횟수보다 클 때 -> return

        // todo 재고 리스트 중 없는 개체가 있습니다.
        // todo 재고 리스트 중 현재 예약 중인 개체가 있습니다. -> return
        // todo 재고 리스트 중 대여 가능 최초 일시가 더 큰 개체가 있습니다. -> return
        // todo 재고 리스트 중 대여 가능 마지막 일시가 더 작은 개체가 있습니다. -> return
        // todo 재고 리스트 중 대여 가능 설정이 아닌 상품이 있습니다. -> return

        // todo 예약 정보 입력
        // todo 개별 상품 예약 정보 입력

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationController.PostProductReservationOutputVo(
            1L,
            "2024_05_02_T_15_14_49_552_KST",
            "2024_05_02_T_15_14_49_552_KST"
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
            when (history.stateCode) {
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
                inputVo.cancelReason,
                nowDatetime
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
                        inputVo.cancelReason,
                        nowDatetime
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
            when (history.stateCode) {
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
                inputVo.earlyReturnReason,
                anchorDatetime
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationController.PostRentalProductEarlyReturnOutputVo(
            stateChangeEntity.uid!!
        )
    }
}