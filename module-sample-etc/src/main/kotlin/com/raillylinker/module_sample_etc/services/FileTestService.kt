package com.raillylinker.module_sample_etc.services

import com.raillylinker.module_sample_etc.controllers.FileTestController
import com.raillylinker.module_sample_etc.util_components.AwsS3UtilComponent
import com.raillylinker.module_sample_etc.util_components.CustomUtil
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.zip.ZipOutputStream

@Service
class FileTestService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val customUtil: CustomUtil,

    // (AWS S3 유틸 객체)
    private val awsS3UtilComponent: AwsS3UtilComponent
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (파일 리스트 zip 압축 테스트)
    fun filesToZipTest(httpServletResponse: HttpServletResponse) {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 경로 리스트
        val filePathList = listOf(
            "$projectRootAbsolutePathString/module-sample-etc/src/main/resources/static/files_to_zip_test/1.txt",
            "$projectRootAbsolutePathString/module-sample-etc/src/main/resources/static/files_to_zip_test/2.xlsx",
            "$projectRootAbsolutePathString/module-sample-etc/src/main/resources/static/files_to_zip_test/3.png",
            "$projectRootAbsolutePathString/module-sample-etc/src/main/resources/static/files_to_zip_test/4.mp4"
        )

        // 파일 저장 디렉토리 경로
        val saveDirectoryPathString = "./by_product_files/sample_etc/test"
        val saveDirectoryPath = Paths.get(saveDirectoryPathString).toAbsolutePath().normalize()
        // 파일 저장 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 확장자 포함 파일명 생성
        val fileTargetPath = saveDirectoryPath.resolve(
            "zipped_${
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            }.zip"
        ).normalize()

        // 압축 파일 생성
        FileOutputStream(fileTargetPath.toFile()).use { fileOutputStream ->
            ZipOutputStream(fileOutputStream).use { zipOutputStream ->
                for (filePath in filePathList) {
                    val file = File(filePath)
                    if (file.exists()) {
                        customUtil.addToZip(file, file.name, zipOutputStream)
                    }
                }
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (폴더 zip 압축 테스트)
    fun folderToZipTest(httpServletResponse: HttpServletResponse) {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 압축 대상 디렉토리
        val sourceDir =
            File("$projectRootAbsolutePathString/module-sample-etc/src/main/resources/static/files_to_zip_test")

        // 파일 저장 디렉토리 경로
        val saveDirectoryPathString = "./by_product_files/sample_etc/test"
        val saveDirectoryPath = Paths.get(saveDirectoryPathString).toAbsolutePath().normalize()
        // 파일 저장 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 확장자 포함 파일명 생성
        val fileTargetPath = saveDirectoryPath.resolve(
            "zipped_${
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            }.zip"
        ).normalize()

        // 압축 파일 생성
        FileOutputStream(fileTargetPath.toFile()).use { fileOutputStream ->
            ZipOutputStream(fileOutputStream).use { zipOutputStream ->
                customUtil.compressDirectoryToZip(sourceDir, sourceDir.name, zipOutputStream)
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (zip 압축 파일 해제 테스트)
    fun unzipTest(httpServletResponse: HttpServletResponse) {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath
        val filePathString =
            "$projectRootAbsolutePathString/module-sample-etc/src/main/resources/static/unzip_test/test.zip"

        // 파일 저장 디렉토리 경로
        val saveDirectoryPathString = "./by_product_files/sample_etc/test"
        val saveDirectoryPath = Paths.get(saveDirectoryPathString).toAbsolutePath().normalize()
        // 파일 저장 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 요청 시간을 문자열로
        val timeString = LocalDateTime.now().atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))

        // 확장자 포함 파일명 생성
        val saveFileName = "unzipped_${timeString}/"

        val fileTargetPath = saveDirectoryPath.resolve(saveFileName).normalize()

        customUtil.unzipFile(filePathString, fileTargetPath)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (클라이언트 이미지 표시 테스트용 API)
    fun forClientSideImageTest(
        httpServletResponse: HttpServletResponse,
        delayTimeSecond: Int
    ): ResponseEntity<Resource>? {
        if (delayTimeSecond < 0) {
            httpServletResponse.status = HttpStatus.BAD_REQUEST.value()
            return null
        }

        Thread.sleep(delayTimeSecond * 1000L)

        val file: Resource = ClassPathResource("static/for_client_side_image_test/client_image_test.jpg")

        httpServletResponse.status = HttpStatus.OK.value()
        return ResponseEntity<Resource>(
            file,
            HttpHeaders().apply {
                this.contentDisposition = ContentDisposition.builder("attachment")
                    .filename("client_image_test.jpg", StandardCharsets.UTF_8)
                    .build()
                this.add(HttpHeaders.CONTENT_TYPE, "image/jpeg")
            },
            HttpStatus.OK
        )
    }


    // ----
    // (AWS S3 로 파일 업로드)
    fun awsS3UploadTest(
        httpServletResponse: HttpServletResponse,
        inputVo: FileTestController.AwsS3UploadTestInputVo
    ): FileTestController.AwsS3UploadTestOutputVo? {
        // 원본 파일명(with suffix)
        val multiPartFileNameString = StringUtils.cleanPath(inputVo.multipartFile.originalFilename!!)

        // 파일 확장자 구분 위치
        val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

        // 확장자가 없는 파일명
        val fileNameWithOutExtension: String
        // 확장자
        val fileExtension: String

        if (fileExtensionSplitIdx == -1) {
            fileNameWithOutExtension = multiPartFileNameString
            fileExtension = ""
        } else {
            fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
            fileExtension =
                multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
        }

        val savedFileName = "${fileNameWithOutExtension}(${
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        }).$fileExtension"

        val uploadedFileFullUrl: String = awsS3UtilComponent.upload(
            inputVo.multipartFile,
            savedFileName,
            if (activeProfile == "prod80") {
                "test-prod/test"
            } else {
                "test-dev/test"
            }
        )

        httpServletResponse.status = HttpStatus.OK.value()

        return FileTestController.AwsS3UploadTestOutputVo(uploadedFileFullUrl)
    }


    // ----
    // (AWS S3 파일의 내용을 String 으로 가져오기)
    fun getFileContentToStringTest(
        httpServletResponse: HttpServletResponse,
        uploadFileName: String
    ): FileTestController.GetFileContentToStringTestOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()

        return FileTestController.GetFileContentToStringTestOutputVo(
            awsS3UtilComponent.getTextFileString(
                if (activeProfile == "prod80") {
                    "raillylinker-prod/test"
                } else {
                    "raillylinker-dev/test"
                },
                uploadFileName
            )
        )
    }


    // ----
    // (AWS S3 파일을 삭제하기)
    fun deleteAwsS3FileTest(httpServletResponse: HttpServletResponse, deleteFileName: String) {
        // AWS 파일 삭제
        awsS3UtilComponent.delete(
            if (activeProfile == "prod80") {
                "raillylinker-prod/test"
            } else {
                "raillylinker-dev/test"
            },
            deleteFileName
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }
}