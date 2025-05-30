package com.raillylinker.services

import com.raillylinker.controllers.MapCoordinateCalculationController
import com.raillylinker.util_components.MapCoordinateUtil
import com.raillylinker.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_TestMap
import com.raillylinker.jpa_beans.db1_main.repositories.Db1_Template_TestMap_Repository
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MapCoordinateCalculationService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val mapCoordinateUtil: MapCoordinateUtil,

    // (Database Repository)
    private val db1TemplateTestMapRepository: Db1_Template_TestMap_Repository
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (테스트용 기본 좌표 리스트를 DB에 저장)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun insertDefaultCoordinateDataToDatabase(httpServletResponse: HttpServletResponse) {
        db1TemplateTestMapRepository.deleteAll()

        val latLngList: List<Pair<Double, Double>> = listOf(
            Pair(37.5845885, 127.0001891),
            Pair(37.6060504, 126.9607987),
            Pair(37.5844214, 126.9699813),
            Pair(37.5757558, 126.9710255),
            Pair(37.5764907, 126.968655),
            Pair(37.5786667, 127.0156223),
            Pair(37.561697, 126.9968491),
            Pair(37.5880051, 127.0181872),
            Pair(37.5713246, 126.9635654),
            Pair(37.5922066, 127.0135319),
            Pair(37.5690038, 126.9632755),
            Pair(37.584865, 126.948639),
            Pair(37.5690454, 127.0232121),
            Pair(37.5634635, 127.015948),
            Pair(37.5748642, 127.0155003),
            Pair(37.5708604, 126.9612919),
            Pair(37.5570078, 126.9533333),
            Pair(37.5726188, 127.0576283),
            Pair(37.5914225, 127.0129648),
            Pair(37.5659102, 127.0217363)
        )

        for (latLng in latLngList) {
            db1TemplateTestMapRepository.save(
                Db1_Template_TestMap(
                    latLng.first,
                    latLng.second
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (두 좌표 사이의 거리를 반환(하버사인 공식))
    fun getDistanceMeterBetweenTwoCoordinate(
        httpServletResponse: HttpServletResponse,
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): MapCoordinateCalculationController.GetDistanceMeterBetweenTwoCoordinateOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MapCoordinateCalculationController.GetDistanceMeterBetweenTwoCoordinateOutputVo(
            mapCoordinateUtil.getDistanceMeterBetweenTwoLatLngCoordinateHarversine(
                Pair(latitude1, longitude1),
                Pair(latitude2, longitude2)
            )
        )
    }


    // ----
    // (두 좌표 사이의 거리를 반환(Vincenty 공식))
    fun getDistanceMeterBetweenTwoCoordinateVincenty(
        httpServletResponse: HttpServletResponse,
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): MapCoordinateCalculationController.GetDistanceMeterBetweenTwoCoordinateVincentyOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MapCoordinateCalculationController.GetDistanceMeterBetweenTwoCoordinateVincentyOutputVo(
            mapCoordinateUtil.getDistanceMeterBetweenTwoLatLngCoordinateVincenty(
                Pair(latitude1, longitude1),
                Pair(latitude2, longitude2)
            )
        )
    }


    // ----
    // (좌표들 사이의 중심 좌표를 반환)
    fun returnCenterCoordinate(
        httpServletResponse: HttpServletResponse,
        inputVo: MapCoordinateCalculationController.ReturnCenterCoordinateInputVo
    ): MapCoordinateCalculationController.ReturnCenterCoordinateOutputVo? {
        val latLngCoordinate = ArrayList<Pair<Double, Double>>()

        for (coordinate in inputVo.coordinateList) {
            latLngCoordinate.add(
                Pair(coordinate.centerLatitude, coordinate.centerLongitude)
            )
        }

        val centerCoordinate = mapCoordinateUtil.getCenterLatLngCoordinate(
            latLngCoordinate
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return MapCoordinateCalculationController.ReturnCenterCoordinateOutputVo(
            centerCoordinate.first,
            centerCoordinate.second
        )
    }


    // ----
    // (DB 의 좌표 테이블에 좌표 정보를 저장)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun insertCoordinateDataToDatabase(
        httpServletResponse: HttpServletResponse,
        inputVo: MapCoordinateCalculationController.InsertCoordinateDataToDatabaseInputVo
    ): MapCoordinateCalculationController.InsertCoordinateDataToDatabaseOutputVo? {
        db1TemplateTestMapRepository.save(
            Db1_Template_TestMap(
                inputVo.latitude,
                inputVo.longitude
            )
        )

        val coordinateList =
            ArrayList<MapCoordinateCalculationController.InsertCoordinateDataToDatabaseOutputVo.Coordinate>()
        val latLngCoordinate = ArrayList<Pair<Double, Double>>()

        for (testMap in db1TemplateTestMapRepository.findAll()) {
            coordinateList.add(
                MapCoordinateCalculationController.InsertCoordinateDataToDatabaseOutputVo.Coordinate(
                    testMap.latitude,
                    testMap.longitude
                )
            )

            latLngCoordinate.add(
                Pair(testMap.latitude, testMap.longitude)
            )
        }

        val centerCoordinate = mapCoordinateUtil.getCenterLatLngCoordinate(
            latLngCoordinate
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return MapCoordinateCalculationController.InsertCoordinateDataToDatabaseOutputVo(
            coordinateList,
            MapCoordinateCalculationController.InsertCoordinateDataToDatabaseOutputVo.Coordinate(
                centerCoordinate.first,
                centerCoordinate.second
            )
        )
    }


    // ----
    // (DB 의 좌표 테이블의 모든 데이터 삭제)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteAllCoordinateDataFromDatabase(httpServletResponse: HttpServletResponse) {
        db1TemplateTestMapRepository.deleteAll()

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (DB 에 저장된 좌표들을 SQL 을 사용하여, 기준 좌표의 N Km 내의 결과만 필터)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectCoordinateDataRowsInRadiusKiloMeterSample(
        httpServletResponse: HttpServletResponse,
        anchorLatitude: Double,
        anchorLongitude: Double,
        radiusKiloMeter: Double
    ): MapCoordinateCalculationController.SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo? {
        val entityList =
            db1TemplateTestMapRepository.findAllFromTemplateTestMapInnerHaversineCoordDistanceArea(
                anchorLatitude,
                anchorLongitude,
                radiusKiloMeter
            )

        val coordinateCalcResultList =
            ArrayList<MapCoordinateCalculationController.SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo.CoordinateCalcResult>()
        for (entity in entityList) {
            coordinateCalcResultList.add(
                MapCoordinateCalculationController.SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo.CoordinateCalcResult(
                    entity.uid,
                    entity.latitude,
                    entity.longitude,
                    entity.distanceKiloMeter
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MapCoordinateCalculationController.SelectCoordinateDataRowsInRadiusKiloMeterSampleOutputVo(
            coordinateCalcResultList
        )
    }


    // ----
    // (DB 에 저장된 좌표들을 SQL 을 사용하여, 북서 좌표에서 남동 좌표까지의 사각 영역 안에 들어오는 좌표들만 필터링하여 반환)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectCoordinateDataRowsInCoordinateBoxSample(
        httpServletResponse: HttpServletResponse,
        northLatitude: Double, // 북위도 (ex : 37.771848)
        eastLongitude: Double, // 동경도 (ex : 127.433549)
        southLatitude: Double, // 남위도 (ex : 37.245683)
        westLongitude: Double // 남경도 (ex : 126.587602)
    ): MapCoordinateCalculationController.SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo? {
        val entityList =
            db1TemplateTestMapRepository.findAllFromTemplateTestMapInnerCoordSquareArea(
                northLatitude,
                eastLongitude,
                southLatitude,
                westLongitude
            )

        val coordinateCalcResultList =
            ArrayList<MapCoordinateCalculationController.SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo.CoordinateCalcResult>()
        for (entity in entityList) {
            coordinateCalcResultList.add(
                MapCoordinateCalculationController.SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo.CoordinateCalcResult(
                    entity.uid,
                    entity.latitude,
                    entity.longitude
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MapCoordinateCalculationController.SelectCoordinateDataRowsInCoordinateBoxSampleOutputVo(
            coordinateCalcResultList
        )
    }
}