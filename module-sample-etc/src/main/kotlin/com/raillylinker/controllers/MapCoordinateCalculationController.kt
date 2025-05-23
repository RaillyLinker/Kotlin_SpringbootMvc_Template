package com.raillylinker.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.services.MapCoordinateCalculationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

// Google 맵과 비교하여 정상 동작 테스트를 하면 됩니다.
@Tag(name = "/map-coordinate-calculation APIs", description = "지도/좌표 계산에 대한 API 컨트롤러")
@Controller
@RequestMapping("/map-coordinate-calculation")
class MapCoordinateCalculationController(
    private val service: MapCoordinateCalculationService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "테스트용 기본 좌표 리스트를 DB에 저장",
        description = "DB 내에 기존 좌표 데이터들을 모두 날려버리고, 테스트용 기본 좌표 리스트를 DB에 저장합니다."
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
        path = ["/default-coordinate"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun insertDefaultCoordinateDataToDatabase(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.insertDefaultCoordinateDataToDatabase(httpServletResponse)
    }


    // ----
    @Operation(
        summary = "두 좌표 사이의 거리를 반환(하버사인 공식)",
        description = "하버사인 공식을 사용하여 두 좌표 사이의 거리를 meter 단위로 반환하는 API"
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
        path = ["/distance-meter-between-two-coordinate-harversine"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun getDistanceMeterBetweenTwoCoordinate(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "latitude1", description = "위도1", example = "37.675683")
        @RequestParam("latitude1")
        latitude1: Double,
        @Parameter(name = "longitude1", description = "경도1", example = "126.761259")
        @RequestParam("longitude1")
        longitude1: Double,
        @Parameter(name = "latitude2", description = "위도2", example = "37.676563")
        @RequestParam("latitude2")
        latitude2: Double,
        @Parameter(name = "longitude2", description = "경도2", example = "126.764777")
        @RequestParam("longitude2")
        longitude2: Double
    ): GetDistanceMeterBetweenTwoCoordinateOutputVo? {
        return service.getDistanceMeterBetweenTwoCoordinate(
            httpServletResponse,
            latitude1,
            longitude1,
            latitude2,
            longitude2
        )
    }

    data class GetDistanceMeterBetweenTwoCoordinateOutputVo(
        @Schema(description = "좌표간 거리 (Meter)", required = true, example = "325.42")
        @JsonProperty("distanceMeter")
        val distanceMeter: Double
    )


    // ----
    @Operation(
        summary = "두 좌표 사이의 거리를 반환(Vincenty 공식)",
        description = "Vincenty 공식을 사용하여 두 좌표 사이의 거리를 meter 단위로 반환하는 API"
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
        path = ["/distance-meter-between-two-coordinate-vincenty"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun getDistanceMeterBetweenTwoCoordinateVincenty(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "latitude1", description = "위도1", example = "37.675683")
        @RequestParam("latitude1")
        latitude1: Double,
        @Parameter(name = "longitude1", description = "경도1", example = "126.761259")
        @RequestParam("longitude1")
        longitude1: Double,
        @Parameter(name = "latitude2", description = "위도2", example = "37.676563")
        @RequestParam("latitude2")
        latitude2: Double,
        @Parameter(name = "longitude2", description = "경도2", example = "126.764777")
        @RequestParam("longitude2")
        longitude2: Double
    ): GetDistanceMeterBetweenTwoCoordinateVincentyOutputVo? {
        return service.getDistanceMeterBetweenTwoCoordinateVincenty(
            httpServletResponse,
            latitude1,
            longitude1,
            latitude2,
            longitude2
        )
    }

    data class GetDistanceMeterBetweenTwoCoordinateVincentyOutputVo(
        @Schema(description = "좌표간 거리 (Meter)", required = true, example = "325.42")
        @JsonProperty("distanceMeter")
        val distanceMeter: Double
    )


    // ----
    @Operation(
        summary = "좌표들 사이의 중심 좌표를 반환",
        description = "함수를 사용하여 좌표들 사이의 중심 좌표를 반환하는 API"
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
        path = ["/for-center-coordinate"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun returnCenterCoordinate(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: ReturnCenterCoordinateInputVo
    ): ReturnCenterCoordinateOutputVo? {
        return service.returnCenterCoordinate(httpServletResponse, inputVo)
    }

    data class ReturnCenterCoordinateInputVo(
        @Schema(description = "좌표 리스트", required = true)
        @JsonProperty("coordinateList")
        val coordinateList: List<Coordinate>
    ) {
        @Schema(description = "좌표 데이터")
        data class Coordinate(
            @Schema(description = "중심좌표 위도", required = true, example = "37.676563")
            @JsonProperty("centerLatitude")
            val centerLatitude: Double,
            @Schema(description = "중심좌표 경도", required = true, example = "126.761259")
            @JsonProperty("centerLongitude")
            val centerLongitude: Double
        )
    }

    data class ReturnCenterCoordinateOutputVo(
        @Schema(description = "중심좌표 위도", required = true, example = "37.676563")
        @JsonProperty("centerLatitude")
        val centerLatitude: Double,
        @Schema(description = "중심좌표 경도", required = true, example = "126.761259")
        @JsonProperty("centerLongitude")
        val centerLongitude: Double
    )


    // ----
    @Operation(
        summary = "DB 의 좌표 테이블에 좌표 정보를 저장",
        description = "DB 의 좌표 테이블에 좌표 정보를 저장하는 API"
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
        path = ["/test-map/coordinate"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun insertCoordinateDataToDatabase(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: InsertCoordinateDataToDatabaseInputVo
    ): InsertCoordinateDataToDatabaseOutputVo? {
        return service.insertCoordinateDataToDatabase(httpServletResponse, inputVo)
    }

    data class InsertCoordinateDataToDatabaseInputVo(
        @Schema(description = "좌표 위도", required = true, example = "37.676563")
        @JsonProperty("latitude")
        val latitude: Double,
        @Schema(description = "좌표 경도", required = true, example = "126.761259")
        @JsonProperty("longitude")
        val longitude: Double
    )

    data class InsertCoordinateDataToDatabaseOutputVo(
        @Schema(description = "DB 좌표 리스트", required = true)
        @JsonProperty("coordinateList")
        val coordinateList: List<Coordinate>,
        @Schema(description = "좌표 리스트들의 중심좌표", required = true)
        @JsonProperty("centerCoordinate")
        val centerCoordinate: Coordinate
    ) {
        @Schema(description = "좌표 데이터")
        data class Coordinate(
            @Schema(description = "중심좌표 위도", required = true, example = "37.676563")
            @JsonProperty("centerLatitude")
            val centerLatitude: Double,
            @Schema(description = "중심좌표 경도", required = true, example = "126.761259")
            @JsonProperty("centerLongitude")
            val centerLongitude: Double
        )
    }


    // ----
    @Operation(
        summary = "DB 의 좌표 테이블의 모든 데이터 삭제",
        description = "DB 의 좌표 테이블의 모든 데이터 삭제 API"
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
        path = ["/test-map/coordinate/all"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteAllCoordinateDataFromDatabase(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.deleteAllCoordinateDataFromDatabase(httpServletResponse)
    }


    // ----
    @Operation(
        summary = "DB 에 저장된 좌표들을 SQL 을 사용하여, 기준 좌표의 N Km 내의 결과만 필터",
        description = "기준 좌표를 입력하면 DB 에 저장된 좌표들과의 거리를 SQL 로 계산하여 N Km 내의 결과만 필터링 하여 리스트로 반환하는 API"
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
        path = ["/test-map/coordinate-in-round"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectCoordinateDataRowsInRadiusKiloMeterSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "anchorLatitude", description = "기준 좌표 위도", example = "37.675683")
        @RequestParam("anchorLatitude")
        anchorLatitude: Double,
        @Parameter(name = "anchorLongitude", description = "기준 좌표 경도", example = "126.761259")
        @RequestParam("anchorLongitude")
        anchorLongitude: Double,
        @Parameter(
            name = "radiusKiloMeter",
            description = "기준 좌표를 중심으로 몇 Km 안까지의 결과를 가져올지",
            example = "12.54"
        )
        @RequestParam("radiusKiloMeter")
        radiusKiloMeter: Double
    ): SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo? {
        return service.selectCoordinateDataRowsInRadiusKiloMeterSample(
            httpServletResponse,
            anchorLatitude,
            anchorLongitude,
            radiusKiloMeter
        )
    }

    data class SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo(
        @Schema(description = "결과 리스트", required = true)
        @JsonProperty("resultList")
        val resultList: List<CoordinateCalcResult>
    ) {
        @Schema(description = "결과 좌표")
        data class CoordinateCalcResult(
            @Schema(description = "고유번호", required = true, example = "1")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "좌표 위도", required = true, example = "37.676563")
            @JsonProperty("latitude")
            val latitude: Double,
            @Schema(description = "좌표 경도", required = true, example = "126.761259")
            @JsonProperty("longitude")
            val longitude: Double,
            @Schema(description = "기준 좌표와의 거리 (Km)", required = true, example = "15.214")
            @JsonProperty("distanceMeter")
            val distanceKiloMeter: Double
        )
    }


    // ----
    @Operation(
        summary = "DB 에 저장된 좌표들을 SQL 을 사용하여, 북서 좌표에서 남동 좌표까지의 사각 영역 안에 들어오는 좌표들만 필터링하여 반환",
        description = "북, 서, 남, 동 좌표를 입력하면 DB 에 저장된 좌표들 중 좌표 사각 영역 안에 들어오는 좌표를 필터링 하여 리스트로 반환하는 API"
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
        path = ["/test-map/coordinate-in-box"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectCoordinateDataRowsInCoordinateBoxSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "northLatitude", description = "북위도", example = "37.771848")
        @RequestParam("northLatitude")
        northLatitude: Double,
        @Parameter(name = "eastLongitude", description = "동경도", example = "127.433549")
        @RequestParam("eastLongitude")
        eastLongitude: Double,
        @Parameter(name = "southLatitude", description = "남위도", example = "37.245683")
        @RequestParam("southLatitude")
        southLatitude: Double,
        @Parameter(name = "westLongitude", description = "서경도", example = "126.587602")
        @RequestParam("westLongitude")
        westLongitude: Double
    ): SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo? {
        return service.selectCoordinateDataRowsInCoordinateBoxSample(
            httpServletResponse,
            northLatitude,
            eastLongitude,
            southLatitude,
            westLongitude
        )
    }

    data class SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo(
        @Schema(description = "결과 좌표 리스트", required = true)
        @JsonProperty("resultList")
        val resultList: List<CoordinateCalcResult>
    ) {
        @Schema(description = "결과 좌표")
        data class CoordinateCalcResult(
            @Schema(description = "고유번호", required = true, example = "1")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "좌표 위도", required = true, example = "37.676563")
            @JsonProperty("latitude")
            val latitude: Double,
            @Schema(description = "좌표 경도", required = true, example = "126.761259")
            @JsonProperty("longitude")
            val longitude: Double
        )
    }
}