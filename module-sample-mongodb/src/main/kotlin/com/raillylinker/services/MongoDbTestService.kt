package com.raillylinker.services

import com.raillylinker.controllers.MongoDbTestController
import com.raillylinker.configurations.mongodb_configs.Mdb1MainConfig
import com.raillylinker.mongodb_beans.mdb1_main.documents.Mdb1_LogicalDeleteUniqueData
import com.raillylinker.mongodb_beans.mdb1_main.documents.Mdb1_TestData
import com.raillylinker.mongodb_beans.mdb1_main.repositories.Mdb1_LogicalDeleteUniqueData_Repository
import com.raillylinker.mongodb_beans.mdb1_main.repositories.Mdb1_TestData_Repository
import com.raillylinker.mongodb_beans.mdb1_main.repositories_template.Mdb1_TestData_Repository_Template
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class MongoDbTestService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,
    private val mdb1TestDataRepository: Mdb1_TestData_Repository,
    private val mdb1TestDataRepositoryTemplate: Mdb1_TestData_Repository_Template,
    private val mbdMdb1LogicalDeleteUniqueDataRepository: Mdb1_LogicalDeleteUniqueData_Repository
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (DB document 입력 테스트 API)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME)
    fun insertDataSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MongoDbTestController.InsertDataSampleInputVo
    ): MongoDbTestController.InsertDataSampleOutputVo? {
        val resultCollection = mdb1TestDataRepository.save(
            Mdb1_TestData(
                inputVo.content,
                (0..99999999).random(),
                ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                inputVo.nullableValue
            )
        )

        httpServletResponse.setHeader("api-result-code", "")
        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.InsertDataSampleOutputVo(
            resultCollection.uid!!.toString(),
            resultCollection.content,
            resultCollection.randomNum,
            resultCollection.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            resultCollection.nullableValue,
            resultCollection.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            resultCollection.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            resultCollection.rowDeleteDateStr
        )
    }


    // ----
    // (DB Rows 삭제 테스트 API)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun deleteRowsSample(httpServletResponse: HttpServletResponse, deleteLogically: Boolean) {
        if (deleteLogically) {
            val entityList = mdb1TestDataRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
            for (entity in entityList) {
                entity.rowDeleteDateStr =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                mdb1TestDataRepository.save(entity)
            }
        } else {
            mdb1TestDataRepository.deleteAll()
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (DB Row 삭제 테스트)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun deleteRowSample(httpServletResponse: HttpServletResponse, id: String, deleteLogically: Boolean) {
        val entity = mdb1TestDataRepository.findByUidAndRowDeleteDateStr(id, "/")

        if (entity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (deleteLogically) {
            entity.rowDeleteDateStr =
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            mdb1TestDataRepository.save(entity)
        } else {
            mdb1TestDataRepository.deleteById(id)
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (DB Rows 조회 테스트)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectRowsSample(httpServletResponse: HttpServletResponse): MongoDbTestController.SelectRowsSampleOutputVo? {
        val resultEntityList =
            mdb1TestDataRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
        val entityVoList =
            ArrayList<MongoDbTestController.SelectRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                MongoDbTestController.SelectRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.content,
                    resultEntity.randomNum,
                    resultEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.nullableValue,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        val logicalDeleteEntityVoList =
            mdb1TestDataRepository.findAllByRowDeleteDateStrNotOrderByRowCreateDate("/")
        val logicalDeleteVoList =
            ArrayList<MongoDbTestController.SelectRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in logicalDeleteEntityVoList) {
            logicalDeleteVoList.add(
                MongoDbTestController.SelectRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.content,
                    resultEntity.randomNum,
                    resultEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.nullableValue,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectRowsSampleOutputVo(
            entityVoList,
            logicalDeleteVoList
        )
    }


    // ----
    // (DB 테이블의 random_num 컬럼 근사치 기준으로 정렬한 리스트 조회 API)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectRowsOrderByRandomNumSample(
        httpServletResponse: HttpServletResponse,
        num: Int
    ): MongoDbTestController.SelectRowsOrderByRandomNumSampleOutputVo? {
        val foundEntityList =
            mdb1TestDataRepositoryTemplate.findAllFromTemplateTestDataByNotDeletedWithRandomNumDistance(num)

        val testEntityVoList =
            ArrayList<MongoDbTestController.SelectRowsOrderByRandomNumSampleOutputVo.TestEntityVo>()

        for (entity in foundEntityList) {
            testEntityVoList.add(
                MongoDbTestController.SelectRowsOrderByRandomNumSampleOutputVo.TestEntityVo(
                    entity.uid,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.nullableValue,
                    entity.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.distance
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectRowsOrderByRandomNumSampleOutputVo(
            testEntityVoList
        )
    }


    // ----
    // (DB 테이블의 row_create_date 컬럼 근사치 기준으로 정렬한 리스트 조회 API)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectRowsOrderByRowCreateDateSample(
        httpServletResponse: HttpServletResponse,
        dateString: String
    ): MongoDbTestController.SelectRowsOrderByRowCreateDateSampleOutputVo? {
        val foundEntityList =
            mdb1TestDataRepositoryTemplate.findAllFromTemplateTestDataByNotDeletedWithRowCreateDateDistance(
                ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            )

        val testEntityVoList =
            ArrayList<MongoDbTestController.SelectRowsOrderByRowCreateDateSampleOutputVo.TestEntityVo>()

        for (entity in foundEntityList) {
            testEntityVoList.add(
                MongoDbTestController.SelectRowsOrderByRowCreateDateSampleOutputVo.TestEntityVo(
                    entity.uid,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.nullableValue,
                    entity.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.timeDiffMicroSec
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectRowsOrderByRowCreateDateSampleOutputVo(
            testEntityVoList
        )
    }


    // ----
    // (DB Rows 조회 테스트 (페이징))
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectRowsPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int
    ): MongoDbTestController.SelectRowsPageSampleOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
        val entityList = mdb1TestDataRepository.findAllByRowDeleteDateStrOrderByRowCreateDate(
            "/",
            pageable
        )

        val testEntityVoList =
            ArrayList<MongoDbTestController.SelectRowsPageSampleOutputVo.TestEntityVo>()
        for (entity in entityList) {
            testEntityVoList.add(
                MongoDbTestController.SelectRowsPageSampleOutputVo.TestEntityVo(
                    entity.uid!!,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.nullableValue,
                    entity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectRowsPageSampleOutputVo(
            entityList.totalElements,
            testEntityVoList
        )
    }


    // ----
    // (DB Rows 조회 테스트 (네이티브 쿼리 페이징))
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectRowsNativeQueryPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        num: Int
    ): MongoDbTestController.SelectRowsNativeQueryPageSampleOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
        val voList = mdb1TestDataRepositoryTemplate.findPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistance(
            num,
            pageable
        )

        val testEntityVoList =
            ArrayList<MongoDbTestController.SelectRowsNativeQueryPageSampleOutputVo.TestEntityVo>()
        for (vo in voList) {
            testEntityVoList.add(
                MongoDbTestController.SelectRowsNativeQueryPageSampleOutputVo.TestEntityVo(
                    vo.uid,
                    vo.content,
                    vo.randomNum,
                    vo.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.nullableValue,
                    vo.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.distance
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectRowsNativeQueryPageSampleOutputVo(
            voList.totalElements,
            testEntityVoList
        )
    }


    // ----
    // (DB Row 수정 테스트)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun updateRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: String,
        inputVo: MongoDbTestController.UpdateRowSampleInputVo
    ): MongoDbTestController.UpdateRowSampleOutputVo? {
        val oldEntity = mdb1TestDataRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")

        if (oldEntity == null || oldEntity.rowDeleteDateStr != "/") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        oldEntity.content = inputVo.content
        oldEntity.testDatetime =
            ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        oldEntity.nullableValue = inputVo.nullableValue

        val result = mdb1TestDataRepository.save(oldEntity)

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.UpdateRowSampleOutputVo(
            result.uid!!.toString(),
            result.content,
            result.randomNum,
            result.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.nullableValue,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowDeleteDateStr
        )
    }


    // ----
    // (트랜젝션 동작 테스트)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun transactionRollbackTest(
        httpServletResponse: HttpServletResponse
    ) {
        mdb1TestDataRepository.save(
            Mdb1_TestData(
                "test",
                (0..99999999).random(),
                LocalDateTime.now(),
                null
            )
        )

        throw RuntimeException("Transaction Rollback Test!")
    }


    // ----
    // (트랜젝션 비동작 테스트)
    fun noTransactionRollbackTest(
        httpServletResponse: HttpServletResponse
    ) {
        mdb1TestDataRepository.save(
            Mdb1_TestData(
                "test",
                (0..99999999).random(),
                LocalDateTime.now(),
                null
            )
        )

        throw RuntimeException("No Transaction Exception Test!")
    }


    // ----
    // (트랜젝션 비동작 테스트(try-catch))
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun tryCatchNonTransactionTest(httpServletResponse: HttpServletResponse) {
        // @CustomTransactional 이 붙어있고, Exception 이 발생해도, 함수 내에서 try catch 로 처리하여 함수 외부로는 전파되지 않기에,
        // 트랜젝션 롤백이 발생하지 않습니다.
        try {
            mdb1TestDataRepository.save(
                Mdb1_TestData(
                    "test",
                    (0..99999999).random(),
                    LocalDateTime.now(),
                    null
                )
            )

            throw RuntimeException("Transaction Rollback Test!")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // ----
    // (DB Rows 조회 테스트 (카운팅))
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectRowsCountSample(httpServletResponse: HttpServletResponse): MongoDbTestController.SelectRowsCountSampleOutputVo? {
        val count = mdb1TestDataRepository.countByRowDeleteDateStr("/")

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectRowsCountSampleOutputVo(count)
    }


    // ----
    // (DB Rows 조회 테스트 (네이티브 카운팅))
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectRowsCountByNativeQuerySample(httpServletResponse: HttpServletResponse): MongoDbTestController.SelectRowsCountByNativeQuerySampleOutputVo? {
        val count: Long = mdb1TestDataRepositoryTemplate.countFromTemplateTestDataByNotDeleted()?.count ?: 0L

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectRowsCountByNativeQuerySampleOutputVo(count)
    }


    // ----
    // (DB Row 조회 테스트 (네이티브))
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectRowByNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: String
    ): MongoDbTestController.SelectRowByNativeQuerySampleOutputVo? {
        val entity = mdb1TestDataRepositoryTemplate.findFromTemplateTestDataByNotDeletedAndUid(testTableUid)

        if (entity == null) {
            httpServletResponse.status = HttpStatus.OK.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectRowByNativeQuerySampleOutputVo(
            entity.uid,
            entity.content,
            entity.randomNum,
            entity.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            entity.nullableValue,
            entity.rowCreateDate.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    // ----
    // (유니크 테스트 테이블 Row 입력 API)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun insertUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MongoDbTestController.InsertUniqueTestTableRowSampleInputVo
    ): MongoDbTestController.InsertUniqueTestTableRowSampleOutputVo? {
        val result = mbdMdb1LogicalDeleteUniqueDataRepository.save(
            Mdb1_LogicalDeleteUniqueData(
                inputVo.uniqueValue
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.InsertUniqueTestTableRowSampleOutputVo(
            result.uid!!,
            result.uniqueValue,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowDeleteDateStr
        )
    }


    // ----
    // (유니크 테스트 테이블 Rows 조회 테스트)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun selectUniqueTestTableRowsSample(httpServletResponse: HttpServletResponse): MongoDbTestController.SelectUniqueTestTableRowsSampleOutputVo? {
        val resultEntityList =
            mbdMdb1LogicalDeleteUniqueDataRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
        val entityVoList =
            ArrayList<MongoDbTestController.SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                MongoDbTestController.SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.uniqueValue,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        val logicalDeleteEntityVoList =
            mbdMdb1LogicalDeleteUniqueDataRepository.findAllByRowDeleteDateStrNotOrderByRowCreateDate("/")
        val logicalDeleteVoList =
            ArrayList<MongoDbTestController.SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in logicalDeleteEntityVoList) {
            logicalDeleteVoList.add(
                MongoDbTestController.SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.uniqueValue,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectUniqueTestTableRowsSampleOutputVo(
            entityVoList,
            logicalDeleteVoList
        )
    }


    // ----
    // (유니크 테스트 테이블 Row 수정 테스트)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun updateUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: String,
        inputVo: MongoDbTestController.UpdateUniqueTestTableRowSampleInputVo
    ): MongoDbTestController.UpdateUniqueTestTableRowSampleOutputVo? {
        val oldEntity =
            mbdMdb1LogicalDeleteUniqueDataRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")

        if (oldEntity == null || oldEntity.rowDeleteDateStr != "/") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val uniqueValueEntity =
            mbdMdb1LogicalDeleteUniqueDataRepository.findByUniqueValueAndRowDeleteDateStr(
                inputVo.uniqueValue,
                "/"
            )

        if (uniqueValueEntity != null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }


        oldEntity.uniqueValue = inputVo.uniqueValue

        val result = mbdMdb1LogicalDeleteUniqueDataRepository.save(oldEntity)

        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.UpdateUniqueTestTableRowSampleOutputVo(
            result.uid!!,
            result.uniqueValue,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    // ----
    // (유니크 테스트 테이블 Row 삭제 테스트)
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    fun deleteUniqueTestTableRowSample(httpServletResponse: HttpServletResponse, id: String) {
        val entity = mbdMdb1LogicalDeleteUniqueDataRepository.findByUidAndRowDeleteDateStr(id, "/")

        if (entity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        entity.rowDeleteDateStr =
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        mbdMdb1LogicalDeleteUniqueDataRepository.save(entity)

        httpServletResponse.status = HttpStatus.OK.value()
    }
}