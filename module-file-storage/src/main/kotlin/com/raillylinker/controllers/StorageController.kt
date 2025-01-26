package com.raillylinker.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.services.StorageService
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "/storage APIs", description = "파일 스토리지 API 컨트롤러")
@Controller
@RequestMapping("/storage")
class StorageController(
    private val service: StorageService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "파일 서버 상태 정보 조회 <ADMIN>",
        description = "파일 서버의 상태 정보를 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @GetMapping(
        path = ["/this-server-state"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun getThisServerState(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?
    ): GetThisServerStateOutputVo? {
        return service.getThisServerState(
            httpServletResponse,
            authorization!!
        )
    }

    data class GetThisServerStateOutputVo(
        @Schema(
            description = "본 프로세스가 실행되는 서버 접속 주소. 값이 null 이라면 파일 입력 api 에서 503 을 발생시킬 것입니다.",
            required = false,
            example = "http://127.0.0.1:11001"
        )
        @JsonProperty("thisServerAddress")
        val thisServerAddress: String?,
        @Schema(
            description = "본 파일 서버가 준비 되었는지에 대한 플래그 입니다. 이것이 true 가 아니라면 파일 입력 api 에서 비밀번호(fileInsertPw)를 입력해야만 파일 저장이 됩니다.",
            required = true,
            example = "true"
        )
        @JsonProperty("thisServerReady")
        val thisServerReady: Boolean
    )


    // ----
    @Operation(
        summary = "파일 서버 상태 정보 수정 <ADMIN>",
        description = "파일 서버의 상태 정보를 수정합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : thisServerAddress 가 올바른 URL 형태가 아닙니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "403",
                content = [Content()],
                description = "인가되지 않은 접근입니다."
            )
        ]
    )
    @PutMapping(
        path = ["/this-server-state"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN'))")
    @ResponseBody
    fun putThisServerState(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PutThisServerStateInputVo
    ) {
        service.putThisServerState(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PutThisServerStateInputVo(
        @Schema(
            description = "본 프로세스가 실행되는 서버 접속 주소. 값이 null 이라면 파일 입력 api 에서 503 을 발생시킬 것입니다.",
            required = false,
            example = "http://127.0.0.1:11001"
        )
        @JsonProperty("thisServerAddress")
        val thisServerAddress: String?,
        @Schema(
            description = "본 파일 서버가 준비 되었는지에 대한 플래그 입니다. 이것이 true 가 아니라면 파일 입력 api 에서 비밀번호(fileInsertPw)를 입력해야만 파일 저장이 됩니다.",
            required = true,
            example = "true"
        )
        @JsonProperty("thisServerReady")
        val thisServerReady: Boolean
    )


    // ----
    @Operation(
        summary = "스토리지 폴더 추가 <>",
        description = "스토리지 폴더를 추가합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : parentStorageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 폴더명에는 / 를 사용할 수 없습니다.<br>" +
                                "3 : 중복된 폴더 경로입니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @PostMapping(
        path = ["/folder"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun postFolder(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @RequestBody
        inputVo: PostFolderInputVo
    ): PostFolderOutputVo? {
        return service.postFolder(
            httpServletResponse,
            authorization!!,
            inputVo
        )
    }

    data class PostFolderInputVo(
        @Schema(description = "부모 폴더 고유번호", required = false, example = "1")
        @JsonProperty("parentStorageFolderInfoUid")
        val parentStorageFolderInfoUid: Long?,
        @Schema(description = "폴더명 (폴더명에는 - 나 / 를 사용할 수 없습니다.)", required = true, example = "내 문서")
        @JsonProperty("folderName")
        val folderName: String
    )

    data class PostFolderOutputVo(
        @Schema(description = "storageFolderInfo 고유값", required = true, example = "1")
        @JsonProperty("storageFolderInfoUid")
        val storageFolderInfoUid: Long
    )


    // ----
    @Operation(
        summary = "스토리지 폴더 수정 <>",
        description = "스토리지 폴더 정보를 수정합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : parentStorageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "3 : 자기 자신을 상위 폴더로 지정할 수 없습니다.<br>" +
                                "4 : 자기 자신의 하위 폴더를 상위 폴더로 지정할 수 없습니다.<br>" +
                                "5 : 폴더명에는 / 를 사용할 수 없습니다.<br>" +
                                "6 : 중복된 폴더 경로입니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @PutMapping(
        path = ["/folder/{storageFolderInfoUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun putFolder(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "storageFolderInfoUid", description = "storageFolderInfo 고유값", example = "1")
        @PathVariable("storageFolderInfoUid")
        storageFolderInfoUid: Long,
        @RequestBody
        inputVo: PutFolderInputVo
    ) {
        service.putFolder(
            httpServletResponse,
            authorization!!,
            storageFolderInfoUid,
            inputVo
        )
    }

    data class PutFolderInputVo(
        @Schema(description = "부모 폴더 고유번호", required = false, example = "1")
        @JsonProperty("parentStorageFolderInfoUid")
        val parentStorageFolderInfoUid: Long?,
        @Schema(description = "폴더명 (폴더명에는 - 나 / 를 사용할 수 없습니다.)", required = true, example = "내 문서")
        @JsonProperty("folderName")
        val folderName: String
    )


    // ----
    @Operation(
        summary = "스토리지 폴더 삭제 <>",
        description = "스토리지 폴더 정보를 삭제합니다.<br>" +
                "하위 폴더들, 그에 속한 하위 파일들은 모두 자동 삭제되며, kafka 에 삭제 이벤트가 전파됩니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/folder/{storageFolderInfoUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun deleteFolder(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "storageFolderInfoUid", description = "storageFolderInfo 고유값", example = "1")
        @PathVariable("storageFolderInfoUid")
        storageFolderInfoUid: Long
    ) {
        service.deleteFolder(
            httpServletResponse,
            authorization!!,
            storageFolderInfoUid
        )
    }


    // ----
    @Operation(
        summary = "내 스토리지 폴더 트리 조회 <>",
        description = "내가 등록한 스토리지 폴더의 트리를 가져옵니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @GetMapping(
        path = ["/my-storage-folder-tree"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun getMyStorageFolderTree(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?
    ): GetMyStorageFolderTreeOutputVo? {
        return service.getMyStorageFolderTree(
            httpServletResponse,
            authorization!!
        )
    }

    data class GetMyStorageFolderTreeOutputVo(
        @Schema(description = "폴더 트리 리스트", required = true)
        @JsonProperty("folderTree")
        val folderTree: List<FolderVo>
    ) {
        @Schema(description = "폴더 Vo")
        data class FolderVo(
            @Schema(description = "폴더 고유값", required = true, example = "1")
            @JsonProperty("folderUid")
            val folderUid: Long,
            @Schema(description = "폴더 이름", required = true, example = "내 문서")
            @JsonProperty("folderName")
            val folderName: String,
            @Schema(description = "자식 폴더 리스트", required = false)
            @JsonProperty("folderChildren")
            val folderChildren: List<FolderVo>?
        )
    }


    // ----
    @Operation(
        summary = "파일 및 정보 업로드 <>",
        description = "파일 및 정보를 업로드 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : 파일명에는 / 를 사용할 수 없습니다.<br>" +
                                "3 : 동일 이름의 파일이 폴더 내에 존재합니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "503",
                content = [Content()],
                description = "파일 저장을 위한 용량이 부족할 때"
            )
        ]
    )
    @PostMapping(
        path = ["/file"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun postFile(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter
        inputVo: PostFileInputVo
    ): PostFileOutputVo? {
        return service.postFile(httpServletRequest, httpServletResponse, authorization!!, inputVo)
    }

    data class PostFileInputVo(
        @Schema(description = "관리자용 파일 입력 비밀번호(클라이언트는 무시하고 null 로 보내세요.)", required = false, example = "todopw1234!@")
        @JsonProperty("fileInsertPw")
        val fileInsertPw: String?,
        @Schema(description = "storageFolderInfo 고유값", required = true, example = "1")
        @JsonProperty("storageFolderInfoUid")
        val storageFolderInfoUid: Long,
        @Schema(description = "파일명 (파일명에는 - 나 / 를 사용할 수 없습니다.)", required = true, example = "1")
        @JsonProperty("fileName")
        val fileName: String,
        @Schema(
            description = "파일 다운로드 비밀번호(본 등록 파일을 다운로드 하기 위해 필요한 비밀번호 설정, 해싱 되지 않습니다.)",
            required = false,
            example = "asdfqwer"
        )
        @JsonProperty("fileSecret")
        val fileSecret: String?,
        @Schema(description = "파일", required = true)
        @JsonProperty("file")
        val file: MultipartFile
    )

    data class PostFileOutputVo(
        @Schema(description = "storageFileInfo 고유값", required = true, example = "1")
        @JsonProperty("storageFileInfoUid")
        val storageFileInfoUid: Long,
        @Schema(
            description = "업로드 된 파일 다운로드 주소(origin 제외)",
            required = true,
            example = "/storage/download-file/{storageFileInfoUid}/{fileName}"
        )
        @JsonProperty("fileDownloadUrl")
        val fileDownloadUrl: String
    )


    // ----
    @Operation(
        summary = "파일 및 정보 업로드 여러개 <>",
        description = "파일 및 정보를 여러개 업로드 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않는 정보가 있습니다.<br>" +
                                "2 : 파일명에는 / 를 사용할 수 없는 정보가 있습니다.<br>" +
                                "3 : 동일 이름의 파일이 폴더 내에 존재하는 정보가 있습니다.<br>" +
                                "4 : 파일 정보 리스트들의 개수가 맞지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            ),
            ApiResponse(
                responseCode = "503",
                content = [Content()],
                description = "파일 저장을 위한 용량이 부족할 때"
            )
        ]
    )
    @PostMapping(
        path = ["/files"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun postFiles(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter
        inputVo: PostFilesInputVo
    ): PostFilesOutputVo? {
        return service.postFiles(httpServletRequest, httpServletResponse, authorization!!, inputVo)
    }

    data class PostFilesInputVo(
        @Schema(description = "관리자용 파일 입력 비밀번호(클라이언트는 무시하고 null 로 보내세요.)", required = false, example = "todopw1234!@")
        @JsonProperty("fileInsertPw")
        val fileInsertPw: String?,
        @Schema(description = "storageFolderInfo 고유값 리스트", required = true)
        @JsonProperty("storageFolderInfoUidList")
        val storageFolderInfoUidList: List<Long>,
        @Schema(description = "파일명 리스트 (파일명에는 - 나 / 를 사용할 수 없습니다.)", required = true)
        @JsonProperty("fileNameList")
        val fileNameList: List<String>,
        @Schema(
            description = "파일 다운로드 비밀번호 리스트(본 등록 파일을 다운로드 하기 위해 필요한 비밀번호 설정, '' 를 입력하면 비번 입력되지 않습니다. 해싱 되지 않습니다.)",
            required = true
        )
        @JsonProperty("fileSecretList")
        val fileSecretList: List<String>,
        @Schema(description = "파일 리스트", required = true)
        @JsonProperty("fileList")
        val fileList: List<MultipartFile>
    )

    data class PostFilesOutputVo(
        @Schema(
            description = "업로드 된 파일 다운로드 정보 리스트",
            required = true
        )
        @JsonProperty("fileOutputList")
        val fileOutputList: List<FileOutputVo>
    ) {
        @Schema(description = "FileOutputVo")
        data class FileOutputVo(
            @Schema(description = "storageFileInfo 고유값", required = true, example = "1")
            @JsonProperty("storageFileInfoUid")
            val storageFileInfoUid: Long,
            @Schema(
                description = "업로드 된 파일 다운로드 주소(origin 제외)",
                required = true,
                example = "/storage/download-file/{storageFileInfoUid}/{fileName}"
            )
            @JsonProperty("fileDownloadUrl")
            val fileDownloadUrl: String
        )
    }


    // ----
    @Operation(
        summary = "파일 다운로드 비밀번호 변경 <>",
        description = "파일 다운로드 비밀번호를 변경 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFileInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @PatchMapping(
        path = ["/file/{storageFileInfoUid}/file-secret"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun patchFileSecret(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "storageFileInfoUid", description = "storageFileInfo 고유값", example = "1")
        @PathVariable("storageFileInfoUid")
        storageFileInfoUid: Long,
        @RequestBody
        inputVo: PatchFileSecretInputVo
    ) {
        service.patchFileSecret(httpServletRequest, httpServletResponse, authorization!!, storageFileInfoUid, inputVo)
    }

    data class PatchFileSecretInputVo(
        @Schema(
            description = "파일 다운로드 비밀번호(본 등록 파일을 다운로드 하기 위해 필요한 비밀번호 설정, 해싱 되지 않습니다.)",
            required = false,
            example = "asdfqwer"
        )
        @JsonProperty("fileSecret")
        val fileSecret: String?
    )


    // ----
    @Operation(
        summary = "파일 수정 <>",
        description = "파일 정보를 수정 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFileInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : storageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "3 : 파일명에는 / 를 사용할 수 없습니다.<br>" +
                                "4 : 동일 이름의 파일이 폴더 내에 존재합니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @PutMapping(
        path = ["/file/{storageFileInfoUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun putFile(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "storageFileInfoUid", description = "storageFileInfo 고유값", example = "1")
        @PathVariable("storageFileInfoUid")
        storageFileInfoUid: Long,
        @RequestBody
        inputVo: PutFileInputVo
    ) {
        service.putFile(httpServletRequest, httpServletResponse, authorization!!, storageFileInfoUid, inputVo)
    }

    data class PutFileInputVo(
        @Schema(description = "storageFolderInfo 고유값", required = true, example = "1")
        @JsonProperty("storageFolderInfoUid")
        val storageFolderInfoUid: Long,
        @Schema(description = "파일명 (파일명에는 - 나 / 를 사용할 수 없습니다.)", required = true, example = "내 파일 1")
        @JsonProperty("fileName")
        val fileName: String
    )


    // ----
    @Operation(
        summary = "파일 삭제 <>",
        description = "파일 정보를 삭제 합니다. (파일별로 요청을 해당 서버로 전달합니다.)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFileInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/file/{storageFileInfoUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun deleteFile(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "storageFileInfoUid", description = "storageFileInfo 고유값", example = "1")
        @PathVariable("storageFileInfoUid")
        storageFileInfoUid: Long
    ) {
        service.deleteFile(httpServletRequest, httpServletResponse, authorization!!, storageFileInfoUid)
    }


    // ----
    @Operation(
        summary = "파일 여러개 삭제 <>",
        description = "파일 정보를 여러개 삭제 합니다. (파일별로 요청을 해당 서버로 전달합니다.)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFileInfoUidList 중 데이터베이스에 존재하지 않는 것이 있습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/files/{storageFileInfoUidList}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun deleteFiles(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "storageFileInfoUidList", description = "storageFileInfo 고유값 리스트")
        @PathVariable("storageFileInfoUidList")
        storageFileInfoUidList: List<Long>
    ) {
        service.deleteFiles(httpServletRequest, httpServletResponse, authorization!!, storageFileInfoUidList)
    }


    // ----
    @Hidden
    @Operation(
        summary = "파일 삭제 실제 <>",
        description = "저장된 파일을 실제로 삭제 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : actualApiSecret 이 올바르지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @DeleteMapping(
        path = ["/actual-file/{storageFileInfoUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun deleteActualFile(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "storageFileInfoUid", description = "storageFileInfo 고유값", example = "1")
        @PathVariable("storageFileInfoUid")
        storageFileInfoUid: Long,
        @Parameter(name = "actualApiSecret", description = "파일 실제 처리 api 에 사용할 비밀번호", example = "todopw1234!@")
        @RequestParam("actualApiSecret")
        actualApiSecret: String
    ) {
        service.deleteActualFile(
            httpServletRequest,
            httpServletResponse,
            authorization!!,
            storageFileInfoUid,
            actualApiSecret
        )
    }


    // ----
    @Operation(
        summary = "파일 다운로드",
        description = "파일을 다운로드 합니다. (파일별로 요청을 해당 서버로 전달합니다.)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content()],
                description = "파일이 없거나 비밀번호가 일치하지 않습니다."
            )
        ]
    )
    @GetMapping(
        path = ["/download-file/{storageFileInfoUid}/{fileName}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun downloadFile(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "storageFileInfoUid", description = "storageFileInfo 고유값", example = "1")
        @PathVariable("storageFileInfoUid")
        storageFileInfoUid: Long,
        @Parameter(name = "fileName", description = "파일명", example = "sample.txt")
        @PathVariable("fileName")
        fileName: String,
        @Parameter(name = "fileSecret", description = "파일 다운로드 시크릿", example = "qwer1234")
        @RequestParam("fileSecret")
        fileSecret: String?
    ): ResponseEntity<Resource>? {
        return service.downloadFile(httpServletRequest, httpServletResponse, storageFileInfoUid, fileName, fileSecret)
    }


    // ----
    @Hidden
    @Operation(
        summary = "파일 다운로드 실제",
        description = "파일을 실제 다운로드 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "404",
                content = [Content()],
                description = "파일이 없거나 비밀번호가 일치하지 않습니다."
            )
        ]
    )
    @GetMapping(
        path = ["/actual-download-file"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun downloadActualFile(
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "memberUid", description = "멤버 고유번호", example = "1")
        @RequestParam("memberUid")
        memberUid: Long,
        @Parameter(name = "fileUid", description = "파일 고유번호", example = "1")
        @RequestParam("fileUid")
        fileUid: Long,
        @Parameter(name = "fileName", description = "파일명", example = "text.txt")
        @RequestParam("fileName")
        fileName: String,
        @Parameter(name = "actualApiSecret", description = "파일 실제 처리 api 에 사용할 비밀번호", example = "todopw1234!@")
        @RequestParam("actualApiSecret")
        actualApiSecret: String
    ): ResponseEntity<Resource>? {
        return service.downloadActualFile(
            httpServletRequest,
            httpServletResponse,
            memberUid,
            fileUid,
            fileName,
            actualApiSecret
        )
    }


    // ----
    @Operation(
        summary = "내 스토리지 폴더 내 파일 리스트 조회 <>",
        description = "내가 등록한 스토리지 폴더 내 파일 리스트를 가져옵니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.<br>" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required<br>" +
                                "1 : storageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            ),
            ApiResponse(
                responseCode = "401",
                content = [Content()],
                description = "인증되지 않은 접근입니다."
            )
        ]
    )
    @GetMapping(
        path = ["/my-storage-folder/{storageFolderInfoUid}/files"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun getMyStorageFolderFiles(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(hidden = true)
        @RequestHeader("Authorization")
        authorization: String?,
        @Parameter(name = "storageFolderInfoUid", description = "storageFolderInfo 고유값", example = "1")
        @PathVariable("storageFolderInfoUid")
        storageFolderInfoUid: Long
    ): GetMyStorageFolderFilesOutputVo? {
        return service.getMyStorageFolderFiles(
            httpServletResponse,
            authorization!!,
            storageFolderInfoUid
        )
    }

    data class GetMyStorageFolderFilesOutputVo(
        @Schema(description = "파일 리스트", required = true)
        @JsonProperty("fileList")
        val fileList: List<FileInfoVo>
    ) {
        @Schema(description = "파일 정보 Vo")
        data class FileInfoVo(
            @Schema(description = "파일 고유값", required = true, example = "1")
            @JsonProperty("fileUid")
            val fileUid: Long,
            @Schema(description = "파일 이름", required = true, example = "내 문서")
            @JsonProperty("fileName")
            val fileName: String,
            @Schema(
                description = "파일 다운로드 시크릿 코드(이 값이 null 이 아니라면, 본 파일을 다운로드 하기 위해 시크릿 코드가 필요합니다.)",
                required = false,
                example = "qwer1234"
            )
            @JsonProperty("fileSecretCode")
            val fileSecretCode: String?,
            @Schema(
                description = "파일 다운로드 주소(origin 제외)",
                required = true,
                example = "/storage/download-file/{storageFileInfoUid}/{fileName}"
            )
            @JsonProperty("fileDownloadUrl")
            val fileDownloadUrl: String
        )
    }
}