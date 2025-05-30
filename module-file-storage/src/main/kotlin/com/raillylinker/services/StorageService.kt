package com.raillylinker.services

import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth
import com.raillylinker.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.controllers.StorageController
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_StorageFileInfo
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_StorageFileInfo_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMember_Repository
import com.raillylinker.redis_map_components.redis1_main.Redis1_Lock_StorageFolderInfo
import com.raillylinker.util_components.JwtTokenUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import okhttp3.ResponseBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

@Service
class StorageService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,
    private val authTokenFilterTotalAuth: AuthTokenFilterTotalAuth,

    private val jwtTokenUtil: JwtTokenUtil,
    private val db1RaillyLinkerCompanyTotalAuthMemberRepository: Db1_RaillyLinkerCompany_TotalAuthMember_Repository,
    private val db1RaillyLinkerCompanyStorageFileInfoRepository: Db1_RaillyLinkerCompany_StorageFileInfo_Repository,

    private val redis1LockStorageFolderInfo: Redis1_Lock_StorageFolderInfo
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // (현 프로젝트 동작 서버의 외부 접속 주소)
    // 프로필 이미지 로컬 저장 및 다운로드 주소 지정을 위해 필요
    // !!!프로필별 접속 주소 설정하기!!
    // ex : http://127.0.0.1:8080
    private val externalAccessAddress: String
        get() {
            return when (activeProfile) {
                "prod80" -> {
                    "http://127.0.0.1"
                }

                "dev8080" -> {
                    "http://127.0.0.1:8080"
                }

                else -> {
                    "http://127.0.0.1:8080"
                }
            }
        }

    private val storageRootPath = "./by_product_files/file_storage/files"

    // 본 프로세스가 실행되는 서버 접속 주소(ex : http://111.111.111.111:8856) 를 입력합니다.
    // api 로 이것이 입력되지 않았다면 파일 입력 api 에서 503 을 발생시킬 것입니다.
    private var thisServerAddress: String? = null

    // 본 파일 서버가 준비 되었는지에 대한 플래그 입니다.
    // 이것이 true 가 아니라면 파일 입력 api 에서 비밀번호(fileInsertPw)를 입력해야만 파일 저장이 됩니다.
    // 이는 파일 서버 증량 후 테스트 까지의 시간을 벌기 위한 조치입니다.
    private var thisServerReady: Boolean = false
    private val fileInsertPw = "todopw1234!@"

    // 파일 실제 처리 api 에 사용할 비밀번호
    private val actualApiSecret = "todopw1234!@"


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (파일 서버 상태 정보 조회 <ADMIN>)
    fun getThisServerState(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): StorageController.GetThisServerStateOutputVo? {
        return StorageController.GetThisServerStateOutputVo(
            thisServerAddress,
            thisServerReady
        )
    }


    // ----
    // (파일 서버 상태 정보 수정 <ADMIN>)
    fun putThisServerState(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: StorageController.PutThisServerStateInputVo
    ) {
        if (inputVo.thisServerAddress != null) {
            // 서버 주소값 형태 검증
            if (!Regex("^https?://\\d{1,3}(\\.\\d{1,3}){3}:\\d+$").matches(inputVo.thisServerAddress)) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return
            }
        }

        thisServerAddress = inputVo.thisServerAddress
        thisServerReady = inputVo.thisServerReady
    }


    // ----
    // (파일 및 정보 업로드 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postFile(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: StorageController.PostFileInputVo
    ): StorageController.PostFileOutputVo? {
        if (thisServerAddress == null || !Regex("^https?://\\d{1,3}(\\.\\d{1,3}){3}:\\d+$").matches(thisServerAddress!!)) {
            // 서버 주소 미설정이거나 올바른 형태가 아님
            httpServletResponse.status = HttpStatus.SERVICE_UNAVAILABLE.value()
            return null
        }

        if (!thisServerReady && inputVo.fileInsertPw != fileInsertPw) {
            // 서버 준비 플래그가 true 가 아닌데 파일 비밀번호가 일치하지 않은 경우
            httpServletResponse.status = HttpStatus.SERVICE_UNAVAILABLE.value()
            return null
        }

        if (inputVo.fileName.contains("/")) {
            // 사용 불가 특수문자
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
            authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
        )
        val memberEntity =
            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        return redis1LockStorageFolderInfo.tryLockRepeat(
            "$memberUid",
            7000L,
            {
                // 동일 폴더 정보가 존재하는지 검증
                val uniqueInvalid =
                    db1RaillyLinkerCompanyStorageFileInfoRepository.existsByTotalAuthMemberUidAndFileName(
                        memberUid,
                        inputVo.fileName
                    )

                if (uniqueInvalid) {
                    // 동일 이름의 파일이 폴더 내에 존재합니다.
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return@tryLockRepeat null
                }

                // 기본 파일 저장 위치(./by_product_files/file_storage/files 의 멤버별 할당 폴더)
                val memberRootPath = storageRootPath + "/member_${memberUid}"

                // 경로 String 을 Path 변수로 변환
                val saveDirectoryPath =
                    Paths.get(memberRootPath).toAbsolutePath().normalize()

                // 파일 저장 가능 공간 검증
                // 남은 저장 가능 공간 (bytes 단위)
                val usableSpace = saveDirectoryPath.fileSystem.fileStores.first().usableSpace
                // 업로드된 파일의 크기 (bytes 단위)
                val fileSize = inputVo.file.size

                // 저장 가능 여부 반환
                if (fileSize > usableSpace) {
                    // 파일 크기가 저장 용량을 능가합니다.
                    httpServletResponse.status = HttpStatus.SERVICE_UNAVAILABLE.value()
                    return@tryLockRepeat null
                }

                // 파일 정보 저장
                val newFileInfo = db1RaillyLinkerCompanyStorageFileInfoRepository.save(
                    Db1_RaillyLinkerCompany_StorageFileInfo(
                        memberEntity,
                        inputVo.fileName,
                        thisServerAddress!!,
                        inputVo.fileSecret
                    )
                )

                // 파일 실제 저장
                Files.createDirectories(saveDirectoryPath)
                inputVo.file.transferTo(
                    // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                    // 실제 파일은 member 별 폴더 안에 fileInfo uid 로 저장됩니다.
                    saveDirectoryPath.resolve("${newFileInfo.uid!!}").normalize()
                )

                return@tryLockRepeat StorageController.PostFileOutputVo(
                    newFileInfo.uid!!,
                    if (inputVo.fileSecret == null) {
                        "/storage/download-file/${newFileInfo.uid}/${inputVo.fileName}"
                    } else {
                        "/storage/download-file/${newFileInfo.uid}/${inputVo.fileName}?fileSecret=${inputVo.fileSecret}"
                    }
                )
            }
        )
    }


    // ----
    // (파일 및 정보 업로드 여러개 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postFiles(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: StorageController.PostFilesInputVo
    ): StorageController.PostFilesOutputVo? {
        val fileNameListSize = inputVo.fileNameList.size
        val fileSecretListSize = inputVo.fileSecretList.size
        val fileListSize = inputVo.fileList.size

        if (fileNameListSize != fileSecretListSize ||
            fileNameListSize != fileListSize
        ) {
            // 리스트 사이즈가 맞지 않습니다.
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        if (thisServerAddress == null || !Regex("^https?://\\d{1,3}(\\.\\d{1,3}){3}:\\d+$").matches(thisServerAddress!!)) {
            // 서버 주소 미설정이거나 올바른 형태가 아님
            httpServletResponse.status = HttpStatus.SERVICE_UNAVAILABLE.value()
            return null
        }

        if (!thisServerReady && inputVo.fileInsertPw != fileInsertPw) {
            // 서버 준비 플래그가 true 가 아닌데 파일 비밀번호가 일치하지 않은 경우
            httpServletResponse.status = HttpStatus.SERVICE_UNAVAILABLE.value()
            return null
        }

        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
            authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
        )
        val memberEntity =
            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        return redis1LockStorageFolderInfo.tryLockRepeat(
            "$memberUid",
            7000L,
            {
                // 업로드된 파일의 크기 (bytes 단위)
                var filesSize = 0L

                // 검증
                val fileNameList: MutableList<String> = mutableListOf()
                for (i in inputVo.fileList.indices) {
                    val fileName = inputVo.fileNameList[i]
                    val file = inputVo.fileList[i]

                    if (fileName.contains("/")) {
                        // 사용 불가 특수문자
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "1")
                        return@tryLockRepeat null
                    }

                    // 동일 폴더 정보가 존재하는지 검증
                    val uniqueInvalid =
                        db1RaillyLinkerCompanyStorageFileInfoRepository.existsByTotalAuthMemberUidAndFileName(
                            memberUid,
                            fileName
                        )

                    if (uniqueInvalid || fileNameList.contains(fileName)) {
                        // 동일 이름의 파일이 존재합니다.
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "2")
                        return@tryLockRepeat null
                    }

                    fileNameList.add(fileName)

                    filesSize += file.size
                }

                // 기본 파일 저장 위치(./by_product_files/file_storage/files 의 멤버별 할당 폴더)
                val memberRootPath = storageRootPath + "/member_${memberUid}"

                // 경로 String 을 Path 변수로 변환
                val saveDirectoryPath =
                    Paths.get(memberRootPath).toAbsolutePath().normalize()

                // 파일 저장 가능 공간 검증
                // 남은 저장 가능 공간 (bytes 단위)
                val usableSpace = saveDirectoryPath.fileSystem.fileStores.first().usableSpace

                // 저장 가능 여부 반환
                if (filesSize > usableSpace) {
                    // 파일 크기가 저장 용량을 능가합니다.
                    httpServletResponse.status = HttpStatus.SERVICE_UNAVAILABLE.value()
                    return@tryLockRepeat null
                }

                // 파일 정보 저장
                val fileOutputList: MutableList<StorageController.PostFilesOutputVo.FileOutputVo> = mutableListOf()
                for (i in inputVo.fileList.indices) {
                    val fileName = inputVo.fileNameList[i]
                    val fileSecret =
                        if (inputVo.fileSecretList[i].trim().isEmpty()) {
                            null
                        } else {
                            inputVo.fileSecretList[i]
                        }
                    val file = inputVo.fileList[i]

                    val newFileInfo = db1RaillyLinkerCompanyStorageFileInfoRepository.save(
                        Db1_RaillyLinkerCompany_StorageFileInfo(
                            memberEntity,
                            fileName,
                            thisServerAddress!!,
                            fileSecret
                        )
                    )

                    // 파일 실제 저장
                    Files.createDirectories(saveDirectoryPath)
                    file.transferTo(
                        // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                        // 실제 파일은 member 별 폴더 안에 fileInfo uid 로 저장됩니다.
                        saveDirectoryPath.resolve("${newFileInfo.uid!!}").normalize()
                    )

                    fileOutputList.add(
                        StorageController.PostFilesOutputVo.FileOutputVo(
                            newFileInfo.uid!!,
                            if (fileSecret == null) {
                                "/storage/download-file/${newFileInfo.uid}/${fileName}"
                            } else {
                                "/storage/download-file/${newFileInfo.uid}/${fileName}?fileSecret=${fileSecret}"
                            }
                        )
                    )
                }

                return@tryLockRepeat StorageController.PostFilesOutputVo(
                    fileOutputList
                )
            }
        )
    }


    // ----
    // (파일 수정 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun putFile(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        authorization: String,
        storageFileInfoUid: Long,
        inputVo: StorageController.PutFileInputVo
    ) {
        if (inputVo.fileName.contains("/")) {
            // 사용 불가 특수문자
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
            authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
        )
//        val memberEntity =
//            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        redis1LockStorageFolderInfo.tryLockRepeat(
            "$memberUid",
            7000L,
            {
                val fileInfoOpt = db1RaillyLinkerCompanyStorageFileInfoRepository.findById(storageFileInfoUid)
                if (fileInfoOpt.isEmpty) {
                    // 데이터가 없습니다.
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                val fileInfo = fileInfoOpt.get()
                if (fileInfo.totalAuthMember.uid != memberUid) {
                    // 내가 등록한 정보가 아닙니다.
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                // 동일 파일 정보가 존재하는지 검증
                val uniqueInvalid =
                    db1RaillyLinkerCompanyStorageFileInfoRepository.existsByTotalAuthMemberUidAndFileName(
                        memberUid,
                        inputVo.fileName
                    )

                if (uniqueInvalid) {
                    // 동일 이름의 파일이 폴더 내에 존재합니다.
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "3")
                    return@tryLockRepeat
                }

                // 폴더 정보, 파일명 수정
                fileInfo.fileSecretCode = inputVo.fileSecret
                fileInfo.fileName = inputVo.fileName

                db1RaillyLinkerCompanyStorageFileInfoRepository.save(fileInfo)
            }
        )
    }


    // ----
    // (파일 삭제 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteFile(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        authorization: String,
        storageFileInfoUid: Long
    ) {
        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
            authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
        )
//        val memberEntity =
//            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        redis1LockStorageFolderInfo.tryLockRepeat(
            "$memberUid",
            7000L,
            {
                val fileInfoOpt = db1RaillyLinkerCompanyStorageFileInfoRepository.findById(storageFileInfoUid)
                if (fileInfoOpt.isEmpty) {
                    // 데이터가 없습니다.
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                val fileInfo = fileInfoOpt.get()
                if (fileInfo.totalAuthMember.uid != memberUid) {
                    // 내가 등록한 정보가 아닙니다.
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                //  실제 파일 삭제 요청 전달
                try {
                    Retrofit.Builder()
                        .baseUrl(fileInfo.fileServerAddress)  // 동적으로 서버 주소 설정
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(Retrofit2ActualFileDeleteService::class.java).deleteActualFile(
                            fileInfo.uid!!,
                            authorization,
                            actualApiSecret
                        )
                        .execute()
                } catch (e: Exception) {
                    classLogger.error("", e)
                }

                // 파일 정보 삭제
                db1RaillyLinkerCompanyStorageFileInfoRepository.delete(fileInfo)
            }
        )
    }

    interface Retrofit2ActualFileDeleteService {
        @DELETE("/storage/actual-file/{storageFileInfoUid}")
        fun deleteActualFile(
            @Path("storageFileInfoUid") storageFileInfoUid: Long,
            @Header("Authorization") authorization: String,
            @Query("actualApiSecret") actualApiSecret: String
        ): Call<ResponseBody>
    }


    // ----
    // (파일 여러개 삭제 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteFiles(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        authorization: String,
        storageFileInfoUidList: List<Long>
    ) {
        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
            authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
        )
//        val memberEntity =
//            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        redis1LockStorageFolderInfo.tryLockRepeat(
            "$memberUid",
            7000L,
            {
                val fileInfoList: MutableList<Db1_RaillyLinkerCompany_StorageFileInfo> = mutableListOf()

                for (storageFileInfoUid in storageFileInfoUidList) {
                    val fileInfoOpt = db1RaillyLinkerCompanyStorageFileInfoRepository.findById(storageFileInfoUid)
                    if (fileInfoOpt.isEmpty) {
                        // 데이터가 없습니다.
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "1")
                        return@tryLockRepeat
                    }

                    val fileInfo = fileInfoOpt.get()
                    if (fileInfo.totalAuthMember.uid != memberUid) {
                        // 내가 등록한 정보가 아닙니다.
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "1")
                        return@tryLockRepeat
                    }

                    fileInfoList.add(fileInfo)
                }

                for (fileInfo in fileInfoList) {
                    //  실제 파일 삭제 요청 전달
                    try {
                        Retrofit.Builder()
                            .baseUrl(fileInfo.fileServerAddress)  // 동적으로 서버 주소 설정
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(Retrofit2ActualFileDeleteService::class.java).deleteActualFile(
                                fileInfo.uid!!,
                                authorization,
                                actualApiSecret
                            )
                            .execute()
                    } catch (e: Exception) {
                        classLogger.error("", e)
                    }

                    // 파일 정보 삭제
                    db1RaillyLinkerCompanyStorageFileInfoRepository.delete(fileInfo)
                }
            }
        )
    }


    // ----
    // (파일 삭제 실제 <>)
    fun deleteActualFile(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        authorization: String,
        storageFileInfoUid: Long,
        actualApiSecret: String
    ) {
        if (this.actualApiSecret != actualApiSecret) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
            authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
        )
//        val memberEntity =
//            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        // 기본 파일 저장 위치(./by_product_files/file_storage/files 의 멤버별 할당 폴더)
        val memberRootPath = storageRootPath + "/member_${memberUid}"

        // 경로 String 을 Path 변수로 변환
        val saveDirectoryPath =
            Paths.get(memberRootPath).toAbsolutePath().normalize().resolve("$storageFileInfoUid")

        if (saveDirectoryPath.toFile().exists()) {
            // 파일 실제 삭제 처리
            Files.delete(saveDirectoryPath)
        }
    }


    // ----
    // (파일 다운로드)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun downloadFile(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        storageFileInfoUid: Long,
        fileName: String,
        fileSecret: String?
    ): ResponseEntity<Resource>? {
        val fileInfo =
            db1RaillyLinkerCompanyStorageFileInfoRepository.findByUidAndFileName(storageFileInfoUid, fileName)
        if (fileInfo == null) {
            httpServletResponse.status = HttpStatus.NOT_FOUND.value()
            return null
        }

        if (fileInfo.fileSecretCode != null && fileInfo.fileSecretCode != fileSecret) {
            httpServletResponse.status = HttpStatus.NOT_FOUND.value()
            return null
        }

        // Retrofit2 요청 호출 및 응답 그대로 반환
        val retrofit2Response = Retrofit.Builder()
            .baseUrl(fileInfo.fileServerAddress)  // 동적으로 서버 주소 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Retrofit2ActualFileDownloadService::class.java).downloadActualFile(
                fileInfo.totalAuthMember.uid!!,
                fileInfo.uid!!,
                fileInfo.fileName,
                actualApiSecret
            )
            .execute()

        httpServletResponse.status = retrofit2Response.code()

        if (httpServletResponse.status == 200) {
            val responseBody = retrofit2Response.body()!!
            return ResponseEntity.ok()
                .headers { httpHeaders ->
                    retrofit2Response.headers().forEach { (key, value) ->
                        httpHeaders[key] = value
                    }
                }
                .contentType(
                    MediaType.parseMediaType(
                        retrofit2Response.headers()["Content-Type"] ?: MediaType.APPLICATION_OCTET_STREAM_VALUE
                    )
                )
                .body(InputStreamResource(responseBody.byteStream()))
        } else {
            return null
        }
    }

    interface Retrofit2ActualFileDownloadService {
        @GET("/storage/actual-download-file")
        fun downloadActualFile(
            @Query("memberUid") memberUid: Long,
            @Query("fileUid") fileUid: Long,
            @Query("fileName") fileName: String,
            @Query("actualApiSecret") actualApiSecret: String
        ): Call<ResponseBody>
    }


    // ----
    // (파일 다운로드 실제)
    fun downloadActualFile(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        memberUid: Long,
        fileUid: Long,
        fileName: String,
        actualApiSecret: String
    ): ResponseEntity<Resource>? {
        if (actualApiSecret != this.actualApiSecret) {
            // api 호출 비번이 다를 때
            httpServletResponse.status = HttpStatus.NOT_FOUND.value()
            return null
        }

        // 기본 파일 저장 위치(./by_product_files/file_storage/files 의 멤버별 할당 폴더)
        val memberRootPath = storageRootPath + "/member_${memberUid}"

        // 경로 String 을 Path 변수로 변환
        val saveDirectoryPath =
            Paths.get(memberRootPath).toAbsolutePath().normalize().resolve("${fileUid}")

        when {
            Files.isDirectory(saveDirectoryPath) -> {
                // 파일이 디렉토리일때
                httpServletResponse.status = HttpStatus.NOT_FOUND.value()
                return null
            }

            Files.notExists(saveDirectoryPath) -> {
                // 파일이 없을 때
                httpServletResponse.status = HttpStatus.NOT_FOUND.value()
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ResponseEntity<Resource>(
            InputStreamResource(Files.newInputStream(saveDirectoryPath)),
            HttpHeaders().apply {
                this.contentDisposition = ContentDisposition.builder("attachment")
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build()
                this.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(saveDirectoryPath))
            },
            HttpStatus.OK
        )
    }
}