package com.raillylinker.services

import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.controllers.StorageController
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_StorageFileInfo
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_StorageFolderInfo
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_StorageFileInfo_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_StorageFolderInfo_Repository
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_RaillyLinkerCompany_TotalAuthMember_Repository
import com.raillylinker.redis_map_components.redis1_main.Redis1_Lock_StorageFolderInfo
import com.raillylinker.util_components.JwtTokenUtil
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class StorageService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    // 서버 접근 주소 (ex : http://127.0.0.1:11001)
    @Value("\${custom-config.server-address}") private var serverAddress: String,

    private val jwtTokenUtil: JwtTokenUtil,
    private val db1RaillyLinkerCompanyTotalAuthMemberRepository: Db1_RaillyLinkerCompany_TotalAuthMember_Repository,
    private val db1RaillyLinkerCompanyStorageFolderInfoRepository: Db1_RaillyLinkerCompany_StorageFolderInfo_Repository,
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


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (스토리지 폴더 추가 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postFolder(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: StorageController.PostFolderInputVo
    ): StorageController.PostFolderOutputVo? {
        if (inputVo.folderName.contains("/") || inputVo.folderName.contains("-")) {
            // 사용 불가 특수문자
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // 동일 폴더 정보가 존재하는지 검증
        val uniqueInvalid =
            db1RaillyLinkerCompanyStorageFolderInfoRepository.existsByTotalAuthMemberUidAndParentStorageFolderInfoUidAndFolderName(
                memberUid,
                inputVo.parentStorageFolderInfoUid,
                inputVo.folderName
            )

        if (uniqueInvalid) {
            // 중복된 폴더 경로
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }

        // 멤버 데이터 조회
        val memberEntity =
            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        // 부모 폴더 정보 조회
        val parentStorageFolderInfo: Db1_RaillyLinkerCompany_StorageFolderInfo? =
            if (inputVo.parentStorageFolderInfoUid == null) {
                null
            } else {
                val parentStorageFolderEntity =
                    db1RaillyLinkerCompanyStorageFolderInfoRepository.findByUidAndTotalAuthMemberUid(
                        inputVo.parentStorageFolderInfoUid,
                        memberUid
                    )

                if (parentStorageFolderEntity == null) {
                    // 부모 폴더 정보가 없습니다.
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                parentStorageFolderEntity
            }

        // 폴더 정보 입력
        val newStorageFolderInfo =
            db1RaillyLinkerCompanyStorageFolderInfoRepository.save(
                Db1_RaillyLinkerCompany_StorageFolderInfo(
                    memberEntity,
                    parentStorageFolderInfo,
                    if (parentStorageFolderInfo == null) {
                        0L
                    } else {
                        parentStorageFolderInfo.uid!!
                    },
                    inputVo.folderName
                )
            )

        // 기본 파일 저장 위치(./by_product_files/file_storage/files 의 멤버별 할당 폴더)
        val memberRootPath = storageRootPath + "/member_${memberUid}"

        // 상위 폴더에서부터 지금 폴더 까지의 계층 반환
        val folderPathStringBuilder = StringBuilder()
        var anchorFolderEntity: Db1_RaillyLinkerCompany_StorageFolderInfo? = newStorageFolderInfo
        while (anchorFolderEntity != null) {
            folderPathStringBuilder.insert(0, "/" + anchorFolderEntity.folderName)
            anchorFolderEntity = anchorFolderEntity.parentStorageFolderInfo
        }

        // 경로 String 을 Path 변수로 변환
        val saveDirectoryPath =
            Paths.get(memberRootPath + folderPathStringBuilder.toString()).toAbsolutePath().normalize()

        // 파일 저장 기본 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        httpServletResponse.status = HttpStatus.OK.value()
        return StorageController.PostFolderOutputVo(
            newStorageFolderInfo.uid!!
        )
    }


    // ----
    // (스토리지 폴더 수정 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun putFolder(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        storageFolderInfoUid: Long,
        inputVo: StorageController.PutFolderInputVo
    ) {
        if (inputVo.folderName.contains("/") || inputVo.folderName.contains("-")) {
            // 사용 불가 특수문자
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "5")
            return
        }

        if (inputVo.parentStorageFolderInfoUid == storageFolderInfoUid) {
            // 자기 자신을 상위 폴더로 지정할 수 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return
        }

        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        // 동일 폴더 정보가 존재하는지 검증
        val uniqueInvalid =
            db1RaillyLinkerCompanyStorageFolderInfoRepository.existsByTotalAuthMemberUidAndParentStorageFolderInfoUidAndFolderName(
                memberUid,
                inputVo.parentStorageFolderInfoUid,
                inputVo.folderName
            )

        if (uniqueInvalid) {
            // 중복된 폴더 경로
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "6")
            return
        }

//        val memberEntity =
//            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        redis1LockStorageFolderInfo.tryLockRepeat(
            "$storageFolderInfoUid",
            7000L,
            {
                // 수정하려는 폴더 정보 조회
                val storageFolderEntity =
                    db1RaillyLinkerCompanyStorageFolderInfoRepository.findByUidAndTotalAuthMemberUid(
                        storageFolderInfoUid,
                        memberUid
                    )

                if (storageFolderEntity == null) {
                    // 수정하려는 데이터가 없음
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat
                }

                // 기본 파일 저장 위치(./by_product_files/file_storage/files 의 멤버별 할당 폴더)
                val memberRootPath = storageRootPath + "/member_${memberUid}"

                // 기존 폴더 정보 객체
                val oldFolderPathStringBuilder = StringBuilder()
                var oldAnchorFolderEntity: Db1_RaillyLinkerCompany_StorageFolderInfo? = storageFolderEntity
                while (oldAnchorFolderEntity != null) {
                    oldFolderPathStringBuilder.insert(0, "/" + oldAnchorFolderEntity.folderName)
                    oldAnchorFolderEntity = oldAnchorFolderEntity.parentStorageFolderInfo
                }

                // 경로 String 을 Path 변수로 변환
                val oldSaveDirectoryPath =
                    Paths.get(memberRootPath + oldFolderPathStringBuilder.toString()).toAbsolutePath().normalize()

                // 부모 폴더 정보 조회
                val parentStorageFolderInfo: Db1_RaillyLinkerCompany_StorageFolderInfo? =
                    if (inputVo.parentStorageFolderInfoUid == null) {
                        null
                    } else {
                        val parentStorageFolderEntity =
                            db1RaillyLinkerCompanyStorageFolderInfoRepository.findByUidAndTotalAuthMemberUid(
                                inputVo.parentStorageFolderInfoUid,
                                memberUid
                            )

                        if (parentStorageFolderEntity == null) {
                            // 부모 폴더 정보가 없음
                            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                            httpServletResponse.setHeader("api-result-code", "2")
                            return@tryLockRepeat
                        }

                        // 상위 폴더를 자기 자신의 하위 폴더로 설정 못하도록 검증
                        var anchorFolderEntity: Db1_RaillyLinkerCompany_StorageFolderInfo? = parentStorageFolderEntity
                        while (anchorFolderEntity != null) {
                            // 상위 폴더로 설정할 엔티티가 자기 자신의 하위 폴더로 설정 되어 있는지 확인
                            if (anchorFolderEntity.uid == storageFolderInfoUid) {
                                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                                httpServletResponse.setHeader("api-result-code", "4")
                                return@tryLockRepeat
                            }
                            anchorFolderEntity = anchorFolderEntity.parentStorageFolderInfo
                        }

                        parentStorageFolderEntity
                    }

                // 폴더 정보 수정
                storageFolderEntity.parentStorageFolderInfo = parentStorageFolderInfo
                storageFolderEntity.parentStorageFolderInfoUidNn =
                    if (parentStorageFolderInfo == null) {
                        0L
                    } else {
                        parentStorageFolderInfo.uid!!
                    }
                storageFolderEntity.folderName = inputVo.folderName

                db1RaillyLinkerCompanyStorageFolderInfoRepository.save(storageFolderEntity)

                // 실제로 폴더 수정
                // 새 폴더 정보 객체
                val newFolderPathStringBuilder = StringBuilder()
                var newAnchorFolderEntity: Db1_RaillyLinkerCompany_StorageFolderInfo? = storageFolderEntity
                while (newAnchorFolderEntity != null) {
                    newFolderPathStringBuilder.insert(0, "/" + newAnchorFolderEntity.folderName)
                    newAnchorFolderEntity = newAnchorFolderEntity.parentStorageFolderInfo
                }

                // 경로 String 을 Path 변수로 변환
                val newSaveDirectoryPath =
                    Paths.get(memberRootPath + newFolderPathStringBuilder.toString()).toAbsolutePath().normalize()

                // 폴더 이동
                Files.move(oldSaveDirectoryPath, newSaveDirectoryPath, StandardCopyOption.REPLACE_EXISTING)

                httpServletResponse.status = HttpStatus.OK.value()
            }
        )
    }


    // ----
    // (스토리지 폴더 삭제 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteFolder(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        storageFolderInfoUid: Long
    ) {
        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
//        val memberEntity =
//            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        // 삭제하려는 폴더 정보 조회
        val storageFolderEntity =
            db1RaillyLinkerCompanyStorageFolderInfoRepository.findByUidAndTotalAuthMemberUid(
                storageFolderInfoUid,
                memberUid
            )

        if (storageFolderEntity == null) {
            // 삭제하려는 데이터가 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 실제 삭제할 폴더 경로 가져오기
        // 기본 파일 저장 위치(./by_product_files/file_storage/files 의 멤버별 할당 폴더)
        val memberRootPath = storageRootPath + "/member_${memberUid}"

        // 상위 폴더에서부터 지금 폴더 까지의 계층 반환
        val folderPathStringBuilder = StringBuilder()
        var anchorFolderEntity: Db1_RaillyLinkerCompany_StorageFolderInfo? = storageFolderEntity
        while (anchorFolderEntity != null) {
            folderPathStringBuilder.insert(0, "/" + anchorFolderEntity.folderName)
            anchorFolderEntity = anchorFolderEntity.parentStorageFolderInfo
        }

        // 경로 String 을 Path 변수로 변환
        val saveDirectoryPath =
            Paths.get(memberRootPath + folderPathStringBuilder.toString()).toAbsolutePath().normalize()

        // 기준 폴더로부터 모든 하위 폴더 출력 (하위 Depth 폴더 우선 정렬)
        val folderTreePathList =
            db1RaillyLinkerCompanyStorageFolderInfoRepository.findAllStorageFolderTreeUidList(storageFolderInfoUid)
        for (folderTreePath in folderTreePathList) {
            redis1LockStorageFolderInfo.tryLockRepeat(
                "${folderTreePath.uid}",
                7000L,
                {
                    // 폴더 내 하위 파일들 조회
                    val fileInfoList =
                        db1RaillyLinkerCompanyStorageFileInfoRepository.findAllByStorageFolderInfoUid(
                            folderTreePath.uid
                        )

                    for (fileInfo in fileInfoList) {
                        // 하위 파일들 삭제 처리
                        db1RaillyLinkerCompanyStorageFileInfoRepository.deleteById(fileInfo.uid!!)
                    }

                    // 폴더 삭제 처리
                    db1RaillyLinkerCompanyStorageFolderInfoRepository.deleteById(folderTreePath.uid)
                }
            )
        }

        // 파일 폴더 실제 삭제 처리(선택한 최상위 폴더를 삭제하면 자동으로 아래 파일들 삭제되게)
        FileUtils.deleteDirectory(saveDirectoryPath.toFile())

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (내 스토리지 폴더 트리 조회 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun getMyStorageFolderTree(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): StorageController.GetMyStorageFolderTreeOutputVo? {
        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
//        val memberEntity =
//            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        // 최상위 폴더 조회 (parentStorageFolderInfo == null)
        val rootFolders =
            db1RaillyLinkerCompanyStorageFolderInfoRepository.findAllByTotalAuthMemberUidAndParentStorageFolderInfoIsNull(
                memberUid
            )

        // 폴더 트리 변환
        val folderTree = rootFolders.map { folder -> mapToFolderVo(folder) }

        return StorageController.GetMyStorageFolderTreeOutputVo(
            folderTree
        )
    }

    // 폴더 트리를 FolderVo로 변환하는 재귀 함수
    private fun mapToFolderVo(folder: Db1_RaillyLinkerCompany_StorageFolderInfo): StorageController.GetMyStorageFolderTreeOutputVo.FolderVo {
        return StorageController.GetMyStorageFolderTreeOutputVo.FolderVo(
            folder.uid!!,
            folder.folderName,
            folder.childStorageFolderInfoList.map { child -> mapToFolderVo(child) }
        )
    }


    // ----
    // (파일 및 정보 업로드 <>)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun postFile(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: StorageController.PostFileInputVo
    ): StorageController.PostFileOutputVo? {
        if (inputVo.fileName.contains("/") || inputVo.fileName.contains("-")) {
            // 사용 불가 특수문자
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // 멤버 데이터 조회
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
//        val memberEntity =
//            db1RaillyLinkerCompanyTotalAuthMemberRepository.findByUidAndRowDeleteDateStr(memberUid, "/")!!

        return redis1LockStorageFolderInfo.tryLockRepeat(
            "${inputVo.storageFolderInfoUid}",
            7000L,
            {
                // 폴더 정보 조회
                val storageFolderEntity =
                    db1RaillyLinkerCompanyStorageFolderInfoRepository.findByUidAndTotalAuthMemberUid(
                        inputVo.storageFolderInfoUid,
                        memberUid
                    )

                if (storageFolderEntity == null) {
                    // 폴더 데이터가 없음
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return@tryLockRepeat null
                }

                // 기본 파일 저장 위치(./by_product_files/file_storage/files 의 멤버별 할당 폴더)
                val memberRootPath = storageRootPath + "/member_${memberUid}"

                // 상위 폴더에서부터 지금 폴더 까지의 계층 반환
                val folderPathStringBuilder = StringBuilder()
                var anchorFolderEntity: Db1_RaillyLinkerCompany_StorageFolderInfo? = storageFolderEntity
                while (anchorFolderEntity != null) {
                    folderPathStringBuilder.insert(0, "/" + anchorFolderEntity.folderName)
                    anchorFolderEntity = anchorFolderEntity.parentStorageFolderInfo
                }

                // 경로 String 을 Path 변수로 변환
                val saveDirectoryPath =
                    Paths.get(memberRootPath + folderPathStringBuilder.toString()).toAbsolutePath().normalize()

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
                        storageFolderEntity,
                        inputVo.fileName,
                        serverAddress,
                        inputVo.fileSecret
                    )
                )

                // 파일 실제 저장
                inputVo.file.transferTo(
                    // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                    saveDirectoryPath.resolve(
                        inputVo.fileName
                    ).normalize()
                )

                return@tryLockRepeat StorageController.PostFileOutputVo(
                    newFileInfo.uid!!
                )
            }
        )
    }
}