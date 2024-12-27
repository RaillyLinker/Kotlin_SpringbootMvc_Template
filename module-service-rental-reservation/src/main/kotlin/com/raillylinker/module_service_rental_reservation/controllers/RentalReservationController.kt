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
                                "2 : rentableProductVersionSeq 가 맞지 않습니다. (결재 시점에 테이블 정보가 수정됨)",
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
            description = "결재 기한 일시(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
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
        summary = "상품 결재 처리 <> (더미)", // todo
        description = "상품 결재 처리(결재 모듈에서 처리한 결재 번호와 상품 번호, 환불은 환불 모듈에서)"
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
                                "2 : paymentUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
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
        path = ["/rentable-product-reservation-payment-info"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun postRentableProductReservationPaymentInfo(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostRentableProductReservationPaymentInfoInputVo
    ): PostRentableProductReservationPaymentInfoOutputVo? {
        return service.postRentableProductReservationPaymentInfo(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PostRentableProductReservationPaymentInfoInputVo(
        @Schema(description = "rentableProductReservationInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductReservationInfoUid")
        val rentableProductReservationInfoUid: Long,
        @Schema(description = "payment 고유값", required = true, example = "1")
        @JsonProperty("paymentUid")
        val paymentUid: Long
    )

    data class PostRentableProductReservationPaymentInfoOutputVo(
        @Schema(description = "rentableProductReservationPaymentInfo 고유값", required = true, example = "1")
        @JsonProperty("rentableProductReservationPaymentInfoUid")
        val rentableProductReservationPaymentInfoUid: Long
    )

    // todo : 예약 취소 신청

    // todo : 조기 반납 신청

    // todo : 필요 정보 조회 API 들 추가
}