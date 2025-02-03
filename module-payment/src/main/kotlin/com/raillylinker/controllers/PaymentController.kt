package com.raillylinker.controllers

import com.raillylinker.services.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

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
        summary = "비 로그인 접속 테스트",
        description = "비 로그인 접속 테스트용 API"
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
        path = ["/for-no-logged-in"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    @ResponseBody
    fun noLoggedInAccessTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): String? {
        return service.noLoggedInAccessTest(httpServletResponse)
    }

    // todo : 수동 결제 요청
    // todo : 수동 결제 전액 환불 신청
    // todo : 수동 결제 부분 환불 신청

    // todo : PG 결제 요청
    // todo : PG 결제 요청 billing pay
    // todo : PG 결제 전액 환불 신청
    // todo : PG 결제 부분 환불 신청
}