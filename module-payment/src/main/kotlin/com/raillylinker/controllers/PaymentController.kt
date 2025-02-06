package com.raillylinker.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.services.PaymentService
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
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@Tag(name = "/payment APIs", description = "결제 API 컨트롤러")
@Controller
@RequestMapping("/payment")
class PaymentController(
    private val service: PaymentService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "수동 결제 요청",
        description = "수동 결제 요청 API"
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
                                "1 : 통화 코드값의 길이는 3이어야 합니다.",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @PostMapping(
        path = ["/bank-transfer/request"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun postBankTransferRequest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: PostBankTransferRequestInputVo
    ): PostBankTransferRequestOutputVo? {
        return service.postBankTransferRequest(
            httpServletResponse,
            inputVo
        )
    }

    data class PostBankTransferRequestInputVo(
        @Schema(
            description = "결제 코드입니다. 외부 모듈에서 결제를 의뢰할 때에 구분을 위해 입력하는 정보로, {모듈 고유값}_{모듈 내 고유값} 으로 이루어집니다.",
            required = true,
            example = "module1_uid1"
        )
        @JsonProperty("paymentCode")
        val paymentCode: String,
        @Schema(description = "결제 금액", required = true, example = "1000")
        @JsonProperty("paymentAmount")
        val paymentAmount: BigDecimal,
        @Schema(
            description = "결제 금액 통화 코드(IOS 4217, ex : KRW, USD, EUR...)",
            required = true,
            example = "KRW"
        )
        @JsonProperty("currencyCode")
        val currencyCode: String,
        @Schema(description = "결제 이유", required = true, example = "상품 구입")
        @JsonProperty("paymentReason")
        val paymentReason: String,
        @Schema(description = "입금 받을 은행명", required = true, example = "서울은행")
        @JsonProperty("receiveBankName")
        val receiveBankName: String,
        @Schema(description = "입금 받을 은행 계좌번호", required = true, example = "11-11111-1111")
        @JsonProperty("receiveBankAccount")
        val receiveBankAccount: String,
        @Schema(description = "입금자 이름", required = false, example = "홍길동")
        @JsonProperty("depositoryName")
        val depositoryName: String?
    )

    data class PostBankTransferRequestOutputVo(
        @Schema(description = "payment request 고유값", required = true, example = "1")
        @JsonProperty("paymentRequestUid")
        val paymentRequestUid: Long
    )

    // todo : 수동 결제 전액 환불 신청
    // todo : 수동 결제 부분 환불 신청

    // todo : PG 결제 요청
    // todo : PG 결제 요청 billing pay
    // todo : PG 결제 전액 환불 신청
    // todo : PG 결제 부분 환불 신청
}