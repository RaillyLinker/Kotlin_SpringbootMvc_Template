package com.raillylinker.module_service_rental_reservation.services

import com.raillylinker.module_service_rental_reservation.util_components.JwtTokenUtil
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.module_service_rental_reservation.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.module_service_rental_reservation.controllers.RentalReservationController
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

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
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

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationController.PostCancelProductReservationOutputVo(
            1L,
            false
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

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationController.PostRentalProductEarlyReturnOutputVo(
            1L
        )
    }
}