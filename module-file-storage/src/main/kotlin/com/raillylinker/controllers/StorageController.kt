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
//    @Operation(
//        summary = "예약 상품 카테고리 정보 삭제 <>",
//        description = "예약 상품의 카테고리 정보를 삭제합니다.<br>" +
//                "하위 카테고리들은 모두 자동 삭제되며, 예약 상품 정보의 카테고리로 설정되어 있다면 null 로 재설정 됩니다."
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            ),
//            ApiResponse(
//                responseCode = "204",
//                content = [Content()],
//                description = "Response Body 가 없습니다.<br>" +
//                        "Response Headers 를 확인하세요.",
//                headers = [
//                    Header(
//                        name = "api-result-code",
//                        description = "(Response Code 반환 원인) - Required<br>" +
//                                "1 : rentableProductCategoryUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
//                        schema = Schema(type = "string")
//                    )
//                ]
//            ),
//            ApiResponse(
//                responseCode = "401",
//                content = [Content()],
//                description = "인증되지 않은 접근입니다."
//            ),
//            ApiResponse(
//                responseCode = "403",
//                content = [Content()],
//                description = "인가되지 않은 접근입니다."
//            )
//        ]
//    )
//    @DeleteMapping(
//        path = ["/rentable-product-category/{rentableProductCategoryUid}"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.ALL_VALUE]
//    )
//    @PreAuthorize("isAuthenticated()")
//    @ResponseBody
//    fun deleteRentableProductCategory(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(hidden = true)
//        @RequestHeader("Authorization")
//        authorization: String?,
//        @Parameter(name = "rentableProductCategoryUid", description = "rentableProductCategory 고유값", example = "1")
//        @PathVariable("rentableProductCategoryUid")
//        rentableProductCategoryUid: Long
//    ) {
//        service.deleteRentableProductCategory(
//            httpServletResponse,
//            authorization!!,
//            rentableProductCategoryUid
//        )
//    }
}