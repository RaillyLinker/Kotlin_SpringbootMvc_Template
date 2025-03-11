package com.raillylinker.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.services.MongoDbTestService
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
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.math.BigInteger

/*
    !!!
    테스트를 하고 싶다면, 도커를 설치하고,
    cmd 를 열어,
    프로젝트 폴더 내의 external_files/dockers/mongodb_docker 로 이동 후,
    명령어.txt 에 적힌 명령어를 입력하여 Mongodb 를 실행시킬 수 있습니다.
    !!!
 */
@Tag(name = "/mongodb-test APIs", description = "MongoDB 테스트 API 컨트롤러")
@Controller
@RequestMapping("/mongodb-test")
class MongoDbTestController(
    private val service: MongoDbTestService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "DB Row 입력 테스트 API",
        description = "테스트 테이블에 Row 를 입력합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/row"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun insertDataSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: InsertDataSampleInputVo
    ): InsertDataSampleOutputVo? {
        return service.insertDataSample(httpServletResponse, inputVo)
    }

    data class InsertDataSampleInputVo(
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(
            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("dateString")
        val dateString: String,
        @Schema(
            description = "테스트용 nullable 데이터",
            required = false,
            example = "test"
        )
        @JsonProperty("nullableValue")
        val nullableValue: String?
    )

    data class InsertDataSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: String,
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "자동 생성 숫자", required = true, example = "21345")
        @JsonProperty("randomNum")
        val randomNum: Int,
        @Schema(
            description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("testDatetime")
        val testDatetime: String,
        @Schema(description = "테스트용 nullable 데이터", required = false, example = "test")
        @JsonProperty("nullableValue")
        val nullableValue: String?,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String,
        @Schema(description = "글 삭제일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, Null 이면 /)", required = true, example = "/")
        @JsonProperty("deleteDate")
        val deleteDate: String
    )


    // ----
    @Operation(
        summary = "DB Rows 삭제 테스트 API",
        description = "테스트 테이블의 모든 Row 를 모두 삭제합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @DeleteMapping(
        path = ["/rows"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "deleteLogically", description = "논리적 삭제 여부", example = "true")
        @RequestParam("deleteLogically")
        deleteLogically: Boolean
    ) {
        service.deleteRowsSample(httpServletResponse, deleteLogically)
    }


    // ----
    @Operation(
        summary = "DB Row 삭제 테스트",
        description = "테스트 테이블의 Row 하나를 삭제합니다."
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
                                "1 : index 에 해당하는 데이터가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @DeleteMapping(
        path = ["/row/{id}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "id", description = "글 아이디", example = "1")
        @PathVariable("id")
        id: String,
        @Parameter(name = "deleteLogically", description = "논리적 삭제 여부", example = "true")
        @RequestParam("deleteLogically")
        deleteLogically: Boolean
    ) {
        service.deleteRowSample(httpServletResponse, id, deleteLogically)
    }


    // ----
    @Operation(
        summary = "DB Rows 조회 테스트",
        description = "테스트 테이블의 모든 Rows 를 반환합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectRowsSampleOutputVo? {
        return service.selectRowsSample(httpServletResponse)
    }

    data class SelectRowsSampleOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>,

        @Schema(description = "논리적으로 제거된 아이템 리스트", required = true)
        @JsonProperty("logicalDeleteEntityVoList")
        val logicalDeleteEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: String,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(description = "테스트용 nullable 데이터", required = false, example = "test")
            @JsonProperty("nullableValue")
            val nullableValue: String?,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "글 삭제일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, Null 이면 /)", required = true, example = "/")
            @JsonProperty("deleteDate")
            val deleteDate: String
        )
    }


    // ----
    @Operation(
        summary = "DB 테이블의 random_num 컬럼 근사치 기준으로 정렬한 리스트 조회 API",
        description = "테이블의 row 중 random_num 컬럼과 num 파라미터의 값의 근사치로 정렬한 리스트 반환"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/order-by-random-num-nearest"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsOrderByRandomNumSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "num", description = "근사값 정렬의 기준", example = "1")
        @RequestParam("num")
        num: Int
    ): SelectRowsOrderByRandomNumSampleOutputVo? {
        return service.selectRowsOrderByRandomNumSample(httpServletResponse, num)
    }

    data class SelectRowsOrderByRandomNumSampleOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: String,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(description = "테스트용 nullable 데이터", required = false, example = "test")
            @JsonProperty("nullableValue")
            val nullableValue: String?,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "기준과의 절대거리", required = true, example = "34")
            @JsonProperty("distance")
            val distance: Int
        )
    }


    // ----
    @Operation(
        summary = "DB 테이블의 row_create_date 컬럼 근사치 기준으로 정렬한 리스트 조회 API",
        description = "테이블의 row 중 row_create_date 컬럼과 dateString 파라미터의 값의 근사치로 정렬한 리스트 반환"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/order-by-create-date-nearest"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsOrderByRowCreateDateSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(
            name = "dateString",
            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @RequestParam("dateString")
        dateString: String
    ): SelectRowsOrderByRowCreateDateSampleOutputVo? {
        return service.selectRowsOrderByRowCreateDateSample(httpServletResponse, dateString)
    }

    data class SelectRowsOrderByRowCreateDateSampleOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: String,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(description = "테스트용 nullable 데이터", required = false, example = "test")
            @JsonProperty("nullableValue")
            val nullableValue: String?,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "기준과의 절대차이(마이크로 초)", required = true, example = "34")
            @JsonProperty("timeDiffMicroSec")
            val timeDiffMicroSec: Long
        )
    }


    // todo
//    // ----
//    @Operation(
//        summary = "DB Rows 조회 테스트 (페이징)",
//        description = "테스트 테이블의 Rows 를 페이징하여 반환합니다."
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/paging"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun selectRowsPageSample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
//        @RequestParam("page")
//        page: Int,
//        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
//        @RequestParam("pageElementsCount")
//        pageElementsCount: Int
//    ): SelectRowsPageSampleOutputVo? {
//        return service.selectRowsPageSample(httpServletResponse, page, pageElementsCount)
//    }
//
//    data class SelectRowsPageSampleOutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long,
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1234")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "23456")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(
//                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("testDatetime")
//            val testDatetime: String,
//            @Schema(
//                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(
//                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("updateDate")
//            val updateDate: String
//        )
//    }
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "DB Rows 조회 테스트 (네이티브 쿼리 페이징)",
//        description = "테스트 테이블의 Rows 를 네이티브 쿼리로 페이징하여 반환합니다.<br>" +
//                "num 을 기준으로 근사치 정렬도 수행합니다."
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/native-paging"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun selectRowsNativeQueryPageSample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
//        @RequestParam("page")
//        page: Int,
//        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
//        @RequestParam("pageElementsCount")
//        pageElementsCount: Int,
//        @Parameter(name = "num", description = "근사값의 기준", example = "1")
//        @RequestParam("num")
//        num: Int
//    ): SelectRowsNativeQueryPageSampleOutputVo? {
//        return service.selectRowsNativeQueryPageSample(httpServletResponse, page, pageElementsCount, num)
//    }
//
//    data class SelectRowsNativeQueryPageSampleOutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long,
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(
//                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("testDatetime")
//            val testDatetime: String,
//            @Schema(
//                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(
//                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("updateDate")
//            val updateDate: String,
//            @Schema(description = "기준과의 절대거리", required = true, example = "34")
//            @JsonProperty("distance")
//            val distance: Int
//        )
//    }
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "DB Row 수정 테스트",
//        description = "테스트 테이블의 Row 하나를 수정합니다."
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
//                                "1 : testTableUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
//                        schema = Schema(type = "string")
//                    )
//                ]
//            )
//        ]
//    )
//    @PatchMapping(
//        path = ["/row/{testTableUid}"],
//        consumes = [MediaType.APPLICATION_JSON_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun updateRowSample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
//        @PathVariable("testTableUid")
//        testTableUid: Long,
//        @RequestBody
//        inputVo: UpdateRowSampleInputVo
//    ): UpdateRowSampleOutputVo? {
//        return service.updateRowSample(httpServletResponse, testTableUid, inputVo)
//    }
//
//    data class UpdateRowSampleInputVo(
//        @Schema(description = "글 본문", required = true, example = "테스트 텍스트 수정글입니다.")
//        @JsonProperty("content")
//        val content: String,
//        @Schema(
//            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//            required = true,
//            example = "2024_05_02_T_15_14_49_552_KST"
//        )
//        @JsonProperty("dateString")
//        val dateString: String
//    )
//
//    data class UpdateRowSampleOutputVo(
//        @Schema(description = "글 고유번호", required = true, example = "1234")
//        @JsonProperty("uid")
//        val uid: Long,
//        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//        @JsonProperty("content")
//        val content: String,
//        @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//        @JsonProperty("randomNum")
//        val randomNum: Int,
//        @Schema(
//            description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//            required = true,
//            example = "2024_05_02_T_15_14_49_552_KST"
//        )
//        @JsonProperty("testDatetime")
//        val testDatetime: String,
//        @Schema(
//            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//            required = true,
//            example = "2024_05_02_T_15_14_49_552_KST"
//        )
//        @JsonProperty("createDate")
//        val createDate: String,
//        @Schema(
//            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//            required = true,
//            example = "2024_05_02_T_15_14_49_552_KST"
//        )
//        @JsonProperty("updateDate")
//        val updateDate: String
//    )
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "DB Row 수정 테스트 (네이티브 쿼리)",
//        description = "테스트 테이블의 Row 하나를 네이티브 쿼리로 수정합니다."
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
//                                "1 : testTableUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.",
//                        schema = Schema(type = "string")
//                    )
//                ]
//            )
//        ]
//    )
//    @PatchMapping(
//        path = ["/row/{testTableUid}/native-query"],
//        consumes = [MediaType.APPLICATION_JSON_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun updateRowNativeQuerySample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
//        @PathVariable("testTableUid")
//        testTableUid: Long,
//        @RequestBody
//        inputVo: UpdateRowNativeQuerySampleInputVo
//    ) {
//        return service.updateRowNativeQuerySample(httpServletResponse, testTableUid, inputVo)
//    }
//
//    data class UpdateRowNativeQuerySampleInputVo(
//        @Schema(description = "글 본문", required = true, example = "테스트 텍스트 수정글입니다.")
//        @JsonProperty("content")
//        val content: String,
//        @Schema(
//            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//            required = true,
//            example = "2024_05_02_T_15_14_49_552_KST"
//        )
//        @JsonProperty("dateString")
//        val dateString: String
//    )
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "DB 정보 검색 테스트",
//        description = "글 본문 내용중 searchKeyword 가 포함된 rows 를 검색하여 반환합니다."
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/search-content"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun selectRowWhereSearchingKeywordSample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
//        @RequestParam("page")
//        page: Int,
//        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
//        @RequestParam("pageElementsCount")
//        pageElementsCount: Int,
//        @Parameter(name = "searchKeyword", description = "검색어", example = "테스트")
//        @RequestParam("searchKeyword")
//        searchKeyword: String
//    ): SelectRowWhereSearchingKeywordSampleOutputVo? {
//        return service.selectRowWhereSearchingKeywordSample(
//            httpServletResponse,
//            page,
//            pageElementsCount,
//            searchKeyword
//        )
//    }
//
//    data class SelectRowWhereSearchingKeywordSampleOutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long,
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(
//                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("testDatetime")
//            val testDatetime: String,
//            @Schema(
//                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(
//                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("updateDate")
//            val updateDate: String
//        )
//    }


    // ----
    @Operation(
        summary = "트랜젝션 동작 테스트",
        description = "정보 입력 후 Exception 이 발생했을 때 롤백되어 데이터가 저장되지 않는지를 테스트하는 API"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/transaction-rollback-sample"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun transactionRollbackTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.transactionRollbackTest(httpServletResponse)
    }


    // ----
    @Operation(
        summary = "트랜젝션 비동작 테스트",
        description = "트랜젝션 처리를 하지 않았을 때, DB 정보 입력 후 Exception 이 발생 했을 때 의 테스트 API"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/no-transaction-exception-sample"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun noTransactionRollbackTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.noTransactionRollbackTest(httpServletResponse)
    }


    // ----
    @Operation(
        summary = "트랜젝션 비동작 테스트(try-catch)",
        description = "에러 발생문이 try-catch 문 안에 있을 때, DB 정보 입력 후 Exception 이 발생 해도 트랜젝션이 동작하지 않는지에 대한 테스트 API"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/try-catch-no-transaction-exception-sample"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun tryCatchNonTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.tryCatchNonTransactionTest(httpServletResponse)
    }


    // todo
//    // ----
//    @Operation(
//        summary = "DB Rows 조회 테스트 (중복 없는 네이티브 쿼리 페이징)",
//        description = "테스트 테이블의 Rows 를 네이티브 쿼리로 중복없이 페이징하여 반환합니다.<br>" +
//                "num 을 기준으로 근사치 정렬도 수행합니다."
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/native-paging-no-duplication"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun selectRowsNoDuplicatePagingSample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "lastItemUid", description = "이전 페이지에서 받은 마지막 아이템의 Uid (첫 요청이면 null)", example = "1")
//        @RequestParam("lastItemUid")
//        lastItemUid: Long?,
//        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
//        @RequestParam("pageElementsCount")
//        pageElementsCount: Int
//    ): SelectRowsNoDuplicatePagingSampleOutputVo? {
//        return service.selectRowsNoDuplicatePagingSample(httpServletResponse, lastItemUid, pageElementsCount)
//    }
//
//    data class SelectRowsNoDuplicatePagingSampleOutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long,
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(
//                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("testDatetime")
//            val testDatetime: String,
//            @Schema(
//                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(
//                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("updateDate")
//            val updateDate: String
//        )
//    }
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "DB Rows 조회 테스트 (카운팅)",
//        description = "테스트 테이블의 Rows 를 카운팅하여 반환합니다."
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/counting"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun selectRowsCountSample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse
//    ): SelectRowsCountSampleOutputVo? {
//        return service.selectRowsCountSample(httpServletResponse)
//    }
//
//    data class SelectRowsCountSampleOutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long
//    )
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "DB Rows 조회 테스트 (네이티브 카운팅)",
//        description = "테스트 테이블의 Rows 를 네이티브 쿼리로 카운팅하여 반환합니다."
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/native-counting"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun selectRowsCountByNativeQuerySample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse
//    ): SelectRowsCountByNativeQuerySampleOutputVo? {
//        return service.selectRowsCountByNativeQuerySample(httpServletResponse)
//    }
//
//    data class SelectRowsCountByNativeQuerySampleOutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long
//    )
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "DB Row 조회 테스트 (네이티브)",
//        description = "테스트 테이블의 Row 하나를 네이티브 쿼리로 반환합니다."
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
//                                "1 : testTableUid 에 해당하는 데이터가 존재하지 않습니다.",
//                        schema = Schema(type = "string")
//                    )
//                ]
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/row/native/{testTableUid}"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun selectRowByNativeQuerySample(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
//        @PathVariable("testTableUid")
//        testTableUid: Long
//    ): SelectRowByNativeQuerySampleOutputVo? {
//        return service.selectRowByNativeQuerySample(httpServletResponse, testTableUid)
//    }
//
//    data class SelectRowByNativeQuerySampleOutputVo(
//        @Schema(description = "글 고유번호", required = true, example = "1234")
//        @JsonProperty("uid")
//        val uid: Long,
//        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//        @JsonProperty("content")
//        val content: String,
//        @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//        @JsonProperty("randomNum")
//        val randomNum: Int,
//        @Schema(
//            description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//            required = true,
//            example = "2024_05_02_T_15_14_49_552_KST"
//        )
//        @JsonProperty("testDatetime")
//        val testDatetime: String,
//        @Schema(
//            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//            required = true,
//            example = "2024_05_02_T_15_14_49_552_KST"
//        )
//        @JsonProperty("createDate")
//        val createDate: String,
//        @Schema(
//            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//            required = true,
//            example = "2024_05_02_T_15_14_49_552_KST"
//        )
//        @JsonProperty("updateDate")
//        val updateDate: String
//    )


    // ----
    @Operation(
        summary = "유니크 테스트 테이블 Row 입력 API",
        description = "유니크 테스트 테이블에 Row 를 입력합니다.<br>" +
                "논리적 삭제를 적용한 본 테이블에서 유니크 값은, 유니크 값 컬럼과 행 삭제일 데이터와의 혼합입니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/unique-test-table"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun insertUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: InsertUniqueTestTableRowSampleInputVo
    ): InsertUniqueTestTableRowSampleOutputVo? {
        return service.insertUniqueTestTableRowSample(httpServletResponse, inputVo)
    }

    data class InsertUniqueTestTableRowSampleInputVo(
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int
    )

    data class InsertUniqueTestTableRowSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: String,
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String,
        @Schema(description = "글 삭제일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, Null 이면 /)", required = true, example = "/")
        @JsonProperty("deleteDate")
        val deleteDate: String
    )


    // ----
    @Operation(
        summary = "유니크 테스트 테이블 Rows 조회 테스트",
        description = "유니크 테스트 테이블의 모든 Rows 를 반환합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/unique-test-table/all"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectUniqueTestTableRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectUniqueTestTableRowsSampleOutputVo? {
        return service.selectUniqueTestTableRowsSample(httpServletResponse)
    }

    data class SelectUniqueTestTableRowsSampleOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>,

        @Schema(description = "논리적으로 제거된 아이템 리스트", required = true)
        @JsonProperty("logicalDeleteEntityVoList")
        val logicalDeleteEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: String,
            @Schema(description = "유니크 값", required = true, example = "1")
            @JsonProperty("uniqueValue")
            val uniqueValue: Int,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "글 삭제일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, Null 이면 /)", required = true, example = "/")
            @JsonProperty("deleteDate")
            val deleteDate: String
        )
    }


    // ----
    @Operation(
        summary = "유니크 테스트 테이블 Row 수정 테스트",
        description = "유니크 테스트 테이블의 Row 하나를 수정합니다."
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
                                "1 : uniqueTestTableUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.<br>" +
                                "2 : uniqueValue 와 일치하는 정보가 이미 데이터베이스에 존재합니다.",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @PatchMapping(
        path = ["/unique-test-table/{uniqueTestTableUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun updateUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "uniqueTestTableUid", description = "unique test 테이블의 uid", example = "1")
        @PathVariable("uniqueTestTableUid")
        uniqueTestTableUid: String,
        @RequestBody
        inputVo: UpdateUniqueTestTableRowSampleInputVo
    ): UpdateUniqueTestTableRowSampleOutputVo? {
        return service.updateUniqueTestTableRowSample(httpServletResponse, uniqueTestTableUid, inputVo)
    }

    data class UpdateUniqueTestTableRowSampleInputVo(
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int
    )

    data class UpdateUniqueTestTableRowSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: String,
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String
    )


    // ----
    @Operation(
        summary = "유니크 테스트 테이블 Row 삭제 테스트",
        description = "유니크 테스트 테이블의 Row 하나를 삭제합니다."
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
                                "1 : index 에 해당하는 데이터가 데이터베이스에 존재하지 않습니다.",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @DeleteMapping(
        path = ["/unique-test-table/{id}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "id", description = "글 고유값", example = "1")
        @PathVariable("id")
        id: String
    ) {
        service.deleteUniqueTestTableRowSample(httpServletResponse, id)
    }
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "Native Query 반환값 테스트",
//        description = "Native Query Select 문에서 IF, CASE 등의 문구에서 반환되는 값들을 받는 예시"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/native-query-return"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun getNativeQueryReturnValueTest(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "inputVal", description = "Native Query 비교문에 사용되는 파라미터", example = "true")
//        @RequestParam("inputVal")
//        inputVal: Boolean
//    ): GetNativeQueryReturnValueTestOutputVo? {
//        return service.getNativeQueryReturnValueTest(
//            httpServletResponse,
//            inputVal
//        )
//    }
//
//    data class GetNativeQueryReturnValueTestOutputVo(
//        @Schema(description = "Select 문에서 직접적으로 true 를 반환한 예시", required = true, example = "true")
//        @JsonProperty("normalBoolValue")
//        val normalBoolValue: Boolean,
//        @Schema(description = "Select 문에서 (1=1) 과 같이 비교한 결과를 반환한 예시", required = true, example = "true")
//        @JsonProperty("funcBoolValue")
//        val funcBoolValue: Boolean,
//        @Schema(description = "Select 문에서 if 문의 결과를 반환한 예시", required = true, example = "true")
//        @JsonProperty("ifBoolValue")
//        val ifBoolValue: Boolean,
//        @Schema(description = "Select 문에서 case 문의 결과를 반환한 예시", required = true, example = "true")
//        @JsonProperty("caseBoolValue")
//        val caseBoolValue: Boolean,
//        @Schema(description = "Select 문에서 테이블의 Boolean 컬럼의 결과를 반환한 예시", required = true, example = "true")
//        @JsonProperty("tableColumnBoolValue")
//        val tableColumnBoolValue: Boolean
//    )
//
//
    // todo
//    // ----
//    @Operation(
//        summary = "SQL Injection 테스트",
//        description = "각 상황에서 SQL Injection 공격이 유효한지 확인하기 위한 테스트<br>" +
//                "SELECT 문에서, WHERE 에, content = :searchKeyword 를 하여,<br>" +
//                " 인젝션이 일어나는 키워드를 입력시 인젝션이 먹히는지를 확인할 것입니다."
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/sql-injection-test"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun sqlInjectionTest(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "searchKeyword", description = "Select 문 검색에 사용되는 키워드", example = "test OR 1 = 1")
//        @RequestParam("searchKeyword")
//        searchKeyword: String
//    ): SqlInjectionTestOutputVo? {
//        return service.sqlInjectionTest(
//            httpServletResponse,
//            searchKeyword
//        )
//    }
//
//    data class SqlInjectionTestOutputVo(
//        @Schema(description = "JpaRepository 로 조회했을 때의 아이템 리스트", required = true)
//        @JsonProperty("jpaRepositoryResultList")
//        val jpaRepositoryResultList: List<TestEntityVo>,
//        @Schema(description = "JPQL 로 조회했을 때의 아이템 리스트", required = true)
//        @JsonProperty("jpqlResultList")
//        val jpqlResultList: List<TestEntityVo>,
//        @Schema(description = "Native Query 로 조회했을 때의 아이템 리스트", required = true)
//        @JsonProperty("nativeQueryResultList")
//        val nativeQueryResultList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1234")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(
//                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("testDatetime")
//            val testDatetime: String,
//            @Schema(
//                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(
//                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
//                required = true,
//                example = "2024_05_02_T_15_14_49_552_KST"
//            )
//            @JsonProperty("updateDate")
//            val updateDate: String
//        )
//    }
}