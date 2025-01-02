package com.raillylinker.module_service_rental_reservation.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_service_rental_reservation.services.RentalReservationService
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

@Tag(name = "/rental-reservation APIs", description = "대여 예약 서비스 API 컨트롤러")
@Controller
@RequestMapping("/rental-reservation")
class RentalReservationController(
    private val service: RentalReservationService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "상품 예약 신청하기 <> (더미)", // todo
        description = "상품에 대한 예약 신청을 합니다."
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
                                "2 : rentalStartDatetime 가 rentalEndDatetime 보다 커서는 안됩니다.<br>" +
                                "3 : 예약 상품 버전 시퀀스가 일치하지 않습니다.<br>" +
                                "4 : 예약 상품이 현재 예약 가능 상태가 아닙니다.<br>" +
                                "5 : 상품 예약 가능 일시 이전입니다.<br>" +
                                "6 : 대여 단위 예약 최소 횟수보다 작습니다.<br>" +
                                "7 : 대여 단위 예약 최대 횟수보다 큽니다.<br>" +
                                "8 : <br>" + // todo
                                "9 : 재고 리스트 중 없는 개체가 있습니다.<br>" +
                                "10 : 재고 리스트 중 대여 가능 설정이 아닌 상품이 있습니다.<br>" +
                                "11 : 재고 리스트 중 대여 가능 최초 일시가 대여 시작일보다 큰 상품이 있습니다.<br>" +
                                "12 : 재고 리스트 중 대여 가능 마지막 일시가 대여 마지막일보다 작은 개체가 있습니다.<br>" +
                                "13 : 재고 리스트 중 현재 예약 중인 개체가 있습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/product-reservation"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun postProductReservation(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostProductReservationInputVo
    ): PostProductReservationOutputVo? {
        return service.postProductReservation(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PostProductReservationInputVo(
        @Schema(description = "예약 상품 고유번호", required = true, example = "1")
        @JsonProperty("rentableProductInfoUid")
        val rentableProductInfoUid: Long,
        @Schema(description = "예약 상품 버전 시퀀스(현재 상품 테이블 버전과 맞지 않는다면 진행 불가)", required = true, example = "1")
        @JsonProperty("rentableProductVersionSeq")
        val rentableProductVersionSeq: Long,
        @Schema(description = "예약 상품 재고 고유번호 리스트", required = true)
        @JsonProperty("rentableProductStockInfoUidList")
        val rentableProductStockInfoUidList: List<Long>,
        @Schema(
            description = "대여 시작 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("rentalStartDatetime")
        val rentalStartDatetime: String,
        @Schema(
            description = "대여 끝 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("rentalEndDatetime")
        val rentalEndDatetime: String
    )

    data class PostProductReservationOutputVo(
        @Schema(description = "rentableProductReservationInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductReservationInfoUid")
        val rentableProductReservationInfoUid: Long,
        @Schema(
            description = "고객 결제 기한 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("paymentDeadlineDatetime")
        val paymentDeadlineDatetime: String,
        @Schema(
            description = "취소 가능 기한 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("cancelableDeadlineDatetime")
        val cancelableDeadlineDatetime: String
    )


    // ----
    @Operation(
        summary = "예약 취소 신청 <>",
        description = "예약 취소 신청을 합니다."
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
                                "1 : rentableProductReservationInfo 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 예약 취소 가능 기한 초과<br>" +
                                "3 : 예약 취소 승인 상태<br>" +
                                "4 : 예약 신청 거부 상태<br>" +
                                "5 : 미결제 상태 & 결제 기한 초과 상태(= 취소와 동일)<br>" +
                                "6 : 예약 취소 신청 상태",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/cancel-product-reservation"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun postCancelProductReservation(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostCancelProductReservationInputVo
    ): PostCancelProductReservationOutputVo? {
        return service.postCancelProductReservation(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PostCancelProductReservationInputVo(
        @Schema(description = "rentableProductReservationInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductReservationInfoUid")
        val rentableProductReservationInfoUid: Long,
        @Schema(description = "예약 취소 사유", required = true, example = "개인 사유")
        @JsonProperty("cancelReason")
        val cancelReason: String
    )

    data class PostCancelProductReservationOutputVo(
        @Schema(description = "rentableProductReservationStateChangeInfo 고유값", required = true, example = "1")
        @JsonProperty("stateChangeInfoUid")
        val stateChangeInfoUid: Long,
        @Schema(
            description = "예약 취소 즉시 승인시 rentableProductReservationStateChangeInfo 고유값",
            required = false,
            example = "1"
        )
        @JsonProperty("stateChangeInfoUidForApproved")
        val stateChangeInfoUidForApproved: Long?,
    )


    // ----
    @Operation(
        summary = "대여품 조기반납 신고 <>",
        description = "대여품을 조기반납 신고합니다."
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
                                "1 : rentableProductReservationInfo 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 조기 반납 기준 일시가 현재보다 앞으로 설정됨<br>" +
                                "3 : 조기 반납 기준 일시가 대여 마지막 일시보다 뒤로 설정됨<br>" +
                                "4 : 조기 반납 기준 일시가 대여 시작일보다 앞으로 설정됨<br>" +
                                "5 : 결재 대기 상태입니다<br>" +
                                "6 : 미결제 상태입니다<br>" +
                                "7 : 예약 취소 승인 상태입니다.<br>" +
                                "8 : 예약 신청 승인 처리가 되지 않음",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/rental-product-early-return"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun postRentalProductEarlyReturn(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostRentalProductEarlyReturnInputVo
    ): PostRentalProductEarlyReturnOutputVo? {
        return service.postRentalProductEarlyReturn(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PostRentalProductEarlyReturnInputVo(
        @Schema(description = "rentableProductReservationInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductReservationInfoUid")
        val rentableProductReservationInfoUid: Long,
        @Schema(description = "조기 반납 사유", required = true, example = "개인 사유")
        @JsonProperty("earlyReturnReason")
        val earlyReturnReason: String,
        @Schema(
            description = "조기 반납 기준일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("stateChangeDatetime")
        val stateChangeDatetime: String
    )

    data class PostRentalProductEarlyReturnOutputVo(
        @Schema(description = "rentableProductReservationStateChangeInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductReservationStateChangeInfoUid")
        val rentableProductReservationStateChangeInfoUid: Long
    )

    // todo : 필요 정보 조회 API 들 추가
}