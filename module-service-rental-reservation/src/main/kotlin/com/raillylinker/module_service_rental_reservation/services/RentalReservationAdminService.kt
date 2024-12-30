package com.raillylinker.module_service_rental_reservation.services

import com.raillylinker.module_service_rental_reservation.util_components.JwtTokenUtil
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.module_service_rental_reservation.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.module_service_rental_reservation.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.module_service_rental_reservation.controllers.RentalReservationAdminController
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductCategory
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductImage
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductInfo
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockCategory
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockImage
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockInfo
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_Native_Repository
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_Native_Repository.FindAllCategoryTreeUidListOutputVo
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories.Db1_Native_Repository.FindAllStockCategoryTreeUidListOutputVo
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
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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

                "dev13001" -> {
                    "http://127.0.0.1:13001"
                }

                else -> {
                    "http://127.0.0.1:13001"
                }
            }
        }


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (예약 상품 카테고리 정보 등록 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductCategoryInputVo
    ): RentalReservationAdminController.PostRentableProductCategoryOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val parentCategoryEntity: Db1_RaillyLinkerCompany_RentableProductCategory? =
            if (inputVo.parentRentableProductCategoryUid == null) {
                null
            } else {
                val existsCategoryEntity =
                    db1RaillyLinkerCompanyRentableProductCategoryRepository.findByUidAndRowDeleteDateStr(
                        inputVo.parentRentableProductCategoryUid,
                        "/"
                    )

                if (existsCategoryEntity == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                existsCategoryEntity
            }

        val categoryEntity = db1RaillyLinkerCompanyRentableProductCategoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductCategory(
                inputVo.categoryName,
                parentCategoryEntity
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductCategoryOutputVo(
            categoryEntity.uid!!
        )
    }


    // ----
    // (예약 상품 카테고리 정보 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun putRentableProductCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductCategoryUid: Long,
        inputVo: RentalReservationAdminController.PutRentableProductCategoryInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableCategoryEntity =
            db1RaillyLinkerCompanyRentableProductCategoryRepository.findByUidAndRowDeleteDateStr(
                rentableProductCategoryUid,
                "/"
            )

        if (rentableCategoryEntity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val parentCategoryEntity: Db1_RaillyLinkerCompany_RentableProductCategory? =
            if (inputVo.parentRentableProductCategoryUid == null) {
                null
            } else {
                val existsCategoryEntity =
                    db1RaillyLinkerCompanyRentableProductCategoryRepository.findByUidAndRowDeleteDateStr(
                        inputVo.parentRentableProductCategoryUid,
                        "/"
                    )

                if (existsCategoryEntity == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return
                }

                existsCategoryEntity
            }

        rentableCategoryEntity.categoryName = inputVo.categoryName
        rentableCategoryEntity.parentRentableProductCategory = parentCategoryEntity

        db1RaillyLinkerCompanyRentableProductCategoryRepository.save(rentableCategoryEntity)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (예약 상품 카테고리 정보 삭제 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductCategoryUid: Long
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        // 데이터 존재 여부 확인
        val rentableCategoryExists =
            db1RaillyLinkerCompanyRentableProductCategoryRepository.existsByUidAndRowDeleteDateStr(
                rentableProductCategoryUid,
                "/"
            )

        if (!rentableCategoryExists) {
            // 삭제 대상이 없으므로 204 return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 카테고리 트리 내 하위 카테고리들 모두 조회(최하위 컨테이너 우선 정렬)
        val categoryTreeUidList: List<FindAllCategoryTreeUidListOutputVo> =
            db1NativeRepository.findAllCategoryTreeUidList(
                rentableProductCategoryUid
            )

        // 카테고리 트리 순회
        for (categoryTreeUid in categoryTreeUidList) {
            // 카테고리 객체 조회
            val categoryBranch = db1RaillyLinkerCompanyRentableProductCategoryRepository.findByUidAndRowDeleteDateStr(
                categoryTreeUid.uid,
                "/"
            )

            if (categoryBranch != null) {
                // branch 카테고리를 조회하는 모든 상품들 조회
                val rentableProductList =
                    db1RaillyLinkerCompanyRentableProductInfoRepository.findAllByRentableProductCategoryAndRowDeleteDateStr(
                        categoryBranch,
                        "/"
                    )

                // branch 카테고리를 조회하는 모든 상품들에서 카테고리 해제
                for (rentableProduct in rentableProductList) {
                    rentableProduct.rentableProductCategory = null
                    db1RaillyLinkerCompanyRentableProductInfoRepository.save(
                        rentableProduct
                    )
                }

                // branch 카테고리 삭제처리
                categoryBranch.rowDeleteDateStr =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                db1RaillyLinkerCompanyRentableProductCategoryRepository.save(categoryBranch)
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 등록 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductInfoInputVo
    ): RentalReservationAdminController.PostRentableProductInfoOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        if (inputVo.minimumReservationUnitCount < 0 ||
            (inputVo.maximumReservationUnitCount != null &&
                    (inputVo.maximumReservationUnitCount < 0 ||
                            inputVo.minimumReservationUnitCount > inputVo.maximumReservationUnitCount))
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        val rentableProductCategory =
            if (inputVo.rentableProductCategoryUid == null) {
                null
            } else {
                val rentableProductCategoryEntity =
                    db1RaillyLinkerCompanyRentableProductCategoryRepository.findByUidAndRowDeleteDateStr(
                        inputVo.rentableProductCategoryUid,
                        "/"
                    )

                if (rentableProductCategoryEntity == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                rentableProductCategoryEntity
            }

        val rentableProductInfo = db1RaillyLinkerCompanyRentableProductInfoRepository.save(
            Db1_RaillyLinkerCompany_RentableProductInfo(
                inputVo.productName,
                rentableProductCategory,
                inputVo.productIntro,
                null,
                inputVo.addressCountry,
                inputVo.addressMain,
                inputVo.addressDetail,
                ZonedDateTime.parse(
                    inputVo.firstReservableDatetime,
                    DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
                ).toLocalDateTime(),
                inputVo.reservationUnitMinute,
                inputVo.minimumReservationUnitCount,
                inputVo.maximumReservationUnitCount,
                inputVo.reservationUnitPrice,
                inputVo.reservationUnitPriceCurrencyCode.name,
                inputVo.nowReservable
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductInfoOutputVo(
            rentableProductInfo.uid!!
        )
    }


    // ----
    // (대여 가능 상품 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun putRentableProductInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PutRentableProductInfoInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        if (inputVo.minimumReservationUnitCount < 0 ||
            (inputVo.maximumReservationUnitCount != null &&
                    (inputVo.maximumReservationUnitCount < 0 ||
                            inputVo.minimumReservationUnitCount > inputVo.maximumReservationUnitCount))
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return
        }

        val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
            rentableProductInfoUid,
            "/"
        )

        if (rentableProduct == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val rentableProductCategory =
            if (inputVo.rentableProductCategoryUid == null) {
                null
            } else {
                val rentableProductCategoryEntity =
                    db1RaillyLinkerCompanyRentableProductCategoryRepository.findByUidAndRowDeleteDateStr(
                        inputVo.rentableProductCategoryUid,
                        "/"
                    )

                if (rentableProductCategoryEntity == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return
                }

                rentableProductCategoryEntity
            }

        rentableProduct.updateVersionSeq += 1
        rentableProduct.productName = inputVo.productName
        rentableProduct.rentableProductCategory = rentableProductCategory
        rentableProduct.productIntro = inputVo.productIntro
        rentableProduct.addressCountry = inputVo.addressCountry
        rentableProduct.addressMain = inputVo.addressMain
        rentableProduct.addressDetail = inputVo.addressDetail
        rentableProduct.firstReservableDatetime = ZonedDateTime.parse(
            inputVo.firstReservableDatetime,
            DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
        ).toLocalDateTime()
        rentableProduct.reservationUnitMinute = inputVo.reservationUnitMinute
        rentableProduct.minimumReservationUnitCount = inputVo.minimumReservationUnitCount
        rentableProduct.maximumReservationUnitCount = inputVo.maximumReservationUnitCount
        rentableProduct.reservationUnitPrice = inputVo.reservationUnitPrice
        rentableProduct.reservationUnitPriceCurrencyCode = inputVo.reservationUnitPriceCurrencyCode.name
        rentableProduct.nowReservable = inputVo.nowReservable

        db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 삭제 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductInfo: Db1_RaillyLinkerCompany_RentableProductInfo? =
            db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductInfoUid,
                "/"
            )

        if (rentableProductInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        for (rentableProductReservationInfo in rentableProductInfo.rentableProductReservationInfoList) {
            // 예약 정보에서 고유값 null 처리
            rentableProductReservationInfo.rentableProductInfo = null
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(rentableProductReservationInfo)
        }

        for (rentableProductImage in rentableProductInfo.rentableProductImageList) {
            // 이미지를 참조하고 있던 테이블들 모두 null 처리
            for (rentableProductInfo in rentableProductImage.rentableProductInfoList) {
                rentableProductInfo.frontRentableProductImage = null
                db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProductInfo)
            }

            for (rentableProductReservationInfo in rentableProductImage.rentableProductReservationInfoList) {
                rentableProductReservationInfo.frontRentableProductImage = null
                db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(rentableProductReservationInfo)
            }

            rentableProductImage.rowDeleteDateStr =
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

            db1RaillyLinkerCompanyRentableProductImageRepository.save(
                rentableProductImage
            )
        }

        for (rentableProductStockInfo in rentableProductInfo.rentableProductStockInfoList) {
            for (rentableProductStockImage in rentableProductStockInfo.rentableProductStockImageList) {
                // 이미지 삭제
                // 이미지를 참조하고 있던 테이블들 모두 null 처리
                for (rentableProductStockInfo in rentableProductStockImage.rentableProductStockInfoList) {
                    rentableProductStockInfo.frontRentableProductStockImage = null
                    db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfo)
                }

                rentableProductStockImage.rowDeleteDateStr =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

                db1RaillyLinkerCompanyRentableProductStockImageRepository.save(
                    rentableProductStockImage
                )
            }

            for (rentableProductStockReservationInfo in rentableProductStockInfo.rentableProductStockReservationInfoList) {
                // 상품 예약 내역 삭제
                rentableProductStockReservationInfo.rowDeleteDateStr =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

                db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.save(
                    rentableProductStockReservationInfo
                )
            }

            // 테이블 삭제 처리
            rentableProductStockInfo.rowDeleteDateStr =
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        }

        // 테이블 삭제 처리
        rentableProductInfo.rowDeleteDateStr =
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 추가 예약 가능 설정 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductInfoReservable(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoReservableInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
            rentableProductInfoUid,
            "/"
        )

        if (rentableProduct == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        rentableProduct.nowReservable = inputVo.nowReservable

        db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 최소 예약 횟수 설정 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductInfoMinReservationUnitCount(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoMinReservationUnitCountInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
            rentableProductInfoUid,
            "/"
        )

        if (rentableProduct == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (inputVo.minimumReservationUnitCount < 0 ||
            (rentableProduct.maximumReservationUnitCount != null &&
                    inputVo.minimumReservationUnitCount > rentableProduct.maximumReservationUnitCount!!)
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        rentableProduct.minimumReservationUnitCount = inputVo.minimumReservationUnitCount

        db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 최대 예약 횟수 설정 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductInfoMaxReservationUnitCount(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoMaxReservationUnitCountInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
            rentableProductInfoUid,
            "/"
        )

        if (rentableProduct == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (inputVo.maximumReservationUnitCount != null &&
            (inputVo.maximumReservationUnitCount < 0 ||
                    rentableProduct.minimumReservationUnitCount > inputVo.maximumReservationUnitCount)
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        rentableProduct.maximumReservationUnitCount = inputVo.maximumReservationUnitCount

        db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 이미지 등록 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductImageInputVo
    ): RentalReservationAdminController.PostRentableProductImageOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
            inputVo.rentableProductInfoUid,
            "/"
        )

        if (rentableProduct == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 저장된 상품 이미지 파일을 다운로드 할 수 있는 URL
        val savedProductImageUrl: String

        // 상품 이미지 파일 저장

        //----------------------------------------------------------------------------------------------------------
        // 상품 이미지를 서버 스토리지에 저장할 때 사용하는 방식
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path =
            Paths.get("./by_product_files/service_rental_reservation/rentable_product/images")
                .toAbsolutePath().normalize()

        // 파일 저장 기본 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 원본 파일명(with suffix)
        val multiPartFileNameString = StringUtils.cleanPath(inputVo.thumbnailImage.originalFilename!!)

        // 파일 확장자 구분 위치
        val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

        // 확장자가 없는 파일명
        val fileNameWithOutExtension: String
        // 확장자
        val fileExtension: String

        if (fileExtensionSplitIdx == -1) {
            fileNameWithOutExtension = multiPartFileNameString
            fileExtension = ""
        } else {
            fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
            fileExtension =
                multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
        }

        val savedFileName = "${fileNameWithOutExtension}(${
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        }).$fileExtension"

        // multipartFile 을 targetPath 에 저장
        inputVo.thumbnailImage.transferTo(
            // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
            saveDirectoryPath.resolve(savedFileName).normalize()
        )

        savedProductImageUrl = "${externalAccessAddress}/rental-reservation-admin/product-image/$savedFileName"
        //----------------------------------------------------------------------------------------------------------

        val productImage = db1RaillyLinkerCompanyRentableProductImageRepository.save(
            Db1_RaillyLinkerCompany_RentableProductImage(
                rentableProduct,
                savedProductImageUrl
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductImageOutputVo(
            productImage.uid!!,
            savedProductImageUrl
        )
    }


    // ----
    // (대여 가능 상품 이미지 파일 다운받기)
    fun getProductImageFile(
        httpServletResponse: HttpServletResponse,
        fileName: String
    ): ResponseEntity<Resource>? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명
        val serverFilePathObject =
            Paths.get("$projectRootAbsolutePathString/by_product_files/service_rental_reservation/rentable_product/images/$fileName")

        when {
            Files.isDirectory(serverFilePathObject) -> {
                // 파일이 디렉토리일때
                httpServletResponse.status = HttpStatus.NOT_FOUND.value()
                return null
            }

            Files.notExists(serverFilePathObject) -> {
                // 파일이 없을 때
                httpServletResponse.status = HttpStatus.NOT_FOUND.value()
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ResponseEntity<Resource>(
            InputStreamResource(Files.newInputStream(serverFilePathObject)),
            HttpHeaders().apply {
                this.contentDisposition = ContentDisposition.builder("attachment")
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build()
                this.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(serverFilePathObject))
            },
            HttpStatus.OK
        )
    }


    // ----
    // (대여 가능 상품 이미지 삭제 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductImageUid: Long
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductImage = db1RaillyLinkerCompanyRentableProductImageRepository.findByUidAndRowDeleteDateStr(
            rentableProductImageUid,
            "/"
        )

        if (rentableProductImage == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 이미지를 참조하고 있던 테이블들 모두 null 처리
        for (rentableProductInfo in rentableProductImage.rentableProductInfoList) {
            rentableProductInfo.frontRentableProductImage = null
            db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProductInfo)
        }

        for (rentableProductReservationInfo in rentableProductImage.rentableProductReservationInfoList) {
            rentableProductReservationInfo.frontRentableProductImage = null
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(rentableProductReservationInfo)
        }

        rentableProductImage.rowDeleteDateStr =
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

        db1RaillyLinkerCompanyRentableProductImageRepository.save(
            rentableProductImage
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 대표 상품 이미지 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductInfoFrontImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductInfoFrontImageInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
            rentableProductInfoUid,
            "/"
        )

        if (rentableProduct == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (inputVo.rentableProductImageUid == null) {
            rentableProduct.frontRentableProductImage = null
            db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)
            httpServletResponse.status = HttpStatus.OK.value()
            return
        }

        val productImage = db1RaillyLinkerCompanyRentableProductImageRepository.findByUidAndRowDeleteDateStr(
            inputVo.rentableProductImageUid,
            "/"
        )

        if (productImage == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        rentableProduct.frontRentableProductImage = productImage

        db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 대표 상품 이미지 설정 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductStockCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductStockCategoryInputVo
    ): RentalReservationAdminController.PostRentableProductStockCategoryOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val parentCategoryEntity: Db1_RaillyLinkerCompany_RentableProductStockCategory? =
            if (inputVo.parentRentableProductStockCategoryUid == null) {
                null
            } else {
                val existsCategoryEntity =
                    db1RaillyLinkerCompanyRentableProductStockCategoryRepository.findByUidAndRowDeleteDateStr(
                        inputVo.parentRentableProductStockCategoryUid,
                        "/"
                    )

                if (existsCategoryEntity == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                existsCategoryEntity
            }

        val categoryEntity = db1RaillyLinkerCompanyRentableProductStockCategoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductStockCategory(
                inputVo.categoryName,
                parentCategoryEntity
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductStockCategoryOutputVo(
            categoryEntity.uid!!
        )
    }


    // ----
    // (예약 상품 재고 카테고리 정보 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun putRentableProductStockCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockCategoryUid: Long,
        inputVo: RentalReservationAdminController.PutRentableProductStockCategoryInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableCategoryEntity =
            db1RaillyLinkerCompanyRentableProductStockCategoryRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockCategoryUid,
                "/"
            )

        if (rentableCategoryEntity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val parentCategoryEntity: Db1_RaillyLinkerCompany_RentableProductStockCategory? =
            if (inputVo.parentRentableProductStockCategoryUid == null) {
                null
            } else {
                val existsCategoryEntity =
                    db1RaillyLinkerCompanyRentableProductStockCategoryRepository.findByUidAndRowDeleteDateStr(
                        inputVo.parentRentableProductStockCategoryUid,
                        "/"
                    )

                if (existsCategoryEntity == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return
                }

                existsCategoryEntity
            }

        rentableCategoryEntity.categoryName = inputVo.categoryName
        rentableCategoryEntity.parentRentableProductStockCategory = parentCategoryEntity

        db1RaillyLinkerCompanyRentableProductStockCategoryRepository.save(rentableCategoryEntity)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (예약 상품 재고 카테고리 정보 삭제 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductStockCategory(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockCategoryUid: Long
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        // 데이터 존재 여부 확인
        val rentableCategoryExists =
            db1RaillyLinkerCompanyRentableProductStockCategoryRepository.existsByUidAndRowDeleteDateStr(
                rentableProductStockCategoryUid,
                "/"
            )

        if (!rentableCategoryExists) {
            // 삭제 대상이 없으므로 204 return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 카테고리 트리 내 하위 카테고리들 모두 조회(최하위 컨테이너 우선 정렬)
        val categoryTreeUidList: List<FindAllStockCategoryTreeUidListOutputVo> =
            db1NativeRepository.findAllStockCategoryTreeUidList(
                rentableProductStockCategoryUid
            )

        // 카테고리 트리 순회
        for (categoryTreeUid in categoryTreeUidList) {
            // 카테고리 객체 조회
            val categoryBranch =
                db1RaillyLinkerCompanyRentableProductStockCategoryRepository.findByUidAndRowDeleteDateStr(
                    categoryTreeUid.uid,
                    "/"
                )

            if (categoryBranch != null) {
                // branch 카테고리를 조회하는 모든 상품들 조회
                val rentableProductStockList =
                    db1RaillyLinkerCompanyRentableProductStockInfoRepository.findAllByRentableProductStockCategoryAndRowDeleteDateStr(
                        categoryBranch,
                        "/"
                    )

                // branch 카테고리를 조회하는 모든 상품들에서 카테고리 해제
                for (rentableProductStock in rentableProductStockList) {
                    rentableProductStock.rentableProductStockCategory = null
                    db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(
                        rentableProductStock
                    )
                }

                // branch 카테고리 삭제처리
                categoryBranch.rowDeleteDateStr =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                db1RaillyLinkerCompanyRentableProductStockCategoryRepository.save(categoryBranch)
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 재고 등록 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductStockInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductStockInfoInputVo
    ): RentalReservationAdminController.PostRentableProductStockInfoOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductInfo =
            db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                inputVo.rentableProductInfoUid,
                "/"
            )

        if (rentableProductInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val categoryTable = if (inputVo.rentableProductStockCategoryUid == null) {
            null
        } else {
            val categoryEntity =
                db1RaillyLinkerCompanyRentableProductStockCategoryRepository.findByUidAndRowDeleteDateStr(
                    inputVo.rentableProductStockCategoryUid,
                    "/"
                )

            if (categoryEntity == null) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
            categoryEntity
        }


        val rentableProductStockInfo =
            db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(
                Db1_RaillyLinkerCompany_RentableProductStockInfo(
                    categoryTable,
                    inputVo.productDesc,
                    null,
                    ZonedDateTime.parse(
                        inputVo.firstRentableDatetime,
                        DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
                    ).toLocalDateTime(),
                    if (inputVo.lastRentableDatetime == null) {
                        null
                    } else {
                        ZonedDateTime.parse(
                            inputVo.lastRentableDatetime,
                            DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
                        ).toLocalDateTime()
                    },
                    rentableProductInfo,
                    inputVo.nowReservable
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductStockInfoOutputVo(
            rentableProductStockInfo.uid!!
        )
    }


    // ----
    // (대여 가능 상품 재고 삭제 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductStockInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockInfoUid: Long
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockInfo =
            db1RaillyLinkerCompanyRentableProductStockInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockInfoUid,
                "/"
            )

        if (rentableProductStockInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        for (rentableProductStockImage in rentableProductStockInfo.rentableProductStockImageList) {
            // 이미지 삭제
            // 이미지를 참조하고 있던 테이블들 모두 null 처리
            for (rentableProductStockInfo in rentableProductStockImage.rentableProductStockInfoList) {
                rentableProductStockInfo.frontRentableProductStockImage = null
                db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfo)
            }

            rentableProductStockImage.rowDeleteDateStr =
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

            db1RaillyLinkerCompanyRentableProductStockImageRepository.save(
                rentableProductStockImage
            )
        }

        for (rentableProductStockReservationInfo in rentableProductStockInfo.rentableProductStockReservationInfoList) {
            // 상품 예약 내역 삭제
            rentableProductStockReservationInfo.rowDeleteDateStr =
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

            db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.save(
                rentableProductStockReservationInfo
            )
        }

        // 테이블 삭제 처리
        rentableProductStockInfo.rowDeleteDateStr =
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 재고 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun putRentableProductStockInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockInfoUid: Long,
        inputVo: RentalReservationAdminController.PutRentableProductStockInfoInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockInfo =
            db1RaillyLinkerCompanyRentableProductStockInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockInfoUid,
                "/"
            )

        if (rentableProductStockInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val rentableProductInfo =
            db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                inputVo.rentableProductInfoUid,
                "/"
            )

        if (rentableProductInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        val categoryTable = if (inputVo.rentableProductStockCategoryUid == null) {
            null
        } else {
            val categoryEntity =
                db1RaillyLinkerCompanyRentableProductStockCategoryRepository.findByUidAndRowDeleteDateStr(
                    inputVo.rentableProductStockCategoryUid,
                    "/"
                )

            if (categoryEntity == null) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "3")
                return
            }
            categoryEntity
        }

        rentableProductStockInfo.rentableProductInfo = rentableProductInfo
        rentableProductStockInfo.rentableProductStockCategory = categoryTable
        rentableProductStockInfo.productDesc = inputVo.productDesc
        rentableProductStockInfo.firstRentableDatetime = ZonedDateTime.parse(
            inputVo.firstRentableDatetime,
            DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
        ).toLocalDateTime()
        rentableProductStockInfo.lastRentableDatetime = if (inputVo.lastRentableDatetime == null) {
            null
        } else {
            ZonedDateTime.parse(
                inputVo.lastRentableDatetime,
                DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
            ).toLocalDateTime()
        }
        rentableProductStockInfo.nowReservable = inputVo.nowReservable

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 재고 추가 예약 가능 설정 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductStockInfoReservable(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockInfoReservableInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockInfo =
            db1RaillyLinkerCompanyRentableProductStockInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockInfoUid,
                "/"
            )

        if (rentableProductStockInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        rentableProductStockInfo.nowReservable = inputVo.nowReservable

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 재고 이미지 등록 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductStockImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: RentalReservationAdminController.PostRentableProductStockImageInputVo
    ): RentalReservationAdminController.PostRentableProductStockImageOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableStockProduct =
            db1RaillyLinkerCompanyRentableProductStockInfoRepository.findByUidAndRowDeleteDateStr(
                inputVo.rentableProductInfoStockUid,
                "/"
            )

        if (rentableStockProduct == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 저장된 상품 이미지 파일을 다운로드 할 수 있는 URL
        val savedProductStockImageUrl: String

        // 상품 이미지 파일 저장

        //----------------------------------------------------------------------------------------------------------
        // 상품 이미지를 서버 스토리지에 저장할 때 사용하는 방식
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path =
            Paths.get("./by_product_files/service_rental_reservation/rentable_product_stock/images")
                .toAbsolutePath().normalize()

        // 파일 저장 기본 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 원본 파일명(with suffix)
        val multiPartFileNameString = StringUtils.cleanPath(inputVo.thumbnailImage.originalFilename!!)

        // 파일 확장자 구분 위치
        val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

        // 확장자가 없는 파일명
        val fileNameWithOutExtension: String
        // 확장자
        val fileExtension: String

        if (fileExtensionSplitIdx == -1) {
            fileNameWithOutExtension = multiPartFileNameString
            fileExtension = ""
        } else {
            fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
            fileExtension =
                multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
        }

        val savedFileName = "${fileNameWithOutExtension}(${
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        }).$fileExtension"

        // multipartFile 을 targetPath 에 저장
        inputVo.thumbnailImage.transferTo(
            // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
            saveDirectoryPath.resolve(savedFileName).normalize()
        )

        savedProductStockImageUrl =
            "${externalAccessAddress}/rental-reservation-admin/product-stock-image/$savedFileName"
        //----------------------------------------------------------------------------------------------------------

        val productStockImage = db1RaillyLinkerCompanyRentableProductStockImageRepository.save(
            Db1_RaillyLinkerCompany_RentableProductStockImage(
                rentableStockProduct,
                savedProductStockImageUrl
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductStockImageOutputVo(
            productStockImage.uid!!,
            savedProductStockImageUrl
        )
    }


    // ----
    // (대여 가능 상품 재고 이미지 파일 다운받기)
    fun getProductStockImageFile(
        httpServletResponse: HttpServletResponse,
        fileName: String
    ): ResponseEntity<Resource>? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명
        val serverFilePathObject =
            Paths.get("$projectRootAbsolutePathString/by_product_files/service_rental_reservation/rentable_product_stock/images/$fileName")

        when {
            Files.isDirectory(serverFilePathObject) -> {
                // 파일이 디렉토리일때
                httpServletResponse.status = HttpStatus.NOT_FOUND.value()
                return null
            }

            Files.notExists(serverFilePathObject) -> {
                // 파일이 없을 때
                httpServletResponse.status = HttpStatus.NOT_FOUND.value()
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ResponseEntity<Resource>(
            InputStreamResource(Files.newInputStream(serverFilePathObject)),
            HttpHeaders().apply {
                this.contentDisposition = ContentDisposition.builder("attachment")
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build()
                this.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(serverFilePathObject))
            },
            HttpStatus.OK
        )
    }


    // ----
    // (대여 가능 상품 재고 이미지 삭제 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductStockImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockImageUid: Long
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockImage =
            db1RaillyLinkerCompanyRentableProductStockImageRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockImageUid,
                "/"
            )

        if (rentableProductStockImage == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 이미지를 참조하고 있던 테이블들 모두 null 처리
        for (rentableProductStockInfo in rentableProductStockImage.rentableProductStockInfoList) {
            rentableProductStockInfo.frontRentableProductStockImage = null
            db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfo)
        }

        rentableProductStockImage.rowDeleteDateStr =
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

        db1RaillyLinkerCompanyRentableProductStockImageRepository.save(
            rentableProductStockImage
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 대표 상품 재고 이미지 설정 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductStockInfoFrontImage(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockInfoFrontImageInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStock =
            db1RaillyLinkerCompanyRentableProductStockInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockInfoUid,
                "/"
            )

        if (rentableProductStock == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (inputVo.rentableProductStockImageUid == null) {
            rentableProductStock.frontRentableProductStockImage = null
            db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStock)
            httpServletResponse.status = HttpStatus.OK.value()
            return
        }

        val productStockImage = db1RaillyLinkerCompanyRentableProductStockImageRepository.findByUidAndRowDeleteDateStr(
            inputVo.rentableProductStockImageUid,
            "/"
        )

        if (productStockImage == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        rentableProductStock.frontRentableProductStockImage = productStockImage

        db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStock)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (대여 가능 상품 예약 정보의 결재 완료 처리 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductReservationInfoPaymentComplete(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteOutputVo {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        // todo 상태 변경 가능 여부 확인

        // todo 결재 정보에 완료 처리

        // todo 예약 정보 히스토리 테이블 완료 정보 추가

        // todo
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteOutputVo(
            1L
        )
    }


    // ----
    // (대여 가능 상품 예약 정보의 예약 승인 처리 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
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