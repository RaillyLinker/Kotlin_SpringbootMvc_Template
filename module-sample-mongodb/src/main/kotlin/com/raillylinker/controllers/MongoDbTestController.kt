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
}