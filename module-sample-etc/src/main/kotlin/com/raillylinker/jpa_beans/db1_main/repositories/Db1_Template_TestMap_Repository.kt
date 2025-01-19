package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_TestMap
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface Db1_Template_TestMap_Repository : JpaRepository<Db1_Template_TestMap, Long> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            *, 
            (
                6371 * acos(
                    cos(radians(latitude)) * 
                    cos(radians(:latitude)) * 
                    cos(radians(:longitude) - 
                    radians(longitude)) + 
                    sin(radians(latitude)) * 
                    sin(radians(:latitude))
                )
            ) AS distanceKiloMeter 
            FROM 
            template.test_map 
            WHERE 
            row_delete_date_str = '/' 
            HAVING 
            distanceKiloMeter <= :radiusKiloMeter 
            ORDER BY 
            distanceKiloMeter
            """
    )
    fun findAllFromTemplateTestMapInnerHaversineCoordDistanceArea(
        @Param(value = "latitude") latitude: Double,
        @Param(value = "longitude") longitude: Double,
        @Param(value = "radiusKiloMeter") radiusKiloMeter: Double
    ): List<FindAllFromTemplateTestMapInnerHaversineCoordDistanceAreaOutputVo>

    interface FindAllFromTemplateTestMapInnerHaversineCoordDistanceAreaOutputVo {
        var uid: Long
        var latitude: Double
        var longitude: Double
        var distanceKiloMeter: Double
    }


    // ----
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            * 
            FROM 
            template.test_map 
            WHERE 
            row_delete_date_str = '/' AND 
            latitude BETWEEN :southLatitude AND :northLatitude 
            AND 
            (
                (
                    :westLongitude <= :eastLongitude AND 
                    longitude BETWEEN :westLongitude AND :eastLongitude
                )
                OR
                (
                    :westLongitude > :eastLongitude AND 
                    (
                        longitude >= :westLongitude OR 
                        longitude <= :eastLongitude
                    )
                )
            )
            """
    )
    fun findAllFromTemplateTestMapInnerCoordSquareArea(
        @Param(value = "northLatitude") northLatitude: Double,
        @Param(value = "eastLongitude") eastLongitude: Double,
        @Param(value = "southLatitude") southLatitude: Double,
        @Param(value = "westLongitude") westLongitude: Double
    ): List<FindAllFromTemplateTestMapInnerCoordSquareAreaOutputVo>

    interface FindAllFromTemplateTestMapInnerCoordSquareAreaOutputVo {
        var uid: Long
        var latitude: Double
        var longitude: Double
    }
}