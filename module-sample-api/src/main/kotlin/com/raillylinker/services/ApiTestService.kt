package com.raillylinker.services

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raillylinker.controllers.ApiTestController
import com.raillylinker.util_components.CustomUtil
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import org.springframework.util.StringUtils
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class ApiTestService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,
    private val customUtil: CustomUtil
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // (스레드 풀)
    private val executorService: ExecutorService = Executors.newCachedThreadPool()


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (기본 요청 테스트 API)
    fun basicRequestTest(httpServletResponse: HttpServletResponse): String? {
        httpServletResponse.status = HttpStatus.OK.value()
        return activeProfile
    }


    // ----
    // (요청 Redirect 테스트)
    fun redirectTest(httpServletResponse: HttpServletResponse): ModelAndView? {
        val mv = ModelAndView()
        mv.viewName = "redirect:/api-test"

        return mv
    }


    // ----
    // (요청 Forward 테스트)
    fun forwardTest(httpServletResponse: HttpServletResponse): ModelAndView? {
        val mv = ModelAndView()
        mv.viewName = "forward:/api-test"

        return mv
    }


    // ----
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
    ): ApiTestController.GetRequestTestOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.GetRequestTestOutputVo(
            queryParamString,
            queryParamStringNullable,
            queryParamInt,
            queryParamIntNullable,
            queryParamDouble,
            queryParamDoubleNullable,
            queryParamBoolean,
            queryParamBooleanNullable,
            queryParamStringList,
            queryParamStringListNullable
        )
    }


    // ----
    // (Get 요청 테스트 (Path Parameter))
    fun getRequestTestWithPathParam(
        httpServletResponse: HttpServletResponse,
        pathParamInt: Int
    ): ApiTestController.GetRequestTestWithPathParamOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.GetRequestTestWithPathParamOutputVo(pathParamInt)
    }


    // ----
    // (Post 요청 테스트 (application-json))
    fun postRequestTestWithApplicationJsonTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBodyInputVo
    ): ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo(
            inputVo.requestBodyString,
            inputVo.requestBodyStringNullable,
            inputVo.requestBodyInt,
            inputVo.requestBodyIntNullable,
            inputVo.requestBodyDouble,
            inputVo.requestBodyDoubleNullable,
            inputVo.requestBodyBoolean,
            inputVo.requestBodyBooleanNullable,
            inputVo.requestBodyStringList,
            inputVo.requestBodyStringListNullable
        )
    }


    // ----
    // (Post 요청 테스트 (application-json, 객체 파라미터 포함))
    fun postRequestTestWithApplicationJsonTypeRequestBody2(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2InputVo
    ): ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo? {
        val objectList: MutableList<ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo> =
            mutableListOf()

        for (objectVo in inputVo.objectVoList) {
            val subObjectVoList: MutableList<ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo> =
                mutableListOf()
            for (subObject in objectVo.subObjectVoList) {
                subObjectVoList.add(
                    ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo(
                        subObject.requestBodyString,
                        subObject.requestBodyStringList
                    )
                )
            }

            objectList.add(
                ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo(
                    objectVo.requestBodyString,
                    objectVo.requestBodyStringList,
                    ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo(
                        objectVo.subObjectVo.requestBodyString,
                        objectVo.subObjectVo.requestBodyStringList
                    ),
                    subObjectVoList
                )
            )
        }

        val subObjectVoList: MutableList<ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo> =
            mutableListOf()
        for (subObject in inputVo.objectVo.subObjectVoList) {
            subObjectVoList.add(
                ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo(
                    subObject.requestBodyString,
                    subObject.requestBodyStringList
                )
            )
        }

        val outputVo =
            ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo(
                ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo(
                    inputVo.objectVo.requestBodyString,
                    inputVo.objectVo.requestBodyStringList,
                    ApiTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo.ObjectVo.SubObjectVo(
                        inputVo.objectVo.subObjectVo.requestBodyString,
                        inputVo.objectVo.subObjectVo.requestBodyStringList
                    ),
                    subObjectVoList
                ),
                objectList
            )

        httpServletResponse.status = HttpStatus.OK.value()
        return outputVo
    }


    // ----
    // (Post 요청 테스트 (입출력값 없음))
    fun postRequestTestWithNoInputAndOutput(
        httpServletResponse: HttpServletResponse
    ) {
        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (Post 요청 테스트 (x-www-form-urlencoded))
    fun postRequestTestWithFormTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithFormTypeRequestBodyInputVo
    ): ApiTestController.PostRequestTestWithFormTypeRequestBodyOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.PostRequestTestWithFormTypeRequestBodyOutputVo(
            inputVo.requestFormString,
            inputVo.requestFormStringNullable,
            inputVo.requestFormInt,
            inputVo.requestFormIntNullable,
            inputVo.requestFormDouble,
            inputVo.requestFormDoubleNullable,
            inputVo.requestFormBoolean,
            inputVo.requestFormBooleanNullable,
            inputVo.requestFormStringList,
            inputVo.requestFormStringListNullable
        )
    }


    // ----
    // (Post 요청 테스트 (multipart/form-data))
    fun postRequestTestWithMultipartFormTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithMultipartFormTypeRequestBodyInputVo
    ): ApiTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo? {
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/sample_api/test").toAbsolutePath().normalize()

        customUtil.multipartFileLocalSave(
            saveDirectoryPath,
            null,
            inputVo.multipartFile
        )

        if (inputVo.multipartFileNullable != null) {
            customUtil.multipartFileLocalSave(
                saveDirectoryPath,
                null,
                inputVo.multipartFileNullable
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo(
            inputVo.requestFormString,
            inputVo.requestFormStringNullable,
            inputVo.requestFormInt,
            inputVo.requestFormIntNullable,
            inputVo.requestFormDouble,
            inputVo.requestFormDoubleNullable,
            inputVo.requestFormBoolean,
            inputVo.requestFormBooleanNullable,
            inputVo.requestFormStringList,
            inputVo.requestFormStringListNullable
        )
    }


    // ----
    // (Post 요청 테스트2 (multipart/form-data))
    fun postRequestTestWithMultipartFormTypeRequestBody2(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody2InputVo
    ): ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo? {
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/sample_api/test").toAbsolutePath().normalize()

        // 파일 저장 기본 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        for (multipartFile in inputVo.multipartFileList) {
            customUtil.multipartFileLocalSave(
                saveDirectoryPath,
                null,
                multipartFile
            )
        }

        if (inputVo.multipartFileNullableList != null) {
            for (multipartFileNullable in inputVo.multipartFileNullableList) {
                customUtil.multipartFileLocalSave(
                    saveDirectoryPath,
                    null,
                    multipartFileNullable
                )
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo(
            inputVo.requestFormString,
            inputVo.requestFormStringNullable,
            inputVo.requestFormInt,
            inputVo.requestFormIntNullable,
            inputVo.requestFormDouble,
            inputVo.requestFormDoubleNullable,
            inputVo.requestFormBoolean,
            inputVo.requestFormBooleanNullable,
            inputVo.requestFormStringList,
            inputVo.requestFormStringListNullable
        )
    }


    // ----
    // (Post 요청 테스트 (multipart/form-data - JsonString))
    fun postRequestTestWithMultipartFormTypeRequestBody3(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody3InputVo
    ): ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo? {
        // input Json String to Object
        val inputJsonObject =
            Gson().fromJson<ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody3InputVo.InputJsonObject>(
                inputVo.jsonString, // 해석하려는 json 형식의 String
                object :
                    TypeToken<ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody3InputVo.InputJsonObject>() {}.type // 파싱할 데이터 객체 타입
            )

        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/sample_api/test").toAbsolutePath().normalize()

        customUtil.multipartFileLocalSave(
            saveDirectoryPath,
            null,
            inputVo.multipartFile
        )

        if (inputVo.multipartFileNullable != null) {
            customUtil.multipartFileLocalSave(
                saveDirectoryPath,
                null,
                inputVo.multipartFileNullable
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo(
            inputJsonObject.requestFormString,
            inputJsonObject.requestFormStringNullable,
            inputJsonObject.requestFormInt,
            inputJsonObject.requestFormIntNullable,
            inputJsonObject.requestFormDouble,
            inputJsonObject.requestFormDoubleNullable,
            inputJsonObject.requestFormBoolean,
            inputJsonObject.requestFormBooleanNullable,
            inputJsonObject.requestFormStringList,
            inputJsonObject.requestFormStringListNullable
        )
    }


    // ----
    // (Post 요청 테스트 (multipart/form-data - ObjectList))
    fun postRequestTestWithMultipartFormTypeRequestBody4(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody4InputVo
    ): ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody4OutputVo? {
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/sample_api/test").toAbsolutePath().normalize()

        customUtil.multipartFileLocalSave(
            saveDirectoryPath,
            null,
            inputVo.multipartFile
        )

        if (inputVo.multipartFileNullable != null) {
            customUtil.multipartFileLocalSave(
                saveDirectoryPath,
                null,
                inputVo.multipartFileNullable
            )
        }

        val inputObjectList: ArrayList<ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody4OutputVo.InputObject> =
            arrayListOf()
        for (inputObject in inputVo.inputObjectList) {
            inputObjectList.add(
                ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody4OutputVo.InputObject(
                    inputObject.requestFormString,
                    inputObject.requestFormInt
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.PostRequestTestWithMultipartFormTypeRequestBody4OutputVo(
            inputObjectList
        )
    }


    // ----
    // (인위적 에러 발생 테스트)
    fun generateErrorTest(httpServletResponse: HttpServletResponse) {
        throw RuntimeException("Test Error")
    }


    // ----
    // (결과 코드 발생 테스트)
    fun returnResultCodeThroughHeaders(
        httpServletResponse: HttpServletResponse,
        errorType: ApiTestController.ReturnResultCodeThroughHeadersErrorTypeEnum?
    ) {
        if (errorType == null) {
            httpServletResponse.status = HttpStatus.OK.value()
        } else {
            when (errorType) {
                ApiTestController.ReturnResultCodeThroughHeadersErrorTypeEnum.A -> {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                }

                ApiTestController.ReturnResultCodeThroughHeadersErrorTypeEnum.B -> {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                }

                ApiTestController.ReturnResultCodeThroughHeadersErrorTypeEnum.C -> {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                }
            }
        }
    }


    // ----
    // (인위적 응답 지연 테스트)
    fun responseDelayTest(httpServletResponse: HttpServletResponse, delayTimeSec: Long) {
        val endTime = System.currentTimeMillis() + (delayTimeSec * 1000)

        while (System.currentTimeMillis() < endTime) {
            // 아무 것도 하지 않고 대기
            Thread.sleep(100)  // 100ms마다 스레드를 잠들게 하여 CPU 사용률을 줄임
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (text/string 반환 샘플)
    fun returnTextStringTest(httpServletResponse: HttpServletResponse): String? {
        httpServletResponse.status = HttpStatus.OK.value()
        return "test Complete!"
    }


    // ----
    // (text/html 반환 샘플)
    fun returnTextHtmlTest(httpServletResponse: HttpServletResponse): ModelAndView? {
        val modelAndView = ModelAndView()
        modelAndView.viewName = "return_text_html_test/html_response_example"

        httpServletResponse.status = HttpStatus.OK.value()
        return modelAndView
    }


    // ----
    // (byte 반환 샘플)
    fun returnByteDataTest(httpServletResponse: HttpServletResponse): Resource? {
        httpServletResponse.status = HttpStatus.OK.value()
        return ByteArrayResource(
            byteArrayOf(
                'a'.code.toByte(),
                'b'.code.toByte(),
                'c'.code.toByte(),
                'd'.code.toByte(),
                'e'.code.toByte(),
                'f'.code.toByte()
            )
        )
    }


    // ----
    // (비디오 스트리밍 샘플)
    fun videoStreamingTest(
        videoHeight: ApiTestController.VideoStreamingTestVideoHeight,
        httpServletResponse: HttpServletResponse
    ): Resource? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명
        val serverFileAbsolutePathString =
            "$projectRootAbsolutePathString/module-sample-api/src/main/resources/static/video_streaming_test"

        // 멤버십 등의 정보로 해상도 제한을 걸 수도 있음
        val serverFileNameString =
            when (videoHeight) {
                ApiTestController.VideoStreamingTestVideoHeight.H240 -> {
                    "test_240p.mp4"
                }

                ApiTestController.VideoStreamingTestVideoHeight.H360 -> {
                    "test_360p.mp4"
                }

                ApiTestController.VideoStreamingTestVideoHeight.H480 -> {
                    "test_480p.mp4"
                }

                ApiTestController.VideoStreamingTestVideoHeight.H720 -> {
                    "test_720p.mp4"
                }
            }

        // 반환값에 전해줄 FIS
        val fileByteArray: ByteArray
        FileInputStream("$serverFileAbsolutePathString/$serverFileNameString").use { fileInputStream ->
            fileByteArray = FileCopyUtils.copyToByteArray(fileInputStream)
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ByteArrayResource(fileByteArray)
    }


    // ----
    // (오디오 스트리밍 샘플)
    fun audioStreamingTest(httpServletResponse: HttpServletResponse): Resource? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명
        val serverFileAbsolutePathString =
            "$projectRootAbsolutePathString/module-sample-api/src/main/resources/static/audio_streaming_test"
        val serverFileNameString = "test.mp3"

        // 반환값에 전해줄 FIS
        val fileByteArray: ByteArray
        FileInputStream("$serverFileAbsolutePathString/$serverFileNameString").use { fileInputStream ->
            fileByteArray = FileCopyUtils.copyToByteArray(fileInputStream)
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ByteArrayResource(fileByteArray)
    }


    // ----
    // (비동기 처리 결과 반환 샘플)
    fun asynchronousResponseTest(httpServletResponse: HttpServletResponse): DeferredResult<ApiTestController.AsynchronousResponseTestOutputVo>? {
        // 연결 타임아웃 밀리초
        val deferredResultTimeoutMs = 1000L * 60
        val deferredResult = DeferredResult<ApiTestController.AsynchronousResponseTestOutputVo>(
            deferredResultTimeoutMs
        )

        // 비동기 처리
        executorService.execute {
            // 지연시간 대기
            val delayMs = 5000L
            Thread.sleep(delayMs)

            // 결과 반환
            deferredResult.setResult(ApiTestController.AsynchronousResponseTestOutputVo("${delayMs / 1000} 초 경과 후 반환했습니다."))
        }

        // 결과 대기 객체를 먼저 반환
        httpServletResponse.status = HttpStatus.OK.value()
        return deferredResult
    }


    // todo
//    // ----
//    // (클라이언트가 특정 SSE 이벤트를 구독)
//    // api20 에서 발급한 Emitter 객체
//    private val api20SseEmitterWrapperMbr = SseEmitterWrapper()
//    fun sseTestSubscribe(httpServletResponse: HttpServletResponse, lastSseEventId: String?): SseEmitter? {
//        // 수신 객체
//        val sseEmitter = api20SseEmitterWrapperMbr.getSseEmitter(null, lastSseEventId)
//
//        httpServletResponse.status = HttpStatus.OK.value()
//        return sseEmitter
//    }
//
//
//    // ----
//    // (SSE 이벤트 전송 트리거 테스트)
//    private var api21TriggerTestCountMbr = 0
//    fun sseTestEventTrigger(httpServletResponse: HttpServletResponse) {
//        // emitter 이벤트 전송
//        val nowTriggerTestCount = ++api21TriggerTestCountMbr
//
//        api20SseEmitterWrapperMbr.broadcastEvent(
//            "triggerTest",
//            "trigger $nowTriggerTestCount"
//        )
//
//        httpServletResponse.status = HttpStatus.OK.value()
//    }


    // ----
    // (빈 리스트 받기 테스트)
    fun emptyListRequestTest(
        httpServletResponse: HttpServletResponse,
        stringList: List<String>,
        inputVo: ApiTestController.EmptyListRequestTestInputVo
    ): ApiTestController.EmptyListRequestTestOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return ApiTestController.EmptyListRequestTestOutputVo(
            stringList,
            inputVo.requestBodyStringList
        )
    }


    // ----
    // (by_product_files 폴더로 파일 업로드)
    fun uploadToServerTest(
        httpServletResponse: HttpServletResponse,
        inputVo: ApiTestController.UploadToServerTestInputVo
    ): ApiTestController.UploadToServerTestOutputVo? {
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/sample_api/test").toAbsolutePath().normalize()

        val savedFileName = customUtil.multipartFileLocalSave(
            saveDirectoryPath,
            null,
            inputVo.multipartFile
        )

        httpServletResponse.status = HttpStatus.OK.value()

        return ApiTestController.UploadToServerTestOutputVo("http://127.0.0.1:12006/api-test/download-from-server/$savedFileName")
    }


    // ----
    // (by_product_files 폴더에서 파일 다운받기)
    fun fileDownloadTest(
        httpServletResponse: HttpServletResponse,
        fileName: String
    ): ResponseEntity<Resource>? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명 (프로젝트 루트 경로에 있는 by_product_files/sample_api/test 폴더를 기준으로 함)
        val serverFilePathObject =
            Paths.get("$projectRootAbsolutePathString/by_product_files/sample_api/test/$fileName")

        when {
            Files.isDirectory(serverFilePathObject) -> {
                // 파일이 디렉토리일때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return null
            }

            Files.notExists(serverFilePathObject) -> {
                // 파일이 없을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ResponseEntity<Resource>(
            InputStreamResource(Files.newInputStream(serverFilePathObject)),
            HttpHeaders().apply {
                this.contentDisposition = ContentDisposition.builder("attachment")
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build()
                this.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(serverFilePathObject))
            },
            HttpStatus.OK
        )
    }
}