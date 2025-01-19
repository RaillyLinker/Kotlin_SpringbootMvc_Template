package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_JustBooleanTest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface Db1_Template_JustBooleanTest_Repository : JpaRepository<Db1_Template_JustBooleanTest, Long> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            true AS normalBoolValue, 
            (TRUE = :inputVal) AS funcBoolValue, 
            IF
            (
                (TRUE = :inputVal), 
                TRUE, 
                FALSE
            ) AS ifBoolValue, 
            (
                CASE 
                    WHEN 
                        (TRUE = :inputVal) 
                    THEN 
                        TRUE 
                    ELSE 
                        FALSE 
                END
            ) AS caseBoolValue, 
            (
                SELECT 
                bool_value 
                FROM 
                template.just_boolean_test 
                WHERE 
                uid = 1
            ) AS tableColumnBoolValue
            """
    )
    fun multiCaseBooleanReturnTest(
        @Param(value = "inputVal") inputVal: Boolean
    ): MultiCaseBooleanReturnTestOutputVo

    interface MultiCaseBooleanReturnTestOutputVo {
        var normalBoolValue: Long
        var funcBoolValue: Long
        var ifBoolValue: Long
        var caseBoolValue: Long
        var tableColumnBoolValue: Boolean
    }
}