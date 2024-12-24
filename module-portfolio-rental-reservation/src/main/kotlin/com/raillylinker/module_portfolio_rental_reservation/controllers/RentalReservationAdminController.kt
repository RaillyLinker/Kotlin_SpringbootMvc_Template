package com.raillylinker.module_portfolio_rental_reservation.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_portfolio_rental_reservation.services.RentalReservationAdminService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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
        summary = "대여 가능 상품 등록 (더미)", // todo
        description = "대여 상품 정보를 등록합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
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
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
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
        @ModelAttribute
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
            description = "상품 카테고리 (ex : 동물|개과|치와와, 예시와 같이 |로 대분류 -> 소분류 순으로 구분)",
            required = true,
            example = "동물|개과|치와와"
        )
        @JsonProperty("productCategory")
        val productCategory: String,
        @Schema(
            description = "고객에게 보일 상품 소개",
            required = true,
            example = "예약해주세요."
        )
        @JsonProperty("productIntro")
        val productIntro: String,
        @Schema(description = "고객에게 보일 상품 썸네일 이미지", required = false)
        @JsonProperty("thumbnailImage")
        val thumbnailImage: MultipartFile?,
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
            description = "상품 예약이 가능한 최초 일시(콘서트 티켓 예매 선공개와 기능을 가정)(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
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
        val reservationUnitPrice: Long,
        @Schema(
            description = "회수 준비 시간 (분, 대여 상품 반납일시로부터 다음 대여까지 걸리는 시간)",
            required = true,
            example = "10"
        )
        @JsonProperty("preparationMinute")
        val preparationMinute: Long,
        @Schema(
            description = "예약 가능 설정 (재고, 상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정)",
            required = true,
            example = "true"
        )
        @JsonProperty("nowReservable")
        val nowReservable: Boolean
    )

    data class PostRentableProductInfoOutputVo(
        @Schema(description = "rentableProductInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductInfoUid")
        val rentableProductInfoUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 추가 예약 가능 설정 수정 (더미)", // todo
        description = "대여 가능 상품을 현 시간부로 예약 가능하게 할 것인지에 대한 스위치 플래그 수정"
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
        summary = "대여 가능 상품 재고 등록 (더미)", // todo
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
        path = ["/rentable-product-stock-info"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
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
        @ModelAttribute
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
            description = "대여 가능 상품 개별 설명",
            required = true,
            example = "상태 양호"
        )
        @JsonProperty("productDesc")
        val productDesc: String,
        @Schema(description = "대여 가능 상품 개별 썸네일", required = false)
        @JsonProperty("thumbnailImage")
        val thumbnailImage: MultipartFile?,
        @Schema(
            description = "제품 대여(손님에게 제공)가 가능한 최초 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("firstRentableDatetime")
        val firstRentableDatetime: String,
        @Schema(
            description = "제품을 최종 회수하는 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, 본 제품 소유 기한이 정해져서 이 시간까지 회수가 이루어져야 하는 경우 입력)",
            required = false,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("storageDatetime")
        val storageDatetime: String?,
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
        summary = "대여 가능 상품 추가 예약 가능 설정 수정 (더미)", // todo
        description = "대여 가능 상품을 현 시간부로 예약 가능하게 할 것인지에 대한 스위치 플래그 수정"
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


    // todo : 아래에는 예약 상태에 따라 처리하기

    // todo : 대여 가능 상품 정보 수정 (except now_reservable)

    // todo : 대여 가능 상품 삭제

    // todo : 대여 가능 상품 재고 정보 수정 (except now_reservable)

    // todo : 대여 가능 상품 재고 삭제




    // todo : 대여 가능 상품 예약 상태 변경 히스토리 등록 (현 상태에 따라 상태 변화 가능한 방향으로만 변경 가능, 삭제 및 수정 불가)




    // todo : Admin 관련 필요 정보 Read API 궁리
}