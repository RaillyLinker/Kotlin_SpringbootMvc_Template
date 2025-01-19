package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_PublicHolidayKorea
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface Db1_Template_PublicHolidayKorea_Repository : JpaRepository<Db1_Template_PublicHolidayKorea, Long> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT
            public_holiday_korea.holiday_date as holidayDate,
            public_holiday_korea.holiday_name as holidayName
            FROM
            template.public_holiday_korea as public_holiday_korea
            WHERE
            public_holiday_korea.row_delete_date_str = '/' AND
            YEAR(public_holiday_korea.holiday_date) = :anchorYear
            """
    )
    fun findAllThisYearPublicHolidayList(
        @Param(value = "anchorYear")
        anchorYear: Int
    ): List<FindAllThisYearPublicHolidayListOutputVo>

    interface FindAllThisYearPublicHolidayListOutputVo {
        var holidayDate: LocalDate
        var holidayName: String
    }
}