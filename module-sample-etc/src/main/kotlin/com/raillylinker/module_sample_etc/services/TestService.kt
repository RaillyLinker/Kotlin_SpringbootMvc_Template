package com.raillylinker.module_sample_etc.services

import com.raillylinker.module_sample_etc.controllers.TestController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity

interface TestService {
    // (이메일 발송 테스트)
    fun sendEmailTest(
        httpServletResponse: HttpServletResponse,
        inputVo: TestController.SendEmailTestInputVo
    )


    ////
    // (HTML 이메일 발송 테스트)
    fun sendHtmlEmailTest(
        httpServletResponse: HttpServletResponse,
        inputVo: TestController.SendHtmlEmailTestInputVo
    )


    ////
    // (Naver API SMS 발송 샘플)
    fun naverSmsSample(
        httpServletResponse: HttpServletResponse,
        inputVo: TestController.NaverSmsSampleInputVo
    )


    ////
    // (Naver API AlimTalk 발송 샘플)
    fun naverAlimTalkSample(
        httpServletResponse: HttpServletResponse,
        inputVo: TestController.NaverAlimTalkSampleInputVo
    )


    ////
    // (액셀 파일을 받아서 해석 후 데이터 반환)
    fun readExcelFileSample(
        httpServletResponse: HttpServletResponse,
        inputVo: TestController.ReadExcelFileSampleInputVo
    ): TestController.ReadExcelFileSampleOutputVo?


    ////
    // (액셀 파일 쓰기)
    fun writeExcelFileSample(httpServletResponse: HttpServletResponse)


    ////
    // (HTML 을 기반으로 PDF 를 생성)
    fun htmlToPdfSample(
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<Resource>?


    ////
    // (입력받은 HTML 을 기반으로 PDF 를 생성 후 반환)
    fun multipartHtmlToPdfSample(
        httpServletResponse: HttpServletResponse,
        inputVo: TestController.MultipartHtmlToPdfSampleInputVo,
        controllerBasicMapping: String?
    ): ResponseEntity<Resource>?


    ////
    // (by_product_files/uploads/fonts 폴더에서 파일 다운받기)
    fun downloadFontFile(
        httpServletResponse: HttpServletResponse,
        fileName: String
    ): ResponseEntity<Resource>?


    ////
    // (ProcessBuilder 샘플)
    fun processBuilderTest(
        httpServletResponse: HttpServletResponse,
        javaEnvironmentPath: String?
    ): TestController.ProcessBuilderTestOutputVo?


    ////
    // (입력받은 폰트 파일의 내부 이름을 반환)
    fun checkFontFileInnerName(
        httpServletResponse: HttpServletResponse,
        inputVo: TestController.CheckFontFileInnerNameInputVo
    ): TestController.CheckFontFileInnerNameOutputVo?


    ////
    // (AES256 암호화 테스트)
    fun aes256EncryptTest(
        httpServletResponse: HttpServletResponse,
        plainText: String,
        alg: TestController.Aes256EncryptTestCryptoAlgEnum,
        initializationVector: String,
        encryptionKey: String
    ): TestController.Aes256EncryptTestOutputVo?


    ////
    // (AES256 복호화 테스트)
    fun aes256DecryptTest(
        httpServletResponse: HttpServletResponse,
        encryptedText: String,
        alg: TestController.Aes256DecryptTestCryptoAlgEnum,
        initializationVector: String,
        encryptionKey: String
    ): TestController.Aes256DecryptTestOutputVo?


    ////
    // (Jsoup 태그 조작 테스트)
    fun jsoupTest(httpServletResponse: HttpServletResponse, fix: Boolean): String?
}