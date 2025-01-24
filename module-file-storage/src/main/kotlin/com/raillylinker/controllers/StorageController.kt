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
                                "2 : 폴더명에는 - 나 / 를 사용할 수 없습니다.<br>" +
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
                                "5 : 폴더명에는 - 나 / 를 사용할 수 없습니다.<br>" +
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
                                "2 : 파일명에는 - 나 / 를 사용할 수 없습니다.<br>" +
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
        val storageFileInfoUid: Long
    )


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
        description = "파일 정보를 수정 합니다. (파일별로 요청을 해당 서버로 전달합니다.)"
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
                                "3 : 파일명에는 - 나 / 를 사용할 수 없습니다.<br>" +
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
    @Hidden
    @Operation(
        summary = "파일 수정 실제 <>",
        description = "파일 정보를 실제 수정 합니다."
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
                                "3 : 파일명에는 - 나 / 를 사용할 수 없습니다.<br>" +
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
        path = ["/actual-file/{storageFileInfoUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun putActualFile(
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
        service.putActualFile(httpServletRequest, httpServletResponse, authorization!!, storageFileInfoUid, inputVo)
    }


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
    @Hidden
    @Operation(
        summary = "파일 삭제 실제 <>",
        description = "파일 정보를 실제 삭제 합니다."
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
        storageFileInfoUid: Long
    ) {
        service.deleteActualFile(httpServletRequest, httpServletResponse, authorization!!, storageFileInfoUid)
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
        path = ["/actual-download-file/{storageFileInfoUid}/{fileName}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    @ResponseBody
    fun downloadActualFile(
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
        return service.downloadActualFile(
            httpServletRequest,
            httpServletResponse,
            storageFileInfoUid,
            fileName,
            fileSecret
        )
    }


    /*
        todo
        7. 폴더 내 파일 조회(본인 인증 필요, 폴더 고유값에 해당하는 폴더 내 모든 파일 반환)

        9. 완료되면 auth 등 파일 다루는 부분을 이것으로 대체하기(기존 aws s3 처럼 사용한다고 가정하고 util 만들어 사용)
     */
}