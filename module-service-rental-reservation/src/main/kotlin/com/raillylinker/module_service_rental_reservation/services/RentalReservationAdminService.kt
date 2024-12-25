package com.raillylinker.module_service_rental_reservation.services

import com.raillylinker.module_service_rental_reservation.util_components.JwtTokenUtil
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.module_service_rental_reservation.controllers.RentalReservationAdminController
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

    private val jwtTokenUtil: JwtTokenUtil
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
        return RentalReservationAdminController.PostRentableProductCategoryOutputVo(
            1L // todo
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
        return RentalReservationAdminController.PostRentableProductInfoOutputVo(
            1L // todo
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
    // (대여 가능 상품 회수 준비 시간 설정 수정 <ADMIN>)
    fun patchRentableProductInfoPreparationMinute(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoPreparationMinuteInputVo
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
        return RentalReservationAdminController.PostRentableProductImageOutputVo(
            1L // todo
        )
    }


    // ----
    // (대여 가능 상품 이미지 삭제 <ADMIN>)
    fun deleteRentableProductImage(httpServletResponse: HttpServletResponse, authorization: String) {

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
        return RentalReservationAdminController.PostRentableProductStockCategoryOutputVo(
            1L // todo
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
        return RentalReservationAdminController.PostRentableProductStockInfoOutputVo(
            1L // todo
        )
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
}