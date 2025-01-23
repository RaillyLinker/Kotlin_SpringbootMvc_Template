package com.raillylinker.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.services.StorageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

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
                                "1 : parentStorageFolderInfoUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>",
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
        @Schema(description = "폴더명", required = true, example = "내 문서")
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
                                "4 : 자기 자신의 하위 폴더를 상위 폴더로 지정할 수 없습니다.",
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
        @Schema(description = "폴더명", required = true, example = "내 문서")
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


    /*
        폴더 조회(본인 인증 필요, 폴더 트리 반환)


        파일 입력(인증 필요)
        수평 확장 고려, 용량이 부족하면 eureka 가 자동으로 로드 밸런스 탐색을 할 수 있도록 신호를 내려주기

        파일 정보 수정(본인 인증 필요, 실제 파일은 수정 못하고 파일명, 파일 서버 주소, 파일 다운로드 시크릿 코드 수정 가능)

        파일 삭제(본인 인증 필요)

        폴더 내 파일 조회(폴더 고유값 필터 및 본인 인증 필요, 폴더 내 모든 파일 반환)

        파일 다운(비인가, 시크릿 코드 설정 가능)
        중계를 위한 api 와, 실제로 파일을 다운받는 api 가 따로 필요함
        중계 api
        {다운 주소}/storage/{저장위치 암호화}/{파일폴더 + 파일명} ? secret=oooo
        다운 api
        {저장위치 복호화}/storage/{파일폴더 + 파일명} ? secret=oooo

        파일 정리(데이터에 없는 파일 삭제, 파일이 없는 데이터 삭제)


        완료되면 auth 등 파일 다루는 부분을 이것으로 대체하기(기존 aws s3 처럼 사용한다고 가정하고 util 만들어 사용)
        파일 다운로드 파라미터로 경로를 쓸 때에 / 를 - 로 표현하고, 입력시 폴더에 - 를 못 쓰게 하기
     */
}