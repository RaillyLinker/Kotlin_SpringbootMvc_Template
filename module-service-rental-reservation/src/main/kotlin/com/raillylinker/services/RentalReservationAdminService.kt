package com.raillylinker.services

import com.raillylinker.util_components.JwtTokenUtil
import com.raillylinker.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.controllers.RentalReservationAdminController
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductCategory
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductImage
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductInfo
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockCategory
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockImage
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockInfo
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_Native_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_Native_Repository.FindAllCategoryTreeUidListOutputVo
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_Native_Repository.FindAllStockCategoryTreeUidListOutputVo
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
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberEmail_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberPhone_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMemberProfile_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMember_Repository
import com.raillylinker.redis_map_components.redis1_main.Redis1_Lock_RentableProductInfo
import com.raillylinker.redis_map_components.redis1_main.Redis1_Lock_RentableProductStockEarlyReturn
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
    private val db1RaillyLinkerCompanyTotalAuthMemberProfileRepository: Db1_RaillyLinkerCompany_TotalAuthMemberProfile_Repository,
    private val db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository: Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory_Repository,

    private val redis1LockRentableProductInfo: Redis1_Lock_RentableProductInfo,
    private val redis1LockRentableProductStockEarlyReturn: Redis1_Lock_RentableProductStockEarlyReturn
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
                parentCategoryEntity,
                inputVo.categoryName
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

        if (inputVo.customerPaymentDeadlineMinute > inputVo.paymentCheckDeadlineMinute) {
            // 결제 통보 기한이 결제 승인 기한보다 클 경우 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        if (inputVo.paymentCheckDeadlineMinute > inputVo.approvalDeadlineMinute) {
            // 결제 승인 기한이 예약 승인 기한보다 클 경우 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

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
                rentableProductCategory,
                null,
                inputVo.productName,
                inputVo.productIntro,
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
                inputVo.nowReservable,
                inputVo.customerPaymentDeadlineMinute,
                inputVo.paymentCheckDeadlineMinute,
                inputVo.approvalDeadlineMinute,
                inputVo.cancelDeadlineMinute
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductInfoOutputVo(
            rentableProductInfo.uid!!
        )
    }


    // ----
    // (대여 가능 상품 수정 <ADMIN>)
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
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

        if (inputVo.customerPaymentDeadlineMinute > inputVo.paymentCheckDeadlineMinute) {
            // 결제 통보 기한이 결제 승인 기한보다 클 경우 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return
        }

        if (inputVo.paymentCheckDeadlineMinute > inputVo.approvalDeadlineMinute) {
            // 결제 승인 기한이 예약 승인 기한보다 클 경우 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return
        }

        if (inputVo.minimumReservationUnitCount < 0 ||
            (inputVo.maximumReservationUnitCount != null &&
                    (inputVo.maximumReservationUnitCount < 0 ||
                            inputVo.minimumReservationUnitCount > inputVo.maximumReservationUnitCount))
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return
        }

        // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
        redis1LockRentableProductInfo.tryLockRepeat<Unit>(
            "$rentableProductInfoUid",
            7000L,
            {
                val rentableProduct =
                    db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                        rentableProductInfoUid,
                        "/"
                    )

                if (rentableProduct == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
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
                            return@tryLockRepeat
                        }

                        rentableProductCategoryEntity
                    }

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
                rentableProduct.customerPaymentDeadlineMinute = inputVo.customerPaymentDeadlineMinute
                rentableProduct.paymentCheckDeadlineMinute = inputVo.paymentCheckDeadlineMinute
                rentableProduct.approvalDeadlineMinute = inputVo.approvalDeadlineMinute
                rentableProduct.cancelDeadlineMinute = inputVo.cancelDeadlineMinute
                rentableProduct.versionSeq += 1

                db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

                httpServletResponse.status = HttpStatus.OK.value()
                return@tryLockRepeat
            }
        )
    }


    // ----
    // (대여 가능 상품 삭제 <ADMIN>)
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
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

        redis1LockRentableProductInfo.tryLockRepeat(
            "$rentableProductInfoUid",
            7000L,
            {
                val rentableProductInfo: Db1_RaillyLinkerCompany_RentableProductInfo? =
                    db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                        rentableProductInfoUid,
                        "/"
                    )

                if (rentableProductInfo == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                for (rentableProductReservationInfo in rentableProductInfo.rentableProductReservationInfoList) {
                    // 예약 정보에서 고유값 null 처리
                    rentableProductReservationInfo.rentableProductInfo = null
                    db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(rentableProductReservationInfo)
                }

                for (rentableProductImage in rentableProductInfo.rentableProductImageList) {
                    // 이미지를 참조하고 있던 테이블들 모두 null 처리
                    for (rentableProductInfoEntity in rentableProductImage.rentableProductInfoList) {
                        rentableProductInfoEntity.frontRentableProductImage = null
                        db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProductInfoEntity)
                    }

                    for (rentableProductReservationInfo in rentableProductImage.rentableProductReservationInfoList) {
                        rentableProductReservationInfo.frontRentableProductImage = null
                        db1RaillyLinkerCompanyRentableProductReservationInfoRepository.save(
                            rentableProductReservationInfo
                        )
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
                        for (rentableProductStockInfoEntity in rentableProductStockImage.rentableProductStockInfoList) {
                            rentableProductStockInfoEntity.frontRentableProductStockImage = null
                            db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfoEntity)
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
                    db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfo)
                }

                // 테이블 삭제 처리
                rentableProductInfo.rowDeleteDateStr =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProductInfo)

                httpServletResponse.status = HttpStatus.OK.value()
                return@tryLockRepeat
            }
        )
    }


    // ----
    // (대여 가능 상품 추가 예약 가능 설정 수정 <ADMIN>)
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
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

        redis1LockRentableProductInfo.tryLockRepeat(
            "$rentableProductInfoUid",
            7000L,
            {
                val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                    rentableProductInfoUid,
                    "/"
                )

                if (rentableProduct == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                rentableProduct.nowReservable = inputVo.nowReservable

                db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

                httpServletResponse.status = HttpStatus.OK.value()
                return@tryLockRepeat
            }
        )
    }


    // ----
    // (대여 가능 상품 최소 예약 횟수 설정 수정 <ADMIN>)
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
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

        redis1LockRentableProductInfo.tryLockRepeat(
            "$rentableProductInfoUid",
            7000L,
            {
                val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                    rentableProductInfoUid,
                    "/"
                )

                if (rentableProduct == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                if (inputVo.minimumReservationUnitCount < 0 ||
                    (rentableProduct.maximumReservationUnitCount != null &&
                            inputVo.minimumReservationUnitCount > rentableProduct.maximumReservationUnitCount!!)
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return@tryLockRepeat
                }

                rentableProduct.minimumReservationUnitCount = inputVo.minimumReservationUnitCount

                db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

                httpServletResponse.status = HttpStatus.OK.value()
                return@tryLockRepeat
            }
        )
    }


    // ----
    // (대여 가능 상품 최대 예약 횟수 설정 수정 <ADMIN>)
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
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

        redis1LockRentableProductInfo.tryLockRepeat(
            "$rentableProductInfoUid",
            7000L,
            {
                val rentableProduct = db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                    rentableProductInfoUid,
                    "/"
                )

                if (rentableProduct == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                if (inputVo.maximumReservationUnitCount != null &&
                    (inputVo.maximumReservationUnitCount < 0 ||
                            rentableProduct.minimumReservationUnitCount > inputVo.maximumReservationUnitCount)
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return@tryLockRepeat
                }

                rentableProduct.maximumReservationUnitCount = inputVo.maximumReservationUnitCount

                db1RaillyLinkerCompanyRentableProductInfoRepository.save(rentableProduct)

                httpServletResponse.status = HttpStatus.OK.value()
                return@tryLockRepeat
            }
        )
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
    // (예약 상품 재고 카테고리 정보 등록 <ADMIN>)
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
                parentCategoryEntity,
                inputVo.categoryName
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
                    null,
                    rentableProductInfo,
                    inputVo.productDesc,
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
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
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

        redis1LockRentableProductInfo.tryLockRepeat(
            "${rentableProductStockInfo.rentableProductInfo.uid}",
            7000L,
            {
                for (rentableProductStockImage in rentableProductStockInfo.rentableProductStockImageList) {
                    // 이미지 삭제
                    // 이미지를 참조하고 있던 테이블들 모두 null 처리
                    for (rentableProductStockInfoEntity in rentableProductStockImage.rentableProductStockInfoList) {
                        rentableProductStockInfoEntity.frontRentableProductStockImage = null
                        db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfoEntity)
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

                db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfo)

                httpServletResponse.status = HttpStatus.OK.value()
            }
        )
    }


    // ----
    // (대여 가능 상품 재고 수정 <ADMIN>)
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
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

        redis1LockRentableProductInfo.tryLockRepeat(
            "${rentableProductStockInfo.rentableProductInfo.uid}",
            7000L, {
                val rentableProductInfo =
                    db1RaillyLinkerCompanyRentableProductInfoRepository.findByUidAndRowDeleteDateStr(
                        inputVo.rentableProductInfoUid,
                        "/"
                    )

                if (rentableProductInfo == null) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return@tryLockRepeat
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
                        return@tryLockRepeat
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

                db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfo)

                httpServletResponse.status = HttpStatus.OK.value()
            }
        )
    }


    // ----
    // (대여 가능 상품 재고 추가 예약 가능 설정 수정 <ADMIN>)
    // rentableProductInfoUid 관련 공유 락 처리 (예약하기 시점에 예약 정보에 영향을 끼치는 데이터 안정화)
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

        redis1LockRentableProductInfo.tryLockRepeat(
            "${rentableProductStockInfo.rentableProductInfo.uid}",
            7000L,
            {
                rentableProductStockInfo.nowReservable = inputVo.nowReservable
                db1RaillyLinkerCompanyRentableProductStockInfoRepository.save(rentableProductStockInfo)

                httpServletResponse.status = HttpStatus.OK.value()
            }
        )
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
    // (대여 가능 상품 예약 정보의 예약 승인 처리 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductReservationInfoReservationApprove(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoReservationApproveInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoReservationApproveOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )
        // rentableProductReservationInfoUid 정보 존재 여부 확인
        val rentableProductReservationInfo =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductReservationInfoUid,
                "/"
            )

        if (rentableProductReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 예약 상태 확인
        val nowDatetime = LocalDateTime.now()

        if (nowDatetime.isAfter(rentableProductReservationInfo.reservationApprovalDeadlineDatetime)) {
            // 예약 승인 기한을 넘김
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductReservationInfo,
                "/"
            )

        var notPaid = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                4 -> {
                    // 예약 취소 승인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return null
                }

                2 -> {
                    // 예약 신청 거부 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "4")
                    return null
                }

                1 -> {
                    // 예약 승인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "5")
                    return null
                }

                0 -> {
                    notPaid = false
                }
            }
        }

        if (notPaid && nowDatetime.isAfter(rentableProductReservationInfo.paymentCheckDeadlineDatetime)) {
            // 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일) -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "6")
            return null
        }

        // 예약 히스토리에 정보 기입
        val newReservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                    rentableProductReservationInfo,
                    1,
                    "관리자 예약 승인"
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductReservationInfoReservationApproveOutputVo(
            newReservationStateChangeHistory.uid!!
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
    ): RentalReservationAdminController.PostRentableProductReservationInfoReservationDenyOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )
        // rentableProductReservationInfoUid 정보 존재 여부 확인
        val rentableProductReservationInfo =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductReservationInfoUid,
                "/"
            )

        if (rentableProductReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 상태 확인
        val nowDatetime = LocalDateTime.now()

        if (nowDatetime.isAfter(rentableProductReservationInfo.reservationApprovalDeadlineDatetime)) {
            // 예약 승인 기한을 넘김
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductReservationInfo,
                "/"
            )

        var notPaid = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                4 -> {
                    // 예약 취소 승인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return null
                }

                2 -> {
                    // 예약 신청 거부 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "4")
                    return null
                }

                1 -> {
                    // 예약 승인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "5")
                    return null
                }

                0 -> {
                    notPaid = false
                }
            }
        }

        if (notPaid && nowDatetime.isAfter(rentableProductReservationInfo.paymentCheckDeadlineDatetime)) {
            // 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일) -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "6")
            return null
        }

        // 예약 히스토리에 정보 기입
        val newReservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                    rentableProductReservationInfo,
                    2,
                    "관리자 예약 거부"
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductReservationInfoReservationDenyOutputVo(
            newReservationStateChangeHistory.uid!!
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
    ): RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelApproveOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )
        // rentableProductReservationInfoUid 정보 존재 여부 확인
        val rentableProductReservationInfo =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductReservationInfoUid,
                "/"
            )

        if (rentableProductReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 상태 확인
        val nowDatetime = LocalDateTime.now()

        if (nowDatetime.isAfter(rentableProductReservationInfo.rentalStartDatetime)) {
            // 대여 시작 기한 초과 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductReservationInfo,
                "/"
            )

        if (historyList.isNotEmpty()) {
            // 결제 대기 상태라면 그냥 넘어가고, 아니라면 검증 계속(원래라면 결제 대기 상태에 취소 요청시 자동으로 취소 승인이 되어 있어야 함.)
            var notPaid = true
            var noRequestCancel = true
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
                        // 예약 취소 승인 내역 있음 -> return
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "3")
                        return null
                    }

                    3 -> {
                        // 예약 취소 신청
                        noRequestCancel = false
                        if (notRequestCancelDenyLatest) {
                            // 예약 취소 신청 내역이 최신인지
                            notRequestCancelLatest = false
                        }
                    }

                    2 -> {
                        // 예약 신청 거부 내역 있음 -> return
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "4")
                        return null
                    }

                    0 -> {
                        // 관리자 결제 확인
                        notPaid = false
                    }
                }
            }

            if (notPaid && nowDatetime.isAfter(rentableProductReservationInfo.paymentCheckDeadlineDatetime)) {
                // 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일) -> return
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "5")
                return null
            }

            if (noRequestCancel) {
                // 예약 취소 신청 내역이 없음 -> return
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "6")
                return null
            }

            if (!notRequestCancelDenyLatest && notRequestCancelLatest) {
                // 기존 예약 취소 신청에 대한 예약 취소 거부 상태입니다. -> return
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "7")
                return null
            }
        }

        // 예약 히스토리에 정보 기입
        val newReservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                    rentableProductReservationInfo,
                    4,
                    "관리자 취소 승인"
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelApproveOutputVo(
            newReservationStateChangeHistory.uid!!
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
    ): RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelDenyOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )
        // rentableProductReservationInfoUid 정보 존재 여부 확인
        val rentableProductReservationInfo =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductReservationInfoUid,
                "/"
            )

        if (rentableProductReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 상태 확인
        val nowDatetime = LocalDateTime.now()

        if (nowDatetime.isAfter(rentableProductReservationInfo.rentalStartDatetime)) {
            // 대여 시작 기한 초과 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductReservationInfo,
                "/"
            )

        if (historyList.isEmpty()) {
            // 결제 대기 상태입니다.(원래라면 취소 요청시 취소 승인이 자동으로 되어야 함.)
            // 취소 승인 처리를 지금이라도 하기
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                    rentableProductReservationInfo,
                    4,
                    "관리자 취소 승인"
                )
            )

            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        var notPaid = true
        var noRequestCancel = true
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
                    // 예약 취소 승인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return null
                }

                3 -> {
                    // 예약 취소 신청
                    noRequestCancel = false
                    if (notRequestCancelDenyLatest) {
                        // 예약 취소 신청 내역이 최신인지
                        notRequestCancelLatest = false
                    }
                }

                2 -> {
                    // 예약 신청 거부 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "4")
                    return null
                }

                0 -> {
                    // 관리자 결제 확인
                    notPaid = false
                }
            }
        }

        if (notPaid && nowDatetime.isAfter(rentableProductReservationInfo.paymentCheckDeadlineDatetime)) {
            // 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일) -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return null
        }

        if (noRequestCancel) {
            // 예약 취소 신청 내역이 없음 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "6")
            return null
        }

        if (!notRequestCancelDenyLatest && notRequestCancelLatest) {
            // 기존 예약 취소 신청에 대한 예약 취소 거부 상태입니다. -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "7")
            return null
        }

        // 예약 히스토리에 정보 기입
        val newReservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                    rentableProductReservationInfo,
                    5,
                    "관리자 취소 거부"
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductReservationInfoReservationCancelDenyOutputVo(
            newReservationStateChangeHistory.uid!!
        )
    }


    // ----
    // (대여 가능 상품 예약 정보의 결제 확인 처리 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postRentableProductReservationInfoPaymentComplete(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteInputVo
    ): RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )
        // rentableProductReservationInfoUid 정보 존재 여부 확인
        val rentableProductReservationInfo =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductReservationInfoUid,
                "/"
            )

        if (rentableProductReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 상태 확인
        val nowDatetime = LocalDateTime.now()

        if (nowDatetime.isAfter(rentableProductReservationInfo.paymentCheckDeadlineDatetime)) {
            // 결제 확인 기한 초과 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductReservationInfo,
                "/"
            )
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                0 -> {
                    // 결제 확인 내역 있음 -> return
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return null
                }
            }
        }

        // 예약 히스토리에 정보 기입
        val newReservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                    rentableProductReservationInfo,
                    0,
                    "관리자 결제 확인"
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductReservationInfoPaymentCompleteOutputVo(
            newReservationStateChangeHistory.uid!!
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
    ): RentalReservationAdminController.PostRentableProductReservationInfoRefundCompleteOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )
        // rentableProductReservationInfoUid 정보 존재 여부 확인
        val rentableProductReservationInfo =
            db1RaillyLinkerCompanyRentableProductReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductReservationInfoUid,
                "/"
            )

        if (rentableProductReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 상태 확인
        val historyList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductReservationInfo,
                "/"
            )

        if (historyList.isEmpty()) {
            // 결제 대기 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        var notPaid = true
        var noCancelRequest = true
        var noRequestDeny = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                6 -> {
                    // 이미 환불됨
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "5")
                    return null
                }

                4 -> {
                    // 예약 취소 승인
                    noCancelRequest = false
                }

                2 -> {
                    // 예약 거부
                    noRequestDeny = false
                }

                0 -> {
                    // 결제 확인
                    notPaid = false
                }
            }
        }

        if (notPaid) {
            // 결제 확인 내역이 없음 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        if (noCancelRequest && noRequestDeny) {
            // 예약 취소 승인 내역이 없고 예약 신청 거부 내역이 없음 -> return
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        // 예약 히스토리에 정보 기입
        val newReservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(
                Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
                    rentableProductReservationInfo,
                    6,
                    "관리자 환불 완료"
                )
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PostRentableProductReservationInfoRefundCompleteOutputVo(
            newReservationStateChangeHistory.uid!!
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
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val reservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findByUidAndRowDeleteDateStr(
                reservationStateChangeHistoryUid,
                "/"
            )

        if (reservationStateChangeHistory == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        reservationStateChangeHistory.stateChangeDesc = inputVo.stateChangeDesc
        db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.save(reservationStateChangeHistory)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (개별 상품 반납 확인 <ADMIN>)
    // 관리자의 상품 반납 확인과 고객의 조기 반납 신고 간의 공유락 처리
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductStockReservationInfoReturnCheck(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockReservationInfoReturnCheckInputVo
    ): RentalReservationAdminController.PatchRentableProductStockReservationInfoReturnCheckOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockReservationInfoUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val reservationHistoryList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo.rentableProductReservationInfo,
                "/"
            )
        var denied = false
        var notPaid = true
        for (history in reservationHistoryList) {
            when (history.stateCode.toInt()) {
                2 -> {
                    // 관리자 예약 신청 거부
                    denied = true
                }

                0 -> {
                    // 결제 확인 상태
                    notPaid = false
                }
            }
        }
        if (notPaid || denied) {
            // 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        // 관리자의 상품 반납 확인과 고객의 조기 반납 신고 간의 공유락 처리
        return redis1LockRentableProductStockEarlyReturn.tryLockRepeat<RentalReservationAdminController.PatchRentableProductStockReservationInfoReturnCheckOutputVo?>(
            "$rentableProductStockReservationInfoUid",
            7000L,
            {
                // 상태 확인
                val historyList =
                    db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository
                        .findAllByRentableProductStockReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                            rentableProductStockReservationInfo,
                            "/"
                        )

                var noReturnCheck = true
                var noReturnCheckCancel = true
                var noEarlyReturn = true
                var noEarlyReturnCancel = true
                for (history in historyList) {
                    when (history.stateCode.toInt()) {
                        7 -> {
                            // 사용자 조기 반납 신고 취소
                            if (noEarlyReturn) {
                                noEarlyReturnCancel = false
                            }
                        }

                        4 -> {
                            // 반납 확인 취소
                            if (noReturnCheck) {
                                noReturnCheckCancel = false
                            }
                        }

                        1 -> {
                            // 반납 확인
                            if (noReturnCheckCancel) {
                                noReturnCheck = false
                            }
                        }

                        0 -> {
                            // 사용자 조기 반납 신고
                            if (noEarlyReturnCancel) {
                                noEarlyReturn = false
                            }
                        }
                    }
                }

                if (!noReturnCheck) {
                    // 반납 확인 상태
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return@tryLockRepeat null
                }

                // 상태 확인
                val nowDatetime = LocalDateTime.now()

                if (((noEarlyReturn && noEarlyReturnCancel) || !noEarlyReturnCancel) && nowDatetime.isBefore(
                        rentableProductStockReservationInfo.rentableProductReservationInfo.rentalEndDatetime
                    )
                ) {
                    // 조기 반납 신고 상태가 아니고(내역이 없거나 취소 상태), 상품 반납일도 안됨
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return@tryLockRepeat null
                }

                // 개별 상품 반납 확인 내역 추가
                val historyEntity =
                    db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.save(
                        Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory(
                            rentableProductStockReservationInfo,
                            1,
                            inputVo.stateChangeDesc
                        )
                    )

                httpServletResponse.status = HttpStatus.OK.value()
                return@tryLockRepeat RentalReservationAdminController.PatchRentableProductStockReservationInfoReturnCheckOutputVo(
                    historyEntity.uid!!
                )
            }
        )
    }


    // ----
    // (개별 상품 반납 확인 취소 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductStockReservationInfoReturnCheck(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.DeleteRentableProductStockReservationInfoReturnCheckInputVo
    ): RentalReservationAdminController.DeleteRentableProductStockReservationInfoReturnCheckOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockReservationInfoUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val reservationHistoryList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo.rentableProductReservationInfo,
                "/"
            )
        var denied = false
        var notPaid = true
        for (history in reservationHistoryList) {
            when (history.stateCode.toInt()) {
                2 -> {
                    // 관리자 예약 신청 거부
                    denied = true
                }

                0 -> {
                    // 결제 확인 상태
                    notPaid = false
                }
            }
        }
        if (notPaid || denied) {
            // 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        // 상태 확인
        val historyList =
            db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.findAllByRentableProductStockReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo,
                "/"
            )

        var noReturnCheck = true
        var noReturnCheckCancel = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                4 -> {
                    // 반납 확인 취소
                    if (noReturnCheck) {
                        noReturnCheckCancel = false
                    }
                }

                1 -> {
                    // 반납 확인
                    if (noReturnCheckCancel) {
                        noReturnCheck = false
                    }
                }
            }
        }

        if (noReturnCheck && noReturnCheckCancel) {
            // 반납 확인이 된 적이 없는 상태
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        if (!noReturnCheckCancel) {
            // 반납 확인 취소가 된 상태
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        // 개별 상품 반납 확인 취소 내역 추가
        val historyEntity = db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory(
                rentableProductStockReservationInfo,
                4,
                inputVo.stateChangeDesc
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.DeleteRentableProductStockReservationInfoReturnCheckOutputVo(
            historyEntity.uid!!
        )
    }


    // ----
    // (개별 상품 준비 완료 일시 설정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductStockReservationInfoReady(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockReservationInfoReadyInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockReservationInfoUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val anchorDatetime = if (inputVo.readyDatetime == null) {
            // 대여 가능 상품 재고를 기반으로 최신 순서 개별 상품 예약 정보 리스트 가져오기
            val latestFirstStockReservationList =
                db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.findAllByRentableProductStockInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                    rentableProductStockReservationInfo.rentableProductStockInfo,
                    "/"
                )
            val latestStockReservation = latestFirstStockReservationList[0]

            // 최신 개별 상품 예약 정보가 현재 정보와 다를 때(= 새롭게 진행되는 예약이 존재)
            if (latestStockReservation.uid != rentableProductStockReservationInfo.uid) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return
            }

            null
        } else {
            ZonedDateTime.parse(
                inputVo.readyDatetime,
                DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
            ).toLocalDateTime()
        }

        // 개별 상품 반납 확인 내역 추가
        rentableProductStockReservationInfo.productReadyDatetime = anchorDatetime
        db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.save(rentableProductStockReservationInfo)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (개별 상품 연체 상태 변경 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductStockReservationInfoOverdue(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockReservationInfoOverdueInputVo
    ): RentalReservationAdminController.PatchRentableProductStockReservationInfoOverdueOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockReservationInfoUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val reservationHistoryList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo.rentableProductReservationInfo,
                "/"
            )
        var denied = false
        var notPaid = true
        for (history in reservationHistoryList) {
            when (history.stateCode.toInt()) {
                2 -> {
                    // 관리자 예약 신청 거부
                    denied = true
                }

                0 -> {
                    // 결제 확인 상태
                    notPaid = false
                }
            }
        }
        if (notPaid || denied) {
            // 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "6")
            return null
        }

        // 상태 확인
        val historyList =
            db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.findAllByRentableProductStockReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo,
                "/"
            )

        var noLost = true
        var noLostCancel = true
        var noOverdue = true
        var noOverdueCancel = true
        var noReturnCheck = true
        var noReturnCheckCancel = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                6 -> {
                    // 손망실 설정 취소
                    if (noLost) {
                        noLostCancel = false
                    }
                }

                5 -> {
                    // 연체 설정 취소
                    if (noOverdue) {
                        noOverdueCancel = false
                    }
                }

                4 -> {
                    // 반납 확인 취소
                    if (noReturnCheck) {
                        noReturnCheckCancel = false
                    }
                }

                3 -> {
                    // 손망실 상태
                    if (noLostCancel) {
                        noLost = false
                    }
                }

                2 -> {
                    // 연체 상태
                    if (noOverdueCancel) {
                        noOverdue = false
                    }
                }

                1 -> {
                    // 반납 확인 상태
                    if (noReturnCheckCancel) {
                        noReturnCheck = false
                    }
                }
            }
        }

        if (!noOverdue) {
            // 연체 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        if (!noLost) {
            // 손망실 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        if (!noReturnCheck) {
            // 반납 확인 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return null
        }

        // 상태 확인
        val nowDatetime = LocalDateTime.now()

        if (nowDatetime.isBefore(rentableProductStockReservationInfo.rentableProductReservationInfo.rentalEndDatetime)) {
            // 상품 반납일을 넘지 않음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // 개별 상품 연체 내역 추가
        val historyEntity = db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory(
                rentableProductStockReservationInfo,
                2,
                inputVo.stateChangeDesc
            )
        )

        // 상품 준비일 설정 초기화
        rentableProductStockReservationInfo.productReadyDatetime = null
        db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.save(rentableProductStockReservationInfo)

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PatchRentableProductStockReservationInfoOverdueOutputVo(
            historyEntity.uid!!
        )
    }


    // ----
    // (개별 상품 연체 상태 변경 취소 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductStockReservationInfoOverdue(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.DeleteRentableProductStockReservationInfoOverdueInputVo
    ): RentalReservationAdminController.DeleteRentableProductStockReservationInfoOverdueOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockReservationInfoUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val reservationHistoryList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo.rentableProductReservationInfo,
                "/"
            )
        var denied = false
        var notPaid = true
        for (history in reservationHistoryList) {
            when (history.stateCode.toInt()) {
                2 -> {
                    // 관리자 예약 신청 거부
                    denied = true
                }

                0 -> {
                    // 결제 확인 상태
                    notPaid = false
                }
            }
        }
        if (notPaid || denied) {
            // 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        // 상태 확인
        val historyList =
            db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.findAllByRentableProductStockReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo,
                "/"
            )

        var noOverdue = true
        var noOverdueCancel = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                5 -> {
                    // 연체 설정 취소
                    if (noOverdue) {
                        noOverdueCancel = false
                    }
                }

                2 -> {
                    // 연체 상태
                    if (noOverdueCancel) {
                        noOverdue = false
                    }
                }
            }
        }

        if (noOverdue && noOverdueCancel) {
            // 연체 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        if (!noOverdueCancel) {
            // 연체 상태 변경 취소 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        // 개별 상품 연체 상태 취소 내역 추가
        val historyEntity = db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory(
                rentableProductStockReservationInfo,
                5,
                inputVo.stateChangeDesc
            )
        )

        // 상품 준비일 설정 초기화
        rentableProductStockReservationInfo.productReadyDatetime = null
        db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.save(rentableProductStockReservationInfo)

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.DeleteRentableProductStockReservationInfoOverdueOutputVo(
            historyEntity.uid!!
        )
    }


    // ----
    // (개별 상품 연체 상태 변경 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchRentableProductStockReservationInfoLost(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.PatchRentableProductStockReservationInfoLostInputVo
    ): RentalReservationAdminController.PatchRentableProductStockReservationInfoLostOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockReservationInfoUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val reservationHistoryList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo.rentableProductReservationInfo,
                "/"
            )
        var denied = false
        var notPaid = true
        for (history in reservationHistoryList) {
            when (history.stateCode.toInt()) {
                2 -> {
                    // 관리자 예약 신청 거부
                    denied = true
                }

                0 -> {
                    // 결제 확인 상태
                    notPaid = false
                }
            }
        }
        if (notPaid || denied) {
            // 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        // 상태 확인
        val historyList =
            db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.findAllByRentableProductStockReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo,
                "/"
            )

        var noLost = true
        var noLostCancel = true
        var noOverdue = true
        var noOverdueCancel = true
        var noReturnCheck = true
        var noReturnCheckCancel = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                6 -> {
                    // 손망실 설정 취소
                    if (noLost) {
                        noLostCancel = false
                    }
                }

                5 -> {
                    // 연체 설정 취소
                    if (noOverdue) {
                        noOverdueCancel = false
                    }
                }

                4 -> {
                    // 반납 확인 취소
                    if (noReturnCheck) {
                        noReturnCheckCancel = false
                    }
                }

                3 -> {
                    // 손망실 상태
                    if (noLostCancel) {
                        noLost = false
                    }
                }

                2 -> {
                    // 연체 상태
                    if (noOverdueCancel) {
                        noOverdue = false
                    }
                }

                1 -> {
                    // 반납 확인 상태
                    if (noReturnCheckCancel) {
                        noReturnCheck = false
                    }
                }
            }
        }

        if (!noOverdue) {
            // 연체 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        if (!noLost) {
            // 손망실 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        if (!noReturnCheck) {
            // 반납 확인 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return null
        }

        // 상태 확인
        val nowDatetime = LocalDateTime.now()

        if (nowDatetime.isBefore(rentableProductStockReservationInfo.rentableProductReservationInfo.rentalStartDatetime)) {
            // 상품 대여 시작을 넘지 않음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // 개별 상품 손망실 내역 추가
        val historyEntity = db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory(
                rentableProductStockReservationInfo,
                3,
                inputVo.stateChangeDesc
            )
        )

        // 상품 준비일 설정 초기화
        rentableProductStockReservationInfo.productReadyDatetime = null
        db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.save(rentableProductStockReservationInfo)

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.PatchRentableProductStockReservationInfoLostOutputVo(
            historyEntity.uid!!
        )
    }


    // ----
    // (개별 상품 손망실 상태 변경 취소 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRentableProductStockReservationInfoLost(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        rentableProductStockReservationInfoUid: Long,
        inputVo: RentalReservationAdminController.DeleteRentableProductStockReservationInfoLostInputVo
    ): RentalReservationAdminController.DeleteRentableProductStockReservationInfoLostOutputVo? {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val rentableProductStockReservationInfo =
            db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.findByUidAndRowDeleteDateStr(
                rentableProductStockReservationInfoUid,
                "/"
            )

        if (rentableProductStockReservationInfo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val reservationHistoryList =
            db1RaillyLinkerCompanyRentableProductReservationStateChangeHistoryRepository.findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo.rentableProductReservationInfo,
                "/"
            )
        var denied = false
        var notPaid = true
        for (history in reservationHistoryList) {
            when (history.stateCode.toInt()) {
                2 -> {
                    // 관리자 예약 신청 거부
                    denied = true
                }

                0 -> {
                    // 결제 확인 상태
                    notPaid = false
                }
            }
        }
        if (notPaid || denied) {
            // 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "4")
            return null
        }

        // 상태 확인
        val historyList =
            db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.findAllByRentableProductStockReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
                rentableProductStockReservationInfo,
                "/"
            )

        var noLost = true
        var noLostCancel = true
        for (history in historyList) {
            when (history.stateCode.toInt()) {
                6 -> {
                    // 손망실 설정 취소
                    if (noLost) {
                        noLostCancel = false
                    }
                }

                3 -> {
                    // 손망실 상태
                    if (noLostCancel) {
                        noLost = false
                    }
                }
            }
        }

        if (noLost && noLostCancel) {
            // 손망실 상태 변경 내역이 없습니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        if (!noLostCancel) {
            // 손망실 상태 변경 취소 상태입니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        // 개별 상품 손망실 내역 추가
        val historyEntity = db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.save(
            Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory(
                rentableProductStockReservationInfo,
                6,
                inputVo.stateChangeDesc
            )
        )

        // 상품 준비일 설정 초기화
        rentableProductStockReservationInfo.productReadyDatetime = null
        db1RaillyLinkerCompanyRentableProductStockReservationInfoRepository.save(rentableProductStockReservationInfo)

        httpServletResponse.status = HttpStatus.OK.value()
        return RentalReservationAdminController.DeleteRentableProductStockReservationInfoLostOutputVo(
            historyEntity.uid!!
        )
    }


    // ----
    // (개별 상품 예약 상태 테이블의 상세 설명 수정 <ADMIN>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun patchStockReservationStateChangeHistoryStateChangeDesc(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        stockReservationStateChangeHistoryUid: Long,
        inputVo: RentalReservationAdminController.PatchStockReservationStateChangeHistoryStateChangeDescInputVo
    ) {
//        val memberUid = jwtTokenUtil.getMemberUid(
//            authorization.split(" ")[1].trim(),
//            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
//            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
//        )

        val reservationStateChangeHistory =
            db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.findByUidAndRowDeleteDateStr(
                stockReservationStateChangeHistoryUid,
                "/"
            )

        if (reservationStateChangeHistory == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        reservationStateChangeHistory.stateChangeDesc = inputVo.stateChangeDesc
        db1RaillyLinkerCompanyRentableProductStockReservationStateChangeHistoryRepository.save(
            reservationStateChangeHistory
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }
}