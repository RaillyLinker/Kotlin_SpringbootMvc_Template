package com.raillylinker.module_sample_api.services

import com.raillylinker.module_sample_api.controllers.ApiTestController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.servlet.ModelAndView

interface ApiTestService {
    // (기본 요청 테스트 API)
    fun basicRequestTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (요청 Redirect 테스트)
    fun redirectTest(httpServletResponse: HttpServletResponse): ModelAndView?


    ////
    // (요청 Forward 테스트)
    fun forwardTest(httpServletResponse: HttpServletResponse): ModelAndView?


    ////
    // (Get 요청 테스트 (Query Parameter))
    fun getRequestTest(
        httpServletResponse: HttpServletResponse,
        queryParamString: String,
        queryParamStringNullable: String?,
        queryParamInt: Int,
        queryParamIntNullable: Int?,
        queryParamDouble: Double,
        queryParamDoubleNullable: Double?,
        queryParamBoolean: Boolean,
        queryParamBooleanNullable: Boolean?,
        queryParamStringList: List<String>,
        queryParamStringListNullable: List<String>?
    ): ApiTestController.GetRequestTestOutputVo?


    ////
    // (Get 요청 테스트 (Path Parameter))
    fun getRequestTestWithPathParam(
        httpServletResponse: HttpServletResponse,
        pathParamInt: Int
    ): ApiTestController.GetRequestTestWithPathParamOutputVo?


    ////
    // (Post 요청 테스트 (application-json))
    fun postRequestTestWithApplicationJsonTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBodyInputVo
    ): ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (application-json, 객체 파라미터 포함))
    fun postRequestTestWithApplicationJsonTypeRequestBody2(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2InputVo
    ): ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo?


    ////
    // (Post 요청 테스트 (입출력값 없음))
    fun postRequestTestWithNoInputAndOutput(
        httpServletResponse: HttpServletResponse
    )


    ////
    // (Post 요청 테스트 (x-www-form-urlencoded))
    fun postRequestTestWithFormTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithFormTypeRequestBodyInputVo
    ): ApiTestController.PostRequestTestWithFormTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (multipart/form-data))
    fun postRequestTestWithMultipartFormTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithMultipartFormTypeRequestBodyInputVo
    ): ApiTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트2 (multipart/form-data))
    fun postRequestTestWithMultipartFormTypeRequestBody2(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody2InputVo
    ): ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo?


    ////
    // (Post 요청 테스트 (multipart/form-data - JsonString))
    fun postRequestTestWithMultipartFormTypeRequestBody3(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody3InputVo
    ): ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo?


    ////
    // (인위적 에러 발생 테스트)
    fun generateErrorTest(httpServletResponse: HttpServletResponse)

    ////
    // (결과 코드 발생 테스트)
    fun returnResultCodeThroughHeaders(
        httpServletResponse: HttpServletResponse,
        errorType: ApiTestController.ReturnResultCodeThroughHeadersErrorTypeEnum?
    )


    ////
    // (인위적 응답 지연 테스트)
    fun responseDelayTest(httpServletResponse: HttpServletResponse, delayTimeSec: Long)


    ////
    // (text/string 반환 샘플)
    fun returnTextStringTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (text/html 반환 샘플)
    fun returnTextHtmlTest(httpServletResponse: HttpServletResponse): ModelAndView?


    ////
    // (byte 반환 샘플)
    fun returnByteDataTest(httpServletResponse: HttpServletResponse): Resource?


    ////
    // (비디오 스트리밍 샘플)
    fun videoStreamingTest(
        videoHeight: ApiTestController.VideoStreamingTestVideoHeight,
        httpServletResponse: HttpServletResponse
    ): Resource?


    ////
    // (오디오 스트리밍 샘플)
    fun audioStreamingTest(httpServletResponse: HttpServletResponse): Resource?


    ////
    // (비동기 처리 결과 반환 샘플)
    fun asynchronousResponseTest(httpServletResponse: HttpServletResponse): DeferredResult<ApiTestController.AsynchronousResponseTestOutputVo>?


    // todo
//    ////
//    // (클라이언트가 특정 SSE 이벤트를 구독)
//    fun sseTestSubscribe(httpServletResponse: HttpServletResponse, lastSseEventId: String?): SseEmitter?
//
//
//    ////
//    // (SSE 이벤트 전송 트리거 테스트)
//    fun sseTestEventTrigger(httpServletResponse: HttpServletResponse)


    ////
    // (빈 리스트 받기 테스트)
    fun emptyListRequestTest(
        httpServletResponse: HttpServletResponse,
        stringList: List<String>,
        inputVo: ApiTestController.EmptyListRequestTestInputVo
    ): ApiTestController.EmptyListRequestTestOutputVo?


    ////
    // (by_product_files 폴더로 파일 업로드)
    fun uploadToServerTest(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.UploadToServerTestInputVo
    ): ApiTestController.UploadToServerTestOutputVo?


    ////
    // (by_product_files 폴더에서 파일 다운받기)
    fun fileDownloadTest(httpServletResponse: HttpServletResponse, fileName: String): ResponseEntity<Resource>?
}