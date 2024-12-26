package com.raillylinker.module_service_rental_reservation.controllers

import com.raillylinker.module_service_rental_reservation.services.RentalReservationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
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


    // ----
    @Operation(
        summary = "로그인 진입 테스트 <>",
        description = "로그인 되어 있어야 진입 가능"
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
            )
        ]
    )
    @GetMapping(
        path = ["/for-logged-in"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun loggedInAccessTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?
    ): String? {
        return service.loggedInAccessTest(httpServletResponse, authorization!!)
    }


    // todo : 상품 예약 신청
    // todo : 예약 취소 신청
    // todo : 조기 반납 신청

    // todo : 결재 모듈 추가하기?

    // todo : 필요 정보 Read API 궁리
}