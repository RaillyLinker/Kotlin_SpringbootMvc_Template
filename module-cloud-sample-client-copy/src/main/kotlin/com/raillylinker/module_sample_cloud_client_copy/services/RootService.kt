package com.raillylinker.module_sample_cloud_client_copy.services

import com.raillylinker.module_sample_cloud_client_copy.controllers.RootController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.ModelAndView

interface RootService {
    // (루트 홈페이지 반환 함수)
    fun getRootHomePage(
        httpServletResponse: HttpServletResponse
    ): ModelAndView?


    ////
    // (현재 시간 반환 API)
    fun getCurrentTime(
        httpServletResponse: HttpServletResponse
    ): RootController.GetCurrentTimeOutputVo?
}