package com.raillylinker.mongodb_beans.mdb1_main.repositories_template

import com.raillylinker.configurations.mongodb_configs.Mdb1MainConfig
import com.raillylinker.mongodb_beans.mdb1_main.documents.Mdb1_TestData
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class Mdb1_TestData_Repository_Template(
    @Qualifier(Mdb1MainConfig.MONGO_DB_DIRECTORY_NAME)
    private val mongoTemplate: MongoTemplate
) {

    fun findAllFromTemplateTestDataByNotDeletedWithRandomNumDistance(num: Int): List<FindAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("rowDeleteDateStr").`is`("/")),
            Aggregation.project(
                "content",
                "randomNum",
                "testDatetime",
                "nullableValue",
                "uid",
                "rowCreateDate",
                "rowUpdateDate",
                "rowDeleteDateStr"
            )
                .andExpression("abs(randomNum - $num)").`as`("distance"),
            Aggregation.sort(Sort.by(Sort.Order.asc("distance")))
        )

        return mongoTemplate.aggregate(
            aggregation,
            Mdb1_TestData::class.java,
            FindAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo::class.java
        ).mappedResults
    }

    data class FindAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo(
        val content: String,
        val randomNum: Int,
        val testDatetime: LocalDateTime,
        val nullableValue: String?,
        val uid: String,
        val rowCreateDate: LocalDateTime,
        val rowUpdateDate: LocalDateTime,
        val rowDeleteDateStr: String,
        val distance: Int // 차이값 추가
    )
}