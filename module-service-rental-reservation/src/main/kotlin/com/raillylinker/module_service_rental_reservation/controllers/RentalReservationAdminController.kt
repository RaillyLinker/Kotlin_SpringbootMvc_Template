package com.raillylinker.module_service_rental_reservation.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_service_rental_reservation.services.RentalReservationAdminService
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
        summary = "예약 상품 카테고리 정보 등록 <ADMIN> (더미)", // todo
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
        summary = "예약 상품 카테고리 정보 수정 <ADMIN> (더미)", // todo
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
        summary = "예약 상품 카테고리 정보 삭제 <ADMIN> (더미)", // todo
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
        summary = "대여 가능 상품 등록 <ADMIN> (더미)", // todo
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
        val reservationUnitPrice: Long,
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
        summary = "대여 가능 상품 수정 <ADMIN> (더미)", // todo
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
                                "2 : rentableProductCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
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
        val reservationUnitPrice: Long,
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
        summary = "대여 가능 상품 추가 예약 가능 설정 수정 <ADMIN> (더미)", // todo
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
        summary = "대여 가능 상품 최소 예약 횟수 설정 수정 <ADMIN> (더미)", // todo
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
                                "2 : 최소 예약 횟수는 최대 예약 횟수보다 작거나 같아야 합니다.",
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
        summary = "대여 가능 상품 최대 예약 횟수 설정 수정 <ADMIN> (더미)", // todo
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
            required = true,
            example = "1"
        )
        @JsonProperty("maximumReservationUnitCount")
        val maximumReservationUnitCount: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 이미지 등록 <ADMIN> (더미)", // todo
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
        @Schema(description = "rentableProductInfo 고유값", required = true)
        @JsonProperty("rentableProductInfoUid")
        val rentableProductInfoUid: Long,
        @Schema(description = "고객에게 보일 상품 썸네일 이미지", required = true)
        @JsonProperty("thumbnailImage")
        val thumbnailImage: MultipartFile
    )

    data class PostRentableProductImageOutputVo(
        @Schema(description = "rentableProductImage 고유값", required = true, example = "1")
        @JsonProperty("rentableProductImageUid")
        val rentableProductImageUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 이미지 삭제 <ADMIN> (더미)", // todo
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
        summary = "대여 가능 상품 대표 상품 이미지 설정 수정 <ADMIN> (더미)", // todo
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
        summary = "예약 상품 재고 카테고리 정보 등록 <ADMIN> (더미)", // todo
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
        summary = "예약 상품 재고 카테고리 정보 수정 <ADMIN> (더미)", // todo
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
        summary = "예약 상품 재고 카테고리 정보 삭제 <ADMIN> (더미)", // todo
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
        summary = "대여 가능 상품 재고 등록 <ADMIN> (더미)", // todo
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

    data class PostRentableProductStockInfoOutputVo(
        @Schema(description = "rentableProductStockInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductStockInfoUid")
        val rentableProductStockInfoUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 수정 <ADMIN> (더미)", // todo
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
        @ModelAttribute
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
        summary = "대여 가능 상품 추가 예약 가능 설정 수정 <ADMIN> (더미)", // todo
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


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 이미지 등록 <ADMIN> (더미)", // todo
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
        @Schema(description = "rentableProductStockInfo 고유값", required = true)
        @JsonProperty("rentableProductInfoStockUid")
        val rentableProductInfoStockUid: Long,
        @Schema(description = "고객에게 보일 상품 썸네일 이미지", required = true)
        @JsonProperty("thumbnailImage")
        val thumbnailImage: MultipartFile
    )

    data class PostRentableProductStockImageOutputVo(
        @Schema(description = "rentableProductStockImage 고유값", required = true, example = "1")
        @JsonProperty("rentableProductStockImageUid")
        val rentableProductStockImageUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 재고 이미지 삭제 <ADMIN> (더미)", // todo
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
        summary = "대여 가능 상품 대표 상품 재고 이미지 설정 수정 <ADMIN> (더미)", // todo
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
        summary = "대여 가능 상품 예약 정보의 결재 완료 처리 <ADMIN> (더미)", // todo
        description = "대여 가능 상품 예약 정보를 결재 완료 처리합니다.<br>" +
                "상태 변경 철회 불가, 설명 수정은 가능<br>" +
                "rentable_product_reservation_state_change_history 에 결재 완료 히스토리를 추가합니다."
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
                                "2 : 결재 완료 처리가 가능한 상태가 아닙니다.",
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
        summary = "대여 가능 상품 예약 정보의 예약 승인 처리 <ADMIN> (더미)", // todo
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
                                "2 : 예약 승인 처리가 가능한 상태가 아닙니다.",
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
        summary = "대여 가능 상품 예약 정보의 예약 거부 처리 <ADMIN> (더미)", // todo
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
                                "2 : 예약 거부 처리가 가능한 상태가 아닙니다.",
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
        summary = "대여 가능 상품 예약 정보의 예약 취소 승인 처리 <ADMIN> (더미)", // todo
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
                                "2 : 예약 취소 승인 처리가 가능한 상태가 아닙니다.",
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
        summary = "대여 가능 상품 예약 정보의 예약 취소 거부 처리 <ADMIN> (더미)", // todo
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
                                "2 : 예약 거부 처리가 가능한 상태가 아닙니다.",
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
        summary = "대여 가능 상품 예약 정보의 환불 완료 처리 <ADMIN> (더미)", // todo
        description = "대여 가능 상품 예약 정보를 환불 완료 처리합니다.<br>" +
                "상태 변경 철회 불가, 설명 수정은 가능<br>" +
                "rentable_product_reservation_state_change_history 에 환불 완료 히스토리를 추가합니다."
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
                                "2 : 환불 완료 처리가 가능한 상태가 아닙니다.",
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
        summary = "대여 가능 상품 예약 정보의 조기 반납 확인 처리 <ADMIN> (더미)", // todo
        description = "대여 가능 상품 예약 정보를 조기 반납 확인 처리합니다.<br>" +
                "상태 변경 철회 불가, 설명 수정은 가능<br>" +
                "rentable_product_reservation_state_change_history 에 조기 반납 확인 히스토리를 추가합니다."
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
                                "2 : 조기 반납 확인 처리가 가능한 상태가 아닙니다.",
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
        path = ["/rentable-product-reservation-info/{rentableProductReservationInfoUid}/early-return-complete"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun postRentableProductReservationInfoEarlyReturnComplete(
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
        inputVo: PostRentableProductReservationInfoEarlyReturnCompleteInputVo
    ): PostRentableProductReservationInfoEarlyReturnCompleteOutputVo? {
        return service.postRentableProductReservationInfoEarlyReturnComplete(
            httpServletResponse,
            authorization!!,
            rentableProductReservationInfoUid,
            inputVo
        )
    }

    data class PostRentableProductReservationInfoEarlyReturnCompleteInputVo(
        @Schema(description = "상태 변경 상세 설명", required = true, example = "이상무")
        @JsonProperty("stateChangeDesc")
        val stateChangeDesc: String
    )

    data class PostRentableProductReservationInfoEarlyReturnCompleteOutputVo(
        @Schema(description = "reservationStateChangeHistory 고유값", required = true, example = "1")
        @JsonProperty("reservationStateChangeHistoryUid")
        val reservationStateChangeHistoryUid: Long
    )


    // ----
    @Operation(
        summary = "대여 가능 상품 예약 상태 테이블의 상세 설명 수정 <ADMIN> (더미)", // todo
        description = "대여 가능 상품 예약 상태 테이블의 상세 설명을 수정 처리합니다."
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
        produces = [MediaType.APPLICATION_JSON_VALUE]
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
        summary = "개별 상품 예약 정보 다음 준비 예정일 수정 <ADMIN> (더미)", // todo
        description = "개별 상품 예약 정보의 다음 준비 예정일을 수정합니다."
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
                                "2 : 준비 예정일이 현재 상태와 맞지 않습니다.(현재 예약 상태를 파악해서 반납이 이루어지지 않았을 때)",
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
        path = ["/rentable-product-stock-reservation-info/{rentableProductStockReservationInfoUid}/next-ready-datetime"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun patchRentableProductStockReservationInfoNextReadyDatetime(
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
        inputVo: PatchRentableProductStockReservationInfoNextReadyDatetimeInputVo
    ) {
        service.patchRentableProductStockReservationInfoNextReadyDatetime(
            httpServletResponse,
            authorization!!,
            rentableProductStockReservationInfoUid,
            inputVo
        )
    }

    data class PatchRentableProductStockReservationInfoNextReadyDatetimeInputVo(
        @Schema(
            description = "개별 상품 예약 정보 다음 준비 예정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("nextReadyDatetime")
        val nextReadyDatetime: String
    )

    // todo : Admin 관련 필요 정보 조회 API 들 추가
}