package com.raillylinker.module_sample_retrofit2.services

import com.raillylinker.module_sample_retrofit2.controllers.Retrofit2TestController
import jakarta.servlet.http.HttpServletResponse

interface Retrofit2TestService {
    // (기본 요청 테스트)
    fun basicRequestTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (Redirect 테스트)
    fun redirectTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (Forward 테스트)
    fun forwardTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (Get 요청 테스트 (Query Parameter))
    fun getRequestTest(httpServletResponse: HttpServletResponse): Retrofit2TestController.GetRequestTestOutputVo?


    ////
    // (Get 요청 테스트 (Path Parameter))
    fun getRequestTestWithPathParam(httpServletResponse: HttpServletResponse): Retrofit2TestController.GetRequestTestWithPathParamOutputVo?


    ////
    // (Post 요청 테스트 (Request Body, application/json))
    fun postRequestTestWithApplicationJsonTypeRequestBody(httpServletResponse: HttpServletResponse): Retrofit2TestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (Request Body, x-www-form-urlencoded))
    fun postRequestTestWithFormTypeRequestBody(httpServletResponse: HttpServletResponse): Retrofit2TestController.PostRequestTestWithFormTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (Request Body, multipart/form-data))
    fun postRequestTestWithMultipartFormTypeRequestBody(httpServletResponse: HttpServletResponse): Retrofit2TestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (Request Body, multipart/form-data, MultipartFile List))
    fun postRequestTestWithMultipartFormTypeRequestBody2(httpServletResponse: HttpServletResponse): Retrofit2TestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo?


    ////
    // (Post 요청 테스트 (Request Body, multipart/form-data, with jsonString))
    fun postRequestTestWithMultipartFormTypeRequestBody3(httpServletResponse: HttpServletResponse): Retrofit2TestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo?


    ////
    // (에러 발생 테스트)
    fun generateErrorTest(httpServletResponse: HttpServletResponse)


    ////
    // (api-result-code 반환 테스트)
    fun returnResultCodeThroughHeaders(httpServletResponse: HttpServletResponse)


    ////
    // (응답 지연 발생 테스트)
    fun responseDelayTest(httpServletResponse: HttpServletResponse, delayTimeSec: Long)


    ////
    // (text/string 형식 Response 받아오기)
    fun returnTextStringTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (text/html 형식 Response 받아오기)
    fun returnTextHtmlTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (DeferredResult Get 요청 테스트)
    fun asynchronousResponseTest(httpServletResponse: HttpServletResponse): Retrofit2TestController.AsynchronousResponseTestOutputVo?


    // todo
    ////
    // (SSE 구독 테스트)
//    fun sseSubscribeTest(httpServletResponse: HttpServletResponse)


    ////
    // (WebSocket 연결 테스트)
    fun websocketConnectTest(httpServletResponse: HttpServletResponse)
}