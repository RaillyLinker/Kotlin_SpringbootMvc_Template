package com.raillylinker.module_sample_etc.services

import com.raillylinker.module_sample_etc.controllers.FileTestController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity

interface FileTestService {
    // (파일 리스트 zip 압축 테스트)
    fun filesToZipTest(httpServletResponse: HttpServletResponse)


    ////
    // (폴더 zip 압축 테스트)
    fun folderToZipTest(httpServletResponse: HttpServletResponse)


    ////
    // (zip 압축 파일 해제 테스트)
    fun unzipTest(httpServletResponse: HttpServletResponse)


    ////
    // (클라이언트 이미지 표시 테스트용 API)
    fun forClientSideImageTest(
        httpServletResponse: HttpServletResponse,
        delayTimeSecond: Int
    ): ResponseEntity<Resource>?


    ////
    // (AWS S3 로 파일 업로드)
    fun awsS3UploadTest(
        httpServletResponse: HttpServletResponse,
        inputVo: FileTestController.AwsS3UploadTestInputVo
    ): FileTestController.AwsS3UploadTestOutputVo?


    ////
    // (AWS S3 파일의 내용을 String 으로 가져오기)
    fun getFileContentToStringTest(
        httpServletResponse: HttpServletResponse,
        uploadFileName: String
    ): FileTestController.GetFileContentToStringTestOutputVo?


    ////
    // (AWS S3 파일을 삭제하기)
    fun deleteAwsS3FileTest(httpServletResponse: HttpServletResponse, deleteFileName: String)
}