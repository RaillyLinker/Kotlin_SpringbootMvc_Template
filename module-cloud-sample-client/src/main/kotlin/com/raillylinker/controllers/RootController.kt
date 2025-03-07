package com.raillylinker.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.services.RootService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@Tag(name = "root APIs", description = "Root 경로에 대한 API 컨트롤러")
@Controller
class RootController(
    private val service: RootService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "루트 경로",
        description = "루트 경로 정보를 반환합니다."
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
        path = ["", "/"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.TEXT_HTML_VALUE]
    )
    fun getRootInfo(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): ModelAndView? {
        return service.getRootInfo(httpServletResponse)
    }


    // ----
    @Operation(
        summary = "현재 시간 반환 API",
        description = "서버의 현재 시간을 반환합니다."
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
        path = ["/current-time"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun getCurrentTime(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): GetCurrentTimeOutputVo? {
        return service.getCurrentTime(httpServletResponse)
    }

    data class GetCurrentTimeOutputVo(
        @Schema(description = "현재 시간(yyyy_MM_dd_'T'_HH_mm_ss_SSSSSSS)", required = true, example = "2024-12-11T14:21:27.1765398")
        @JsonProperty("currentTime")
        val currentTime: String
    )
}