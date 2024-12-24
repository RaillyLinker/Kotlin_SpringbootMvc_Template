package com.raillylinker.module_portfolio_rental_reservation.controllers

import com.raillylinker.module_portfolio_rental_reservation.services.RentalReservationAdminService
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
        summary = "ADMIN 권한 진입 테스트 <'ADMIN'>",
        description = "ADMIN 권한이 있어야 진입 가능"
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
    @GetMapping(
        path = ["/for-admin"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.TEXT_PLAIN_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun adminAccessTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?
    ): String? {
        return service.adminAccessTest(httpServletResponse, authorization!!)
    }

    // todo : 대여 가능 상품 등록
    // todo : 대여 가능 상품 정보 수정
    // todo : 대여 가능 상품 삭제
    // todo : 대여 가능 상품 재고 등록
    // todo : 대여 가능 상품 재고 정보 수정
    // todo : 대여 가능 상품 재고 삭제
    // todo : 대여 가능 상품 예약 상태 변경 히스토리 등록 (현 상태에 따라 상태 변화 가능한 방향으로만 변경 가능, 삭제 및 수정 불가)
    // todo : Admin 관련 필요 정보 Read API 궁리
}