package com.raillylinker.module_service_rental_reservation.services

import com.raillylinker.module_service_rental_reservation.util_components.JwtTokenUtil
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.module_service_rental_reservation.controllers.RentalReservationAdminController
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

@Service
class RentalReservationAdminService(
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
    // (예약 상품 카테고리 정보 등록 <ADMIN>)
    fun postRentableProductCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductCategoryInputVo
    ): RentalReservationAdminController.PostRentableProductCategoryOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductCategoryOutputVo(
            1L
        )
    }


    // ----
    // (예약 상품 카테고리 정보 수정 <ADMIN>)
    fun putRentableProductCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductCategoryUid: Long,
        inputVo: RentalReservationAdminController.PutRentableProductCategoryInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (예약 상품 카테고리 정보 삭제 <ADMIN>)
    fun deleteRentableProductCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductCategoryUid: Long
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 등록 <ADMIN>)
    fun postRentableProductInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductInfoInputVo
    ): RentalReservationAdminController.PostRentableProductInfoOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductInfoOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 수정 <ADMIN>)
    fun putRentableProductInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PutRentableProductInfoInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 추가 예약 가능 설정 수정 <ADMIN>)
    fun patchRentableProductInfoReservable(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoReservableInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 최소 예약 횟수 설정 수정 <ADMIN>)
    fun patchRentableProductInfoMinReservationUnitCount(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoMinReservationUnitCountInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 최대 예약 횟수 설정 수정 <ADMIN>)
    fun patchRentableProductInfoMaxReservationUnitCount(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoMaxReservationUnitCountInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 이미지 등록 <ADMIN>)
    fun postRentableProductImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductImageInputVo
    ): RentalReservationAdminController.PostRentableProductImageOutputVo? {

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductImageOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 이미지 삭제 <ADMIN>)
    fun deleteRentableProductImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductImageUid: Long
    ) {

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 대표 상품 이미지 수정 <ADMIN>)
    fun patchRentableProductInfoFrontImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoFrontImageInputVo
    ) {

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 대표 상품 이미지 설정 수정 <ADMIN>)
    fun postRentableProductStockCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductStockCategoryInputVo
    ): RentalReservationAdminController.PostRentableProductStockCategoryOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductStockCategoryOutputVo(
            1L
        )
    }


    // ----
    // (예약 상품 재고 카테고리 정보 수정 <ADMIN>)
    fun putRentableProductStockCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockCategoryUid: Long,
        inputVo: RentalReservationAdminController.PutRentableProductStockCategoryInputVo
    ) {

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (예약 상품 재고 카테고리 정보 삭제 <ADMIN>)
    fun deleteRentableProductStockCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockCategoryUid: Long
    ) {

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 재고 등록 <ADMIN>)
    fun postRentableProductStockInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductStockInfoInputVo
    ): RentalReservationAdminController.PostRentableProductStockInfoOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductStockInfoOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 재고 수정 <ADMIN>)
    fun putRentableProductStockInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockInfoUid: Long,
        inputVo: RentalReservationAdminController.PutRentableProductStockInfoInputVo
    ) {
        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 재고 추가 예약 가능 설정 수정 <ADMIN>)
    fun patchRentableProductStockInfoReservable(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockInfoReservableInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 재고 이미지 등록 <ADMIN>)
    fun postRentableProductStockImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductStockImageInputVo
    ): RentalReservationAdminController.PostRentableProductStockImageOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductStockImageOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 재고 이미지 삭제 <ADMIN>)
    fun deleteRentableProductStockImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockImageUid: Long
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 대표 상품 재고 이미지 설정 수정 <ADMIN>)
    fun patchRentableProductStockInfoFrontImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockInfoFrontImageInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 예약 정보의 결재 완료 처리 <ADMIN>)
    fun postRentableProductReservationInfoPaymentComplete(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteOutputVo {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 예약 정보의 예약 승인 처리 <ADMIN>)
    fun postRentableProductReservationInfoReservationApprove(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoReservationApproveInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoReservationApproveOutputVo {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoReservationApproveOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 예약 정보의 예약 거부 처리 <ADMIN>)
    fun postRentableProductReservationInfoReservationDeny(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoReservationDenyInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoReservationDenyOutputVo {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoReservationDenyOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 예약 정보의 예약 취소 승인 처리 <ADMIN>)
    fun postRentableProductReservationInfoReservationCancelApprove(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelApproveInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelApproveOutputVo {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelApproveOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 예약 정보의 예약 취소 거부 처리 <ADMIN>)
    fun postRentableProductReservationInfoReservationCancelDeny(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelDenyInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelDenyOutputVo {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelDenyOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 예약 정보의 환불 완료 처리 <ADMIN>)
    fun postRentableProductReservationInfoRefundComplete(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoRefundCompleteInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoRefundCompleteOutputVo {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoRefundCompleteOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 예약 정보의 환불 완료 처리 <ADMIN>)
    fun postRentableProductReservationInfoEarlyReturnComplete(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoEarlyReturnCompleteInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoEarlyReturnCompleteOutputVo {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoEarlyReturnCompleteOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 예약 상태 테이블의 상세 설명 수정 <ADMIN>)
    fun patchReservationStateChangeHistoryStateChangeDesc(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        reservationStateChangeHistoryUid: Long,
        inputVo: RentalReservationAdminController.PatchReservationStateChangeHistoryStateChangeDescInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoEarlyReturnCompleteOutputVo {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoEarlyReturnCompleteOutputVo(
            1L
        )
    }


    // ----
    // (개별 상품 예약 정보 다음 준비 예정일 수정 <ADMIN>)
    fun patchRentableProductStockReservationInfoNextReadyDatetime(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockReservationInfoNextReadyDatetimeInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
    }
}