package com.raillylinker.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.services.RentalReservationAdminService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

@Tag(name = "/rental-reservation-admin APIs", description = "대여 예약 서비스 관리자 API 컨트롤러")
@Controller
@RequestMapping("/rental-reservation-admin")
class RentalReservationAdminController(
    private val service: RentalReservationAdminService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "예약 상품 카테고리 정보 등록 <ADMIN>",
        description = "예약 상품의 카테고리 정보를 등록합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : parentRentableProductCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-category"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductCategory(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostRentableProductCategoryInputVo
    ): PostRentableProductCategoryOutputVo? {
        return service.postRentableProductCategory(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PostRentableProductCategoryInputVo(
        @Schema(description = "부모 카테고리 고유번호", required = false, example = "1")
        @JsonProperty("parentRentableProductCategoryUid")
        val parentRentableProductCategoryUid: Long?,
        @Schema(description = "카테고리 이름", required = true, example = "유머")
        @JsonProperty("categoryName")
        val categoryName: String
    )

    data class PostRentableProductCategoryOutputVo(
        @Schema(description = "rentableProductCategory 고유값", required = true, example = "1")
        @JsonProperty("rentableProductCategoryUid")
        val rentableProductCategoryUid: Long
    )


    // ----
    @Operation(
        summary = "예약 상품 카테고리 정보 수정 <ADMIN>",
        description = "예약 상품의 카테고리 정보를 수정합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "1 : parentRentableProductCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PutMapping(
        path = ["/rentable-product-category/{rentableProductCategoryUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun putRentableProductCategory(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductCategoryUid", description = "rentableProductCategory 고유값", example = "1")
        @PathVariable("rentableProductCategoryUid")
        rentableProductCategoryUid: Long,
        @RequestBody
        inputVo: PutRentableProductCategoryInputVo
    ) {
        service.putRentableProductCategory(
            httpServletResponse,
            authorization!!,
            rentableProductCategoryUid,
            inputVo
        )
    }

    data class PutRentableProductCategoryInputVo(
        @Schema(description = "부모 카테고리 고유번호", required = false, example = "1")
        @JsonProperty("parentRentableProductCategoryUid")
        val parentRentableProductCategoryUid: Long?,
        @Schema(description = "카테고리 이름", required = true, example = "유머")
        @JsonProperty("categoryName")
        val categoryName: String
    )


    // ----
    @Operation(
        summary = "예약 상품 카테고리 정보 삭제 <ADMIN>",
        description = "예약 상품의 카테고리 정보를 삭제합니다.<br>" +
                "하위 카테고리들은 모두 자동 삭제되며, 예약 상품 정보의 카테고리로 설정되어 있다면 null 로 재설정 됩니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/rentable-product-category/{rentableProductCategoryUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun deleteRentableProductCategory(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductCategoryUid", description = "rentableProductCategory 고유값", example = "1")
        @PathVariable("rentableProductCategoryUid")
        rentableProductCategoryUid: Long
    ) {
        service.deleteRentableProductCategory(
            httpServletResponse,
            authorization!!,
            rentableProductCategoryUid
        )
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 등록 <ADMIN>",
        description = "대여 상품 정보를 등록합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 최소 예약 횟수는 최대 예약 횟수보다 작거나 같아야 합니다.<br>" +
                                "3 : 결제 통보 기한 설정이 결제 승인 기한 설정보다 크면 안됩니다.<br>" +
                                "4 : 결제 승인 기한 설정이 예약 승인 기한 설정보다 크면 안됩니다.<br>" +
                                "5 : reservationUnitMinute, reservationUnitPrice, customerPaymentDeadlineMinute, " +
                                "paymentCheckDeadlineMinute, paymentCheckDeadlineMinute, approvalDeadlineMinute, " +
                                "cancelDeadlineMinute 는 음수가 될 수 없습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-info"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductInfo(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostRentableProductInfoInputVo
    ): PostRentableProductInfoOutputVo? {
        return service.postRentableProductInfo(httpServletResponse, authorization!!, inputVo)
    }

    data class PostRentableProductInfoInputVo(
        @Schema(description = "고객에게 보일 상품명", required = true, example = "testString")
        @JsonProperty("productName")
        val productName: String,
        @Schema(
            description = "상품 카테고리 고유값",
            required = true,
            example = "1"
        )
        @JsonProperty("rentableProductCategoryUid")
        val rentableProductCategoryUid: Long?,
        @Schema(
            description = "고객에게 보일 상품 소개",
            required = true,
            example = "예약해주세요."
        )
        @JsonProperty("productIntro")
        val productIntro: String,
        @Schema(
            description = "상품이 위치한 주소(대여 가능 위치의 기준으로 사용됨) - 국가",
            required = true,
            example = "대한민국"
        )
        @JsonProperty("addressCountry")
        val addressCountry: String,
        @Schema(
            description = "상품이 위치한 주소(대여 가능 위치의 기준으로 사용됨) - 국가와 상세 주소를 제외",
            required = true,
            example = "서울시 은평구 불광동 미래혁신센터"
        )
        @JsonProperty("addressMain")
        val addressMain: String,
        @Schema(
            description = "상품이 위치한 주소(대여 가능 위치의 기준으로 사용됨) - 상세",
            required = true,
            example = "200 동 109 호"
        )
        @JsonProperty("addressDetail")
        val addressDetail: String,
        @Schema(
            description = "상품 예약이 가능한 최초 일시(콘서트 티켓 예매 선공개 기능을 가정)(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("firstReservableDatetime")
        val firstReservableDatetime: String,
        @Schema(
            description = "예약 추가 할 수 있는 최소 시간 단위 (분)",
            required = true,
            example = "60"
        )
        @JsonProperty("reservationUnitMinute")
        val reservationUnitMinute: Long,
        @Schema(
            description = "단위 예약 시간을 대여일 기준에서 최소 몇번 추가 해야 하는지",
            required = true,
            example = "1"
        )
        @JsonProperty("minimumReservationUnitCount")
        val minimumReservationUnitCount: Long,
        @Schema(
            description = "단위 예약 시간을 대여일 기준에서 최대 몇번 추가 가능한지 (Null 이라면 제한 없음)",
            required = false,
            example = "3"
        )
        @JsonProperty("maximumReservationUnitCount")
        val maximumReservationUnitCount: Long?,
        @Schema(
            description = "단위 예약 시간에 대한 가격 (예약 시간 / 단위 예약 시간 * 예약 단가 = 예약 최종가)",
            required = true,
            example = "10000"
        )
        @JsonProperty("reservationUnitPrice")
        val reservationUnitPrice: BigDecimal,
        @Schema(
            description = "단위 예약 시간에 대한 가격 통화 코드(IOS 4217)",
            required = true,
            example = "KRW"
        )
        @JsonProperty("reservationUnitPriceCurrencyCode")
        val reservationUnitPriceCurrencyCode: CurrencyCodeEnum,
        @Schema(
            description = "예약 가능 설정 (재고, 상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정)",
            required = true,
            example = "true"
        )
        @JsonProperty("nowReservable")
        val nowReservable: Boolean,
        @Schema(
            description = "고객에게 이때까지 결제를 해야 한다고 통보하는 기한 설정값(예약일로부터 +N 분)",
            required = true,
            example = "30"
        )
        @JsonProperty("customerPaymentDeadlineMinute")
        val customerPaymentDeadlineMinute: Long,
        @Schema(
            description = "관리자의 결제 확인 기한 설정값(예약일로 부터 +N 분, 고객 결제 기한 설정값보다 크거나 같음)",
            required = true,
            example = "30"
        )
        @JsonProperty("paymentCheckDeadlineMinute")
        val paymentCheckDeadlineMinute: Long,
        @Schema(
            description = "관리자의 예약 승인 기한 설정값(예약일로부터 +N분, 결제 확인 기한 설정값보다 크거나 같음)",
            required = true,
            example = "30"
        )
        @JsonProperty("approvalDeadlineMinute")
        val approvalDeadlineMinute: Long,
        @Schema(
            description = "고객이 예약 취소 가능한 기한 설정값(대여 시작일로부터 -N분으로 계산됨)",
            required = true,
            example = "30"
        )
        @JsonProperty("cancelDeadlineMinute")
        val cancelDeadlineMinute: Long
    ) {
        enum class CurrencyCodeEnum {
            KRW, USD
        }
    }

    data class PostRentableProductInfoOutputVo(
        @Schema(description = "rentableProductInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductInfoUid")
        val rentableProductInfoUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 수정 <ADMIN>",
        description = "대여 상품 정보를 수정합니다.<br>" +
                "상품 수정시 update_version_seq 가 1 증가하며, 예약 요청시 고객이 보내온 update_version 이 일치하지 않는다면 진행되지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : rentableProductCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "3 : 최소,최대 예약 횟수는 0보다 크며, 최소 예약 횟수는 최대 예약 횟수보다 작거나 같아야 합니다.<br>" +
                                "4 : 결제 통보 기한 설정이 결제 승인 기한 설정보다 크면 안됩니다.<br>" +
                                "5 : 결제 승인 기한 설정이 예약 승인 기한 설정보다 크면 안됩니다.<br>" +
                                "6 : reservationUnitMinute, reservationUnitPrice, customerPaymentDeadlineMinute, " +
                                "paymentCheckDeadlineMinute, paymentCheckDeadlineMinute, approvalDeadlineMinute, " +
                                "cancelDeadlineMinute 는 음수가 될 수 없습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PutMapping(
        path = ["/rentable-product-info/{rentableProductInfoUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun putRentableProductInfo(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductInfoUid", description = "rentableProductInfo 고유값", example = "1")
        @PathVariable("rentableProductInfoUid")
        rentableProductInfoUid: Long,
        @RequestBody
        inputVo: PutRentableProductInfoInputVo
    ) {
        service.putRentableProductInfo(httpServletResponse, authorization!!, rentableProductInfoUid, inputVo)
    }

    data class PutRentableProductInfoInputVo(
        @Schema(description = "고객에게 보일 상품명", required = true, example = "testString")
        @JsonProperty("productName")
        val productName: String,
        @Schema(
            description = "상품 카테고리 고유값",
            required = true,
            example = "1"
        )
        @JsonProperty("rentableProductCategoryUid")
        val rentableProductCategoryUid: Long?,
        @Schema(
            description = "고객에게 보일 상품 소개",
            required = true,
            example = "예약해주세요."
        )
        @JsonProperty("productIntro")
        val productIntro: String,
        @Schema(
            description = "상품이 위치한 주소(대여 가능 위치의 기준으로 사용됨) - 국가",
            required = true,
            example = "대한민국"
        )
        @JsonProperty("addressCountry")
        val addressCountry: String,
        @Schema(
            description = "상품이 위치한 주소(대여 가능 위치의 기준으로 사용됨) - 국가와 상세 주소를 제외",
            required = true,
            example = "서울시 은평구 불광동 미래혁신센터"
        )
        @JsonProperty("addressMain")
        val addressMain: String,
        @Schema(
            description = "상품이 위치한 주소(대여 가능 위치의 기준으로 사용됨) - 상세",
            required = true,
            example = "200 동 109 호"
        )
        @JsonProperty("addressDetail")
        val addressDetail: String,
        @Schema(
            description = "상품 예약이 가능한 최초 일시(콘서트 티켓 예매 선공개 기능을 가정)(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("firstReservableDatetime")
        val firstReservableDatetime: String,
        @Schema(
            description = "예약 추가 할 수 있는 최소 시간 단위 (분)",
            required = true,
            example = "60"
        )
        @JsonProperty("reservationUnitMinute")
        val reservationUnitMinute: Long,
        @Schema(
            description = "단위 예약 시간을 대여일 기준에서 최소 몇번 추가 해야 하는지",
            required = true,
            example = "1"
        )
        @JsonProperty("minimumReservationUnitCount")
        val minimumReservationUnitCount: Long,
        @Schema(
            description = "단위 예약 시간을 대여일 기준에서 최대 몇번 추가 가능한지 (Null 이라면 제한 없음)",
            required = false,
            example = "3"
        )
        @JsonProperty("maximumReservationUnitCount")
        val maximumReservationUnitCount: Long?,
        @Schema(
            description = "단위 예약 시간에 대한 가격 (예약 시간 / 단위 예약 시간 * 예약 단가 = 예약 최종가)",
            required = true,
            example = "10000"
        )
        @JsonProperty("reservationUnitPrice")
        val reservationUnitPrice: BigDecimal,
        @Schema(
            description = "단위 예약 시간에 대한 가격 통화 코드(IOS 4217)",
            required = true,
            example = "KRW"
        )
        @JsonProperty("reservationUnitPriceCurrencyCode")
        val reservationUnitPriceCurrencyCode: CurrencyCodeEnum,
        @Schema(
            description = "예약 가능 설정 (재고, 상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정)",
            required = true,
            example = "true"
        )
        @JsonProperty("nowReservable")
        val nowReservable: Boolean,
        @Schema(
            description = "고객에게 이때까지 결제를 해야 한다고 통보하는 기한 설정값(예약일로부터 +N 분)",
            required = true,
            example = "30"
        )
        @JsonProperty("customerPaymentDeadlineMinute")
        val customerPaymentDeadlineMinute: Long,
        @Schema(
            description = "관리자의 결제 확인 기한 설정값(예약일로 부터 +N 분, 고객 결제 기한 설정값보다 크거나 같음)",
            required = true,
            example = "30"
        )
        @JsonProperty("paymentCheckDeadlineMinute")
        val paymentCheckDeadlineMinute: Long,
        @Schema(
            description = "관리자의 예약 승인 기한 설정값(예약일로부터 +N분, 결제 확인 기한 설정값보다 크거나 같음)",
            required = true,
            example = "30"
        )
        @JsonProperty("approvalDeadlineMinute")
        val approvalDeadlineMinute: Long,
        @Schema(
            description = "고객이 예약 취소 가능한 기한 설정값(대여 시작일로부터 -N분이며, 그 결과가 관리자 승인 기한보다 커야함)",
            required = true,
            example = "30"
        )
        @JsonProperty("cancelDeadlineMinute")
        val cancelDeadlineMinute: Long
    ) {
        enum class CurrencyCodeEnum {
            KRW, USD
        }
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 삭제 <ADMIN>",
        description = "대여 상품을 삭제 처리 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/rentable-product-info/{rentableProductInfoUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun deleteRentableProductInfo(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductInfoUid", description = "rentableProductInfo 고유값", example = "1")
        @PathVariable("rentableProductInfoUid")
        rentableProductInfoUid: Long
    ) {
        service.deleteRentableProductInfo(httpServletResponse, authorization!!, rentableProductInfoUid)
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 추가 예약 가능 설정 수정 <ADMIN>",
        description = "대여 가능 상품을 현 시간부로 예약 가능하게 할 것인지에 대한 스위치 플래그 수정<br>" +
                "update_version_seq 증가는 하지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/rentable-product-info/{rentableProductInfoUid}/reservable"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchRentableProductInfoReservable(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductInfoUid", description = "rentableProductInfo 고유값", example = "1")
        @PathVariable("rentableProductInfoUid")
        rentableProductInfoUid: Long,
        @RequestBody
        inputVo: PatchRentableProductInfoReservableInputVo
    ) {
        service.patchRentableProductInfoReservable(
            httpServletResponse,
            authorization!!,
            rentableProductInfoUid,
            inputVo
        )
    }

    data class PatchRentableProductInfoReservableInputVo(
        @Schema(
            description = "예약 가능 설정 (재고, 상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정)",
            required = true,
            example = "true"
        )
        @JsonProperty("nowReservable")
        val nowReservable: Boolean
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 최소 예약 횟수 설정 수정 <ADMIN>",
        description = "대여 가능 상품의 현 시간부로의 최소 예약 횟수 설정 수정<br>" +
                "update_version_seq 증가는 하지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 최소 예약 횟수는 0보다 크며, 최대 예약 횟수보다 작거나 같아야 합니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/rentable-product-info/{rentableProductInfoUid}/min-reservation-unit-count"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchRentableProductInfoMinReservationUnitCount(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductInfoUid", description = "rentableProductInfo 고유값", example = "1")
        @PathVariable("rentableProductInfoUid")
        rentableProductInfoUid: Long,
        @RequestBody
        inputVo: PatchRentableProductInfoMinReservationUnitCountInputVo
    ) {
        service.patchRentableProductInfoMinReservationUnitCount(
            httpServletResponse,
            authorization!!,
            rentableProductInfoUid,
            inputVo
        )
    }

    data class PatchRentableProductInfoMinReservationUnitCountInputVo(
        @Schema(
            description = "단위 예약 시간을 대여일 기준에서 최소 몇번 추가 해야 하는지",
            required = true,
            example = "1"
        )
        @JsonProperty("minimumReservationUnitCount")
        val minimumReservationUnitCount: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 최대 예약 횟수 설정 수정 <ADMIN>",
        description = "대여 가능 상품의 현 시간부로의 최대 예약 횟수 설정 수정<br>" +
                "update_version_seq 증가는 하지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 최대 예약 횟수는 0보다 크며, 최소 예약 횟수보다 크거나 같아야 합니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/rentable-product-info/{rentableProductInfoUid}/max-reservation-unit-count"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchRentableProductInfoMaxReservationUnitCount(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductInfoUid", description = "rentableProductInfo 고유값", example = "1")
        @PathVariable("rentableProductInfoUid")
        rentableProductInfoUid: Long,
        @RequestBody
        inputVo: PatchRentableProductInfoMaxReservationUnitCountInputVo
    ) {
        service.patchRentableProductInfoMaxReservationUnitCount(
            httpServletResponse,
            authorization!!,
            rentableProductInfoUid,
            inputVo
        )
    }

    data class PatchRentableProductInfoMaxReservationUnitCountInputVo(
        @Schema(
            description = "단위 예약 시간을 대여일 기준에서 최대 몇번 추가 해야 하는지",
            required = false,
            example = "1"
        )
        @JsonProperty("maximumReservationUnitCount")
        val maximumReservationUnitCount: Long?
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 이미지 등록 <ADMIN>",
        description = "대여 상품 이미지를 등록합니다.<br>" +
                "이미지 관련 상품 정보 변경에는 update_version_seq 가 증가 하지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-image"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductImage(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @ModelAttribute
        @RequestBody
        inputVo: PostRentableProductImageInputVo
    ): PostRentableProductImageOutputVo? {
        return service.postRentableProductImage(httpServletResponse, authorization!!, inputVo)
    }

    data class PostRentableProductImageInputVo(
        @Schema(description = "rentableProductInfo 고유값", example = "1", required = true)
        @JsonProperty("rentableProductInfoUid")
        val rentableProductInfoUid: Long,
        @Schema(description = "고객에게 보일 상품 썸네일 이미지", required = true)
        @JsonProperty("thumbnailImage")
        val thumbnailImage: MultipartFile
    )

    data class PostRentableProductImageOutputVo(
        @Schema(description = "rentableProductImage 고유값", required = true, example = "1")
        @JsonProperty("rentableProductImageUid")
        val rentableProductImageUid: Long,
        @Schema(description = "생성된 이미지 다운로드 경로", required = true, example = "https://testimage.com/sample.jpg")
        @JsonProperty("productImageFullUrl")
        val productImageFullUrl: String
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 이미지 파일 다운받기",
        description = "대여 가능 상품 이미지를 by_product_files 위치에 저장했을 때 파일을 가져오기 위한 API"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/product-image/{fileName}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun getProductImageFile(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "fileName", description = "by_product_files 폴더 안의 파일명", example = "test.jpg")
        @PathVariable("fileName")
        fileName: String
    ): ResponseEntity<Resource>? {
        return service.getProductImageFile(httpServletResponse, fileName)
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 이미지 삭제 <ADMIN>",
        description = "대여 상품 이미지를 삭제합니다.<br>" +
                "상품 정보에 대표 이미지로 설정되어 있다면 대표 이미지 설정이 null 이 됩니다.<br>" +
                "이미지 관련 상품 정보 변경에는 update_version_seq 가 증가 하지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductImageUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/rentable-product-image/{rentableProductImageUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun deleteRentableProductImage(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductImageUid", description = "rentableProductImage 고유값", example = "1")
        @PathVariable("rentableProductImageUid")
        rentableProductImageUid: Long
    ) {
        service.deleteRentableProductImage(httpServletResponse, authorization!!, rentableProductImageUid)
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 대표 상품 이미지 설정 수정 <ADMIN>",
        description = "대여 가능 상품의 대표 상품 이미지 설정을 수정합니다.<br>" +
                "이미지 관련 상품 정보 변경에는 update_version_seq 가 증가 하지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : rentableProductImageUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/rentable-product-info/{rentableProductInfoUid}/front-image"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchRentableProductInfoFrontImage(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductInfoUid", description = "rentableProductInfo 고유값", example = "1")
        @PathVariable("rentableProductInfoUid")
        rentableProductInfoUid: Long,
        @RequestBody
        inputVo: PatchRentableProductInfoFrontImageInputVo
    ) {
        service.patchRentableProductInfoFrontImage(
            httpServletResponse,
            authorization!!,
            rentableProductInfoUid,
            inputVo
        )
    }

    data class PatchRentableProductInfoFrontImageInputVo(
        @Schema(description = "rentableProductImage 고유값 (null 이라면 대표 이미지를 설정하지 않음)", required = false, example = "1")
        @JsonProperty("rentableProductImageUid")
        val rentableProductImageUid: Long?
    )


    // ----
    @Operation(
        summary = "예약 상품 재고 카테고리 정보 등록 <ADMIN>",
        description = "예약 상품 재고의 카테고리 정보를 등록합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : parentRentableProductStockCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-category"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockCategory(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostRentableProductStockCategoryInputVo
    ): PostRentableProductStockCategoryOutputVo? {
        return service.postRentableProductStockCategory(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PostRentableProductStockCategoryInputVo(
        @Schema(description = "부모 카테고리 고유번호", required = false, example = "1")
        @JsonProperty("parentRentableProductStockCategoryUid")
        val parentRentableProductStockCategoryUid: Long?,
        @Schema(description = "카테고리 이름", required = true, example = "유머")
        @JsonProperty("categoryName")
        val categoryName: String
    )

    data class PostRentableProductStockCategoryOutputVo(
        @Schema(description = "rentableProductStockCategory 고유값", required = true, example = "1")
        @JsonProperty("rentableProductStockCategoryUid")
        val rentableProductStockCategoryUid: Long
    )


    // ----
    @Operation(
        summary = "예약 상품 재고 카테고리 정보 수정 <ADMIN>",
        description = "예약 상품 재고의 카테고리 정보를 수정합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "1 : parentRentableProductStockCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PutMapping(
        path = ["/rentable-product-stock-category/{rentableProductStockCategoryUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun putRentableProductStockCategory(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockCategoryUid",
            description = "rentableProductStockCategory 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockCategoryUid")
        rentableProductStockCategoryUid: Long,
        @RequestBody
        inputVo: PutRentableProductStockCategoryInputVo
    ) {
        service.putRentableProductStockCategory(
            httpServletResponse,
            authorization!!,
            rentableProductStockCategoryUid,
            inputVo
        )
    }

    data class PutRentableProductStockCategoryInputVo(
        @Schema(description = "부모 카테고리 고유번호", required = false, example = "1")
        @JsonProperty("parentRentableProductStockCategoryUid")
        val parentRentableProductStockCategoryUid: Long?,
        @Schema(description = "카테고리 이름", required = true, example = "유머")
        @JsonProperty("categoryName")
        val categoryName: String
    )


    // ----
    @Operation(
        summary = "예약 상품 재고 카테고리 정보 삭제 <ADMIN>",
        description = "예약 상품 재고의 카테고리 정보를 삭제합니다.<br>" +
                "하위 카테고리들은 모두 자동 삭제되며, 예약 상품 재고 정보의 카테고리로 설정되어 있다면 null 로 재설정 됩니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/rentable-product-stock-category/{rentableProductStockCategoryUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun deleteRentableProductStockCategory(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockCategoryUid",
            description = "rentableProductStockCategory 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockCategoryUid")
        rentableProductStockCategoryUid: Long
    ) {
        service.deleteRentableProductStockCategory(
            httpServletResponse,
            authorization!!,
            rentableProductStockCategoryUid
        )
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 등록 <ADMIN>",
        description = "대여 가능 상품에 속하는 상품 재고 정보를 등록합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : rentableProductStockCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-info"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockInfo(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostRentableProductStockInfoInputVo
    ): PostRentableProductStockInfoOutputVo? {
        return service.postRentableProductStockInfo(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PostRentableProductStockInfoInputVo(
        @Schema(
            description = "rentableProductInfo 고유값",
            required = true,
            example = "1"
        )
        @JsonProperty("rentableProductInfoUid")
        val rentableProductInfoUid: Long,
        @Schema(
            description = "상품 재고 카테고리 고유값",
            required = true,
            example = "1"
        )
        @JsonProperty("rentableProductStockCategoryUid")
        val rentableProductStockCategoryUid: Long?,
        @Schema(
            description = "대여 가능 상품 개별 설명",
            required = true,
            example = "상태 양호"
        )
        @JsonProperty("productDesc")
        val productDesc: String,
        @Schema(
            description = "제품 대여(손님에게 제공)가 가능한 최초 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("firstRentableDatetime")
        val firstRentableDatetime: String,
        @Schema(
            description = "제품 대여 마지막 일시 (yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, 이때가 대여 마지막 날, null 이라면 무기한)",
            required = false,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("lastRentableDatetime")
        val lastRentableDatetime: String?,
        @Schema(
            description = "예약 가능 설정 (재고, 상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정)",
            required = true,
            example = "true"
        )
        @JsonProperty("nowReservable")
        val nowReservable: Boolean
    )

    data class PostRentableProductStockInfoOutputVo(
        @Schema(description = "rentableProductStockInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductStockInfoUid")
        val rentableProductStockInfoUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 삭제 <ADMIN>",
        description = "대여 상품 재고를 삭제 처리 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/rentable-product-stock-info/{rentableProductStockInfoUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun deleteRentableProductStockInfo(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductStockInfoUid", description = "rentableProductStockInfo 고유값", example = "1")
        @PathVariable("rentableProductStockInfoUid")
        rentableProductStockInfoUid: Long
    ) {
        service.deleteRentableProductStockInfo(httpServletResponse, authorization!!, rentableProductStockInfoUid)
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 수정 <ADMIN>",
        description = "대여 가능 상품에 속하는 상품 재고 정보를 수정합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "3 : rentableProductStockCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PutMapping(
        path = ["/rentable-product-stock-info/{rentableProductStockInfoUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun putRentableProductStockInfo(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductStockInfoUid", description = "rentableProductStockInfo 고유값", example = "1")
        @PathVariable("rentableProductStockInfoUid")
        rentableProductStockInfoUid: Long,
        @RequestBody
        inputVo: PutRentableProductStockInfoInputVo
    ) {
        service.putRentableProductStockInfo(
            httpServletResponse,
            authorization!!,
            rentableProductStockInfoUid,
            inputVo
        )
    }

    data class PutRentableProductStockInfoInputVo(
        @Schema(
            description = "rentableProductInfo 고유값",
            required = true,
            example = "1"
        )
        @JsonProperty("rentableProductInfoUid")
        val rentableProductInfoUid: Long,
        @Schema(
            description = "상품 재고 카테고리 고유값",
            required = true,
            example = "1"
        )
        @JsonProperty("rentableProductStockCategoryUid")
        val rentableProductStockCategoryUid: Long?,
        @Schema(
            description = "대여 가능 상품 개별 설명",
            required = true,
            example = "상태 양호"
        )
        @JsonProperty("productDesc")
        val productDesc: String,
        @Schema(
            description = "제품 대여(손님에게 제공)가 가능한 최초 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("firstRentableDatetime")
        val firstRentableDatetime: String,
        @Schema(
            description = "제품 대여 마지막 일시 (yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, 이때가 대여 마지막 날)",
            required = false,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("lastRentableDatetime")
        val lastRentableDatetime: String?,
        @Schema(
            description = "예약 가능 설정 (재고, 상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정)",
            required = true,
            example = "true"
        )
        @JsonProperty("nowReservable")
        val nowReservable: Boolean
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 추가 예약 가능 설정 수정 <ADMIN>",
        description = "대여 가능 상품 재고를 현 시간부로 예약 가능하게 할 것인지에 대한 스위치 플래그 수정"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/rentable-product-stock-info/{rentableProductStockInfoUid}/reservable"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchRentableProductStockInfoReservable(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductStockInfoUid", description = "rentableProductStockInfo 고유값", example = "1")
        @PathVariable("rentableProductStockInfoUid")
        rentableProductStockInfoUid: Long,
        @RequestBody
        inputVo: PatchRentableProductStockInfoReservableInputVo
    ) {
        service.patchRentableProductStockInfoReservable(
            httpServletResponse,
            authorization!!,
            rentableProductStockInfoUid,
            inputVo
        )
    }

    data class PatchRentableProductStockInfoReservableInputVo(
        @Schema(
            description = "예약 가능 설정 (재고, 상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정)",
            required = true,
            example = "true"
        )
        @JsonProperty("nowReservable")
        val nowReservable: Boolean
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 이미지 등록 <ADMIN>",
        description = "대여 상품 재고 이미지를 등록합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-image"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockImage(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @ModelAttribute
        @RequestBody
        inputVo: PostRentableProductStockImageInputVo
    ): PostRentableProductStockImageOutputVo? {
        return service.postRentableProductStockImage(httpServletResponse, authorization!!, inputVo)
    }

    data class PostRentableProductStockImageInputVo(
        @Schema(description = "rentableProductStockInfo 고유값", example = "1", required = true)
        @JsonProperty("rentableProductInfoStockUid")
        val rentableProductInfoStockUid: Long,
        @Schema(description = "고객에게 보일 상품 썸네일 이미지", required = true)
        @JsonProperty("thumbnailImage")
        val thumbnailImage: MultipartFile
    )

    data class PostRentableProductStockImageOutputVo(
        @Schema(description = "rentableProductStockImage 고유값", required = true, example = "1")
        @JsonProperty("rentableProductStockImageUid")
        val rentableProductStockImageUid: Long,
        @Schema(description = "생성된 이미지 다운로드 경로", required = true, example = "https://testimage.com/sample.jpg")
        @JsonProperty("productStockImageFullUrl")
        val productStockImageFullUrl: String
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 이미지 파일 다운받기",
        description = "대여 가능 상품 재고 이미지를 by_product_files 위치에 저장했을 때 파일을 가져오기 위한 API"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/product-stock-image/{fileName}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun getProductStockImageFile(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "fileName", description = "by_product_files 폴더 안의 파일명", example = "test.jpg")
        @PathVariable("fileName")
        fileName: String
    ): ResponseEntity<Resource>? {
        return service.getProductStockImageFile(httpServletResponse, fileName)
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 이미지 삭제 <ADMIN>",
        description = "대여 상품 재고 이미지를 삭제합니다.<br>" +
                "상품 정보에 대표 이미지로 설정되어 있다면 대표 이미지 설정이 null 이 됩니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductImageUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/rentable-product-stock-image/{rentableProductStockImageUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun deleteRentableProductStockImage(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductStockImageUid", description = "rentableProductStockImage 고유값", example = "1")
        @PathVariable("rentableProductStockImageUid")
        rentableProductStockImageUid: Long
    ) {
        service.deleteRentableProductStockImage(httpServletResponse, authorization!!, rentableProductStockImageUid)
    }


    // ----
    @Operation(
        summary = "대여 가능 상품 대표 상품 재고 이미지 설정 수정 <ADMIN>",
        description = "대여 가능 상품 재고의 대표 상품 이미지 설정을 수정합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : rentableProductStockImageUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/rentable-product-stock-info/{rentableProductStockInfoUid}/front-image"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchRentableProductStockInfoFrontImage(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "rentableProductStockInfoUid", description = "rentableProductStockInfo 고유값", example = "1")
        @PathVariable("rentableProductStockInfoUid")
        rentableProductStockInfoUid: Long,
        @RequestBody
        inputVo: PatchRentableProductStockInfoFrontImageInputVo
    ) {
        service.patchRentableProductStockInfoFrontImage(
            httpServletResponse,
            authorization!!,
            rentableProductStockInfoUid,
            inputVo
        )
    }

    data class PatchRentableProductStockInfoFrontImageInputVo(
        @Schema(
            description = "rentableProductStockImage 고유값 (null 이라면 대표 이미지를 설정하지 않음)",
            required = false,
            example = "1"
        )
        @JsonProperty("rentableProductStockImageUid")
        val rentableProductStockImageUid: Long?
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 예약 정보의 예약 승인 처리 <ADMIN>",
        description = "대여 가능 상품 예약 정보를 예약 승인 처리합니다.<br>" +
                "상태 변경 철회 불가, 설명 수정은 가능<br>" +
                "rentable_product_reservation_state_change_history 에 예약 승인 히스토리를 추가합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 예약 승인 기한을 넘겼습니다.<br>" +
                                "3 : 예약 취소 승인 내역 있습니다.<br>" +
                                "4 : 예약 신청 거부 내역 있습니다.<br>" +
                                "5 : 예약 승인 내역 있습니다.<br>" +
                                "6 : 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일)",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-reservation-info/{rentableProductReservationInfoUid}/reservation-approve"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductReservationInfoReservationApprove(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductReservationInfoUid",
            description = "rentableProductReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductReservationInfoUid")
        rentableProductReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductReservationInfoReservationApproveInputVo
    ): PostRentableProductReservationInfoReservationApproveOutputVo? {
        return service.postRentableProductReservationInfoReservationApprove(
            httpServletResponse,
            authorization!!,
            rentableProductReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductReservationInfoReservationApproveInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductReservationInfoReservationApproveOutputVo(
        @Schema(description = "reservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("reservationStateChangeHistoryUid")
        val reservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 예약 정보의 예약 거부 처리 <ADMIN>",
        description = "대여 가능 상품 예약 정보를 예약 거부 처리합니다.<br>" +
                "상태 변경 철회 불가, 설명 수정은 가능<br>" +
                "rentable_product_reservation_state_change_history 에 예약 거부 히스토리를 추가합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 예약 승인 기한을 넘겼습니다.<br>" +
                                "3 : 예약 취소 승인 내역 있습니다.<br>" +
                                "4 : 예약 신청 거부 내역 있습니다.<br>" +
                                "5 : 예약 승인 내역 있습니다.<br>" +
                                "6 : 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일)",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-reservation-info/{rentableProductReservationInfoUid}/reservation-deny"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductReservationInfoReservationDeny(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductReservationInfoUid",
            description = "rentableProductReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductReservationInfoUid")
        rentableProductReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductReservationInfoReservationDenyInputVo
    ): PostRentableProductReservationInfoReservationDenyOutputVo? {
        return service.postRentableProductReservationInfoReservationDeny(
            httpServletResponse,
            authorization!!,
            rentableProductReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductReservationInfoReservationDenyInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductReservationInfoReservationDenyOutputVo(
        @Schema(description = "reservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("reservationStateChangeHistoryUid")
        val reservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 예약 정보의 예약 취소 승인 처리 <ADMIN>",
        description = "대여 가능 상품 예약 정보를 예약 취소 승인 처리합니다.<br>" +
                "상태 변경 철회 불가, 설명 수정은 가능<br>" +
                "rentable_product_reservation_state_change_history 에 예약 취소 승인 히스토리를 추가합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 대여 시작 기한 초과하였습니다.<br>" +
                                "3 : 예약 취소 승인 내역 있습니다.<br>" +
                                "4 : 예약 신청 거부 내역 있습니다.<br>" +
                                "5 : 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일)<br>" +
                                "6 : 예약 취소 신청 내역이 없습니다.<br>" +
                                "7 : 기존 예약 취소 신청에 대한 예약 취소 거부 상태입니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-reservation-info/{rentableProductReservationInfoUid}/reservation-cancel-approve"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductReservationInfoReservationCancelApprove(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductReservationInfoUid",
            description = "rentableProductReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductReservationInfoUid")
        rentableProductReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductReservationInfoReservationCancelApproveInputVo
    ): PostRentableProductReservationInfoReservationCancelApproveOutputVo? {
        return service.postRentableProductReservationInfoReservationCancelApprove(
            httpServletResponse,
            authorization!!,
            rentableProductReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductReservationInfoReservationCancelApproveInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductReservationInfoReservationCancelApproveOutputVo(
        @Schema(description = "reservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("reservationStateChangeHistoryUid")
        val reservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 예약 정보의 예약 취소 거부 처리 <ADMIN>",
        description = "대여 가능 상품 예약 정보를 예약 취소 거부 처리합니다.<br>" +
                "상태 변경 철회 불가, 설명 수정은 가능<br>" +
                "rentable_product_reservation_state_change_history 에 예약 취소 거부 히스토리를 추가합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 대여 시작 시간을 초과하였습니다.<br>" +
                                "3 : 예약 취소 승인 내역 있습니다.<br>" +
                                "4 : 예약 신청 거부 내역 있습니다.<br>" +
                                "5 : 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일)<br>" +
                                "6 : 예약 취소 신청 내역이 없습니다.<br>" +
                                "7 : 기존 예약 취소 신청에 대한 예약 취소 거부 상태입니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-reservation-info/{rentableProductReservationInfoUid}/reservation-cancel-deny"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductReservationInfoReservationCancelDeny(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductReservationInfoUid",
            description = "rentableProductReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductReservationInfoUid")
        rentableProductReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductReservationInfoReservationCancelDenyInputVo
    ): PostRentableProductReservationInfoReservationCancelDenyOutputVo? {
        return service.postRentableProductReservationInfoReservationCancelDeny(
            httpServletResponse,
            authorization!!,
            rentableProductReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductReservationInfoReservationCancelDenyInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductReservationInfoReservationCancelDenyOutputVo(
        @Schema(description = "reservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("reservationStateChangeHistoryUid")
        val reservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 예약 정보의 결제 확인 처리 <ADMIN>",
        description = "대여 가능 상품 예약 정보를 결제 확인 처리합니다.<br>" +
                "rentable_product_reservation_state_change_history 에 결제 확인 히스토리를 추가합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 결제 확인 내역이 존재합니다.<br>" +
                                "3 : 결제 확인 가능 기한을 초과하였습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-reservation-info/{rentableProductReservationInfoUid}/payment-complete"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductReservationInfoPaymentComplete(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductReservationInfoUid",
            description = "rentableProductReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductReservationInfoUid")
        rentableProductReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductReservationInfoPaymentCompleteInputVo
    ): PostRentableProductReservationInfoPaymentCompleteOutputVo? {
        return service.postRentableProductReservationInfoPaymentComplete(
            httpServletResponse,
            authorization!!,
            rentableProductReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductReservationInfoPaymentCompleteInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductReservationInfoPaymentCompleteOutputVo(
        @Schema(description = "reservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("reservationStateChangeHistoryUid")
        val reservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 예약 정보의 환불 완료 처리 <ADMIN>",
        description = "대여 가능 상품 예약 정보를 환불 완료 처리합니다.<br>" +
                "rentable_product_reservation_state_change_history 에 결제 확인 히스토리를 추가합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 결제 대기 상태입니다.<br>" +
                                "3 : 결제 확인 내역이 없습니다.<br>" +
                                "4 : 예약 취소 승인 내역이 없고 예약 신청 거부 내역이 없습니다.<br>" +
                                "5 : 이미 환불되었습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-reservation-info/{rentableProductReservationInfoUid}/refund-complete"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductReservationInfoRefundComplete(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductReservationInfoUid",
            description = "rentableProductReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductReservationInfoUid")
        rentableProductReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductReservationInfoRefundCompleteInputVo
    ): PostRentableProductReservationInfoRefundCompleteOutputVo? {
        return service.postRentableProductReservationInfoRefundComplete(
            httpServletResponse,
            authorization!!,
            rentableProductReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductReservationInfoRefundCompleteInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductReservationInfoRefundCompleteOutputVo(
        @Schema(description = "reservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("reservationStateChangeHistoryUid")
        val reservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 예약 상태 테이블의 상세 설명 수정 <ADMIN>",
        description = "대여 가능 상품 예약 상태 테이블의 상세 설명을 수정 처리합니다.<br>" +
                "한번 결정된 상태 코드는 변하지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : reservationStateChangeHistoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/reservation-state-change-history/{reservationStateChangeHistoryUid}/state-change-desc"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchReservationStateChangeHistoryStateChangeDesc(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "reservationStateChangeHistoryUid",
            description = "reservationStateChangeHistory 고유값",
            example = "1"
        )
        @PathVariable("reservationStateChangeHistoryUid")
        reservationStateChangeHistoryUid: Long,
        @RequestBody
        inputVo: PatchReservationStateChangeHistoryStateChangeDescInputVo
    ) {
        service.patchReservationStateChangeHistoryStateChangeDesc(
            httpServletResponse,
            authorization!!,
            reservationStateChangeHistoryUid,
            inputVo
        )
    }

    data class PatchReservationStateChangeHistoryStateChangeDescInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )


    // ----
    @Operation(
        summary = "개별 상품 반납 확인 <ADMIN>",
        description = "개별 상품에 대해 반납 확인 처리를 합니다.<br>" +
                "상품 준비시간 설정은 독립적이기에 취소되지 않습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 조기 반납 신고 상태가 아니고(내역이 없거나 취소 상태), 상품 반납일도 안됨<br>" +
                                "3 : 반납 확인 상태입니다.<br>" +
                                "4 : 결제 확인 완료 아님 || 예약 거부 상태 = 대여 진행 상태가 아님",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-reservation-info/{rentableProductStockReservationInfoUid}/return-check"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockReservationInfoReturnCheck(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockReservationInfoUid",
            description = "rentableProductStockReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockReservationInfoUid")
        rentableProductStockReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductStockReservationInfoReturnCheckInputVo
    ): PostRentableProductStockReservationInfoReturnCheckOutputVo? {
        return service.postRentableProductStockReservationInfoReturnCheck(
            httpServletResponse,
            authorization!!,
            rentableProductStockReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductStockReservationInfoReturnCheckInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductStockReservationInfoReturnCheckOutputVo(
        @Schema(description = "stockReservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("stockReservationStateChangeHistoryUid")
        val stockReservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "개별 상품 반납 확인 취소 <ADMIN>",
        description = "개별 상품에 대해 반납 확인 취소 처리를 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 상품의 조기 반납 신고 내역도 없고, 상품 반납일도 도래하지 않았습니다.<br>" +
                                "3 : 이미 반납 확인 취소 처리되었습니다.<br>" +
                                "4 : 결제 확인 완료 아님 || 예약 거부 상태 = 대여 진행 상태가 아님",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-reservation-info/{rentableProductStockReservationInfoUid}/return-check-cancel"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockReservationInfoReturnCheckCancel(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockReservationInfoUid",
            description = "rentableProductStockReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockReservationInfoUid")
        rentableProductStockReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductStockReservationInfoReturnCheckCancelInputVo
    ): PostRentableProductStockReservationInfoReturnCheckCancelOutputVo? {
        return service.postRentableProductStockReservationInfoReturnCheckCancel(
            httpServletResponse,
            authorization!!,
            rentableProductStockReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductStockReservationInfoReturnCheckCancelInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductStockReservationInfoReturnCheckCancelOutputVo(
        @Schema(description = "stockReservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("stockReservationStateChangeHistoryUid")
        val stockReservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "개별 상품 준비 완료 일시 설정 <ADMIN>",
        description = "개별 상품에 대해 준비 완료 일시를 설정 합니다.<br>" +
                "readyDatetime 변수를 미래로 설정하는 식으로 미리 준비 설정을 할 수도 있습니다.<br>" +
                "다른 모든 상태 정보에 앞서며, 연체 처리, 손망실 처리를 하면 이 설정이 지워집니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : readyDatetime 를 null 로 하려 할 때, 이미 해당 상품이 다른 예약을 진행중입니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/rentable-product-stock-reservation-info/{rentableProductStockReservationInfoUid}/ready"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchRentableProductStockReservationInfoReady(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockReservationInfoUid",
            description = "rentableProductStockReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockReservationInfoUid")
        rentableProductStockReservationInfoUid: Long,
        @RequestBody
        inputVo: PatchRentableProductStockReservationInfoReadyInputVo
    ) {
        service.patchRentableProductStockReservationInfoReady(
            httpServletResponse,
            authorization!!,
            rentableProductStockReservationInfoUid,
            inputVo
        )
    }

    data class PatchRentableProductStockReservationInfoReadyInputVo(
        @Schema(
            description = "상품 준비 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, null 이라면 설정 해제)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("readyDatetime")
        val readyDatetime: String?
    )


    // ----
    @Operation(
        summary = "개별 상품 연체 상태 변경 <ADMIN>",
        description = "개별 상품에 대해 연체 상태로 변경 처리를 합니다.<br>" +
                "연체 상태가 되면 기존 상품 준비일은 null 이 됩니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 상품 반납일이 도래하지 않았습니다.<br>" +
                                "3 : 이미 연체 상태입니다.<br>" +
                                "4 : 현재 손망실 상태입니다.<br>" +
                                "5 : 이미 반납 확인을 한 상태입니다.<br>" +
                                "6 : 결제 확인 완료 아님 || 예약 거부 상태 = 대여 진행 상태가 아님",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-reservation-info/{rentableProductStockReservationInfoUid}/overdue"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockReservationInfoOverdue(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockReservationInfoUid",
            description = "rentableProductStockReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockReservationInfoUid")
        rentableProductStockReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductStockReservationInfoOverdueInputVo
    ): PostRentableProductStockReservationInfoOverdueOutputVo? {
        return service.postRentableProductStockReservationInfoOverdue(
            httpServletResponse,
            authorization!!,
            rentableProductStockReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductStockReservationInfoOverdueInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductStockReservationInfoOverdueOutputVo(
        @Schema(description = "stockReservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("stockReservationStateChangeHistoryUid")
        val stockReservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "개별 상품 연체 상태 변경 취소 <ADMIN>",
        description = "개별 상품에 대해 연체 상태 변경 취소 처리를 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 연체 상태 설정 내역이 없습니다.<br>" +
                                "3 : 연제 상태 변경 취소 상태입니다.<br>" +
                                "4 : 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-reservation-info/{rentableProductStockReservationInfoUid}/overdue-cancel"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockReservationInfoOverdueCancel(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockReservationInfoUid",
            description = "rentableProductStockReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockReservationInfoUid")
        rentableProductStockReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductStockReservationInfoOverdueCancelInputVo
    ): PostRentableProductStockReservationInfoOverdueCancelOutputVo? {
        return service.postRentableProductStockReservationInfoOverdueCancel(
            httpServletResponse,
            authorization!!,
            rentableProductStockReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductStockReservationInfoOverdueCancelInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductStockReservationInfoOverdueCancelOutputVo(
        @Schema(description = "stockReservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("stockReservationStateChangeHistoryUid")
        val stockReservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "개별 상품 손망실 상태 변경 <ADMIN>",
        description = "개별 상품에 대해 손망실 상태로 변경 처리를 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 상품 대여일이 도래하지 않았습니다.<br>" +
                                "3 : 현재 연체 상태입니다.<br>" +
                                "4 : 이미 손망실 상태입니다.<br>" +
                                "5 : 이미 반납 확인을 한 상태입니다.<br>" +
                                "6 : 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-reservation-info/{rentableProductStockReservationInfoUid}/lost"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockReservationInfoLost(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockReservationInfoUid",
            description = "rentableProductStockReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockReservationInfoUid")
        rentableProductStockReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductStockReservationInfoLostInputVo
    ): PostRentableProductStockReservationInfoLostOutputVo? {
        return service.postRentableProductStockReservationInfoLost(
            httpServletResponse,
            authorization!!,
            rentableProductStockReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductStockReservationInfoLostInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductStockReservationInfoLostOutputVo(
        @Schema(description = "stockReservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("stockReservationStateChangeHistoryUid")
        val stockReservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "개별 상품 손망실 상태 변경 취소 <ADMIN>",
        description = "개별 상품에 대해 손망실 상태 취소 처리를 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : rentableProductStockReservationInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 손망실 상태 변경 내역이 없습니다.<br>" +
                                "3 : 손망실 상태 변경 취소 상태입니다.<br>" +
                                "4 : 결제 확인 완료 아님 || 예약 신청 거부 = 대여 진행 상태가 아님",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rentable-product-stock-reservation-info/{rentableProductStockReservationInfoUid}/lost-cancel"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductStockReservationInfoLostCancel(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "rentableProductStockReservationInfoUid",
            description = "rentableProductStockReservationInfo 고유값",
            example = "1"
        )
        @PathVariable("rentableProductStockReservationInfoUid")
        rentableProductStockReservationInfoUid: Long,
        @RequestBody
        inputVo: PostRentableProductStockReservationInfoLostCancelInputVo
    ): PostRentableProductStockReservationInfoLostCancelOutputVo? {
        return service.postRentableProductStockReservationInfoLostCancel(
            httpServletResponse,
            authorization!!,
            rentableProductStockReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductStockReservationInfoLostCancelInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductStockReservationInfoLostCancelOutputVo(
        @Schema(description = "stockReservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("stockReservationStateChangeHistoryUid")
        val stockReservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "개별 상품 예약 상태 테이블의 상세 설명 수정 <ADMIN>",
        description = "개별 상품 예약 상태 테이블의 상세 설명을 수정 처리합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : stockReservationStateChangeHistoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/stock-reservation-state-change-history/{stockReservationStateChangeHistoryUid}/state-change-desc"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchStockReservationStateChangeHistoryStateChangeDesc(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(
            name = "stockReservationStateChangeHistoryUid",
            description = "stockReservationStateChangeHistory 고유값",
            example = "1"
        )
        @PathVariable("stockReservationStateChangeHistoryUid")
        stockReservationStateChangeHistoryUid: Long,
        @RequestBody
        inputVo: PatchStockReservationStateChangeHistoryStateChangeDescInputVo
    ) {
        service.patchStockReservationStateChangeHistoryStateChangeDesc(
            httpServletResponse,
            authorization!!,
            stockReservationStateChangeHistoryUid,
            inputVo
        )
    }

    data class PatchStockReservationStateChangeHistoryStateChangeDescInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )


    // todo : Admin 관련 필요 정보 조회 API 들 추가
}