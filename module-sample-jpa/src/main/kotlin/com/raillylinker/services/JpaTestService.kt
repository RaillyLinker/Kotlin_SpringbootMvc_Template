package com.raillylinker.services

import com.raillylinker.controllers.JpaTestController
import com.raillylinker.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.jpa_beans.db1_main.entities.*
import com.raillylinker.jpa_beans.db1_main.repositories.*
import com.raillylinker.jpa_beans.db1_main.repositories_dsl.Db1_Template_RepositoryDsl
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class JpaTestService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    // (Database Repository)
    private val db1NativeRepository: Db1_Native_Repository,
    private val db1TemplateTestsRepository: Db1_Template_Tests_Repository,
    private val db1TemplateFkTestParentRepository: Db1_Template_FkTestParent_Repository,
    private val db1TemplateFkTestManyToOneChildRepository: Db1_Template_FkTestManyToOneChild_Repository,
    private val db1TemplateLogicalDeleteUniqueDataRepository: Db1_Template_LogicalDeleteUniqueData_Repository,
    private val db1TemplateJustBooleanTestRepository: Db1_Template_JustBooleanTest_Repository,
    private val db1TemplateDataTypeMappingTestRepository: Db1_Template_DataTypeMappingTest_Repository,

    // (Database Repository DSL)
    private val db1TemplateRepositoryDsl: Db1_Template_RepositoryDsl
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (DB Row 입력 테스트 API)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun insertDataSample(
        httpServletResponse: HttpServletResponse,
        inputVo: JpaTestController.InsertDataSampleInputVo
    ): JpaTestController.InsertDataSampleOutputVo? {
        val result = db1TemplateTestsRepository.save(
            Db1_Template_TestData(
                inputVo.content,
                (0..99999999).random(),
                ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.InsertDataSampleOutputVo(
            result.uid!!,
            result.content,
            result.randomNum,
            result.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowDeleteDateStr
        )
    }


    // ----
    // (DB Rows 삭제 테스트 API)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRowsSample(httpServletResponse: HttpServletResponse, deleteLogically: Boolean) {
        if (deleteLogically) {
            val entityList = db1TemplateTestsRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
            for (entity in entityList) {
                entity.rowDeleteDateStr =
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                db1TemplateTestsRepository.save(entity)
            }
        } else {
            db1TemplateTestsRepository.deleteAll()
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (DB Row 삭제 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteRowSample(httpServletResponse: HttpServletResponse, index: Long, deleteLogically: Boolean) {
        val entity = db1TemplateTestsRepository.findByUidAndRowDeleteDateStr(index, "/")

        if (entity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (deleteLogically) {
            entity.rowDeleteDateStr =
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            db1TemplateTestsRepository.save(entity)
        } else {
            db1TemplateTestsRepository.deleteById(index)
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (DB Rows 조회 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowsSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectRowsSampleOutputVo? {
        val resultEntityList =
            db1TemplateTestsRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
        val entityVoList =
            ArrayList<JpaTestController.SelectRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                JpaTestController.SelectRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.content,
                    resultEntity.randomNum,
                    resultEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        val logicalDeleteEntityVoList =
            db1TemplateTestsRepository.findAllByRowDeleteDateStrNotOrderByRowCreateDate("/")
        val logicalDeleteVoList =
            ArrayList<JpaTestController.SelectRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in logicalDeleteEntityVoList) {
            logicalDeleteVoList.add(
                JpaTestController.SelectRowsSampleOutputVo.TestEntityVo(
                    resultEntity.uid!!,
                    resultEntity.content,
                    resultEntity.randomNum,
                    resultEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowDeleteDateStr
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowsSampleOutputVo(
            entityVoList,
            logicalDeleteVoList
        )
    }


    // ----
    // (DB 테이블의 random_num 컬럼 근사치 기준으로 정렬한 리스트 조회 API)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowsOrderByRandomNumSample(
        httpServletResponse: HttpServletResponse,
        num: Int
    ): JpaTestController.SelectRowsOrderByRandomNumSampleOutputVo? {
        val foundEntityList = db1NativeRepository.findAllFromTemplateTestDataByNotDeletedWithRandomNumDistance(num)

        val testEntityVoList =
            ArrayList<JpaTestController.SelectRowsOrderByRandomNumSampleOutputVo.TestEntityVo>()

        for (entity in foundEntityList) {
            testEntityVoList.add(
                JpaTestController.SelectRowsOrderByRandomNumSampleOutputVo.TestEntityVo(
                    entity.uid,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.distance
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowsOrderByRandomNumSampleOutputVo(
            testEntityVoList
        )
    }


    // ----
    // (DB 테이블의 row_create_date 컬럼 근사치 기준으로 정렬한 리스트 조회 API)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowsOrderByRowCreateDateSample(
        httpServletResponse: HttpServletResponse,
        dateString: String
    ): JpaTestController.SelectRowsOrderByRowCreateDateSampleOutputVo? {
        val foundEntityList = db1NativeRepository.findAllFromTemplateTestDataByNotDeletedWithRowCreateDateDistance(
            ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        )

        val testEntityVoList =
            ArrayList<JpaTestController.SelectRowsOrderByRowCreateDateSampleOutputVo.TestEntityVo>()

        for (entity in foundEntityList) {
            testEntityVoList.add(
                JpaTestController.SelectRowsOrderByRowCreateDateSampleOutputVo.TestEntityVo(
                    entity.uid,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.timeDiffMicroSec
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowsOrderByRowCreateDateSampleOutputVo(
            testEntityVoList
        )
    }


    // ----
    // (DB Rows 조회 테스트 (페이징))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowsPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int
    ): JpaTestController.SelectRowsPageSampleOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
        val entityList = db1TemplateTestsRepository.findAllByRowDeleteDateStrOrderByRowCreateDate(
            "/",
            pageable
        )

        val testEntityVoList =
            ArrayList<JpaTestController.SelectRowsPageSampleOutputVo.TestEntityVo>()
        for (entity in entityList) {
            testEntityVoList.add(
                JpaTestController.SelectRowsPageSampleOutputVo.TestEntityVo(
                    entity.uid!!,
                    entity.content,
                    entity.randomNum,
                    entity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    entity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowsPageSampleOutputVo(
            entityList.totalElements,
            testEntityVoList
        )
    }


    // ----
    // (DB Rows 조회 테스트 (네이티브 쿼리 페이징))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowsNativeQueryPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        num: Int
    ): JpaTestController.SelectRowsNativeQueryPageSampleOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
        val voList = db1NativeRepository.findPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistance(
            num,
            pageable
        )

        val testEntityVoList =
            ArrayList<JpaTestController.SelectRowsNativeQueryPageSampleOutputVo.TestEntityVo>()
        for (vo in voList) {
            testEntityVoList.add(
                JpaTestController.SelectRowsNativeQueryPageSampleOutputVo.TestEntityVo(
                    vo.uid,
                    vo.content,
                    vo.randomNum,
                    vo.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.distance
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowsNativeQueryPageSampleOutputVo(
            voList.totalElements,
            testEntityVoList
        )
    }


    // ----
    // (DB Row 수정 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun updateRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: JpaTestController.UpdateRowSampleInputVo
    ): JpaTestController.UpdateRowSampleOutputVo? {
        val oldEntity = db1TemplateTestsRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")

        if (oldEntity == null || oldEntity.rowDeleteDateStr != "/") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        oldEntity.content = inputVo.content
        oldEntity.testDatetime =
            ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

        val result = db1TemplateTestsRepository.save(oldEntity)

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.UpdateRowSampleOutputVo(
            result.uid!!,
            result.content,
            result.randomNum,
            result.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    // ----
    // (DB Row 수정 테스트 (네이티브 쿼리))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun updateRowNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: JpaTestController.UpdateRowNativeQuerySampleInputVo
    ) {
        // !! 아래는 네이티브 쿼리로 수정하는 예시를 보인 것으로,
        // 이 경우에는 @UpdateTimestamp, @Version 기능이 자동 적용 되지 않습니다.
        // 고로 수정문은 jpa 를 사용하길 권장합니다. !!
        val testEntity = db1TemplateTestsRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")

        if (testEntity == null || testEntity.rowDeleteDateStr != "/") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            // 트랜젝션 커밋
            return
        }

        db1NativeRepository.updateToTemplateTestDataSetContentAndTestDateTimeByUid(
            testTableUid,
            inputVo.content,
            ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (DB 정보 검색 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowWhereSearchingKeywordSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        searchKeyword: String
    ): JpaTestController.SelectRowWhereSearchingKeywordSampleOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
        val voList = db1NativeRepository.findPageAllFromTemplateTestDataBySearchKeyword(
            searchKeyword,
            pageable
        )

        val testEntityVoList =
            ArrayList<JpaTestController.SelectRowWhereSearchingKeywordSampleOutputVo.TestEntityVo>()
        for (vo in voList) {
            testEntityVoList.add(
                JpaTestController.SelectRowWhereSearchingKeywordSampleOutputVo.TestEntityVo(
                    vo.uid,
                    vo.content,
                    vo.randomNum,
                    vo.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowWhereSearchingKeywordSampleOutputVo(
            voList.totalElements,
            testEntityVoList
        )
    }


    // ----
    // (트랜젝션 동작 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun transactionTest(
        httpServletResponse: HttpServletResponse
    ) {
        db1TemplateTestsRepository.save(
            Db1_Template_TestData(
                "error test",
                (0..99999999).random(),
                LocalDateTime.now()
            )
        )

        throw RuntimeException("Transaction Rollback Test!")
    }


    // ----
    // (트랜젝션 비동작 테스트)
    fun nonTransactionTest(httpServletResponse: HttpServletResponse) {
        db1TemplateTestsRepository.save(
            Db1_Template_TestData(
                "error test",
                (0..99999999).random(),
                LocalDateTime.now()
            )
        )

        throw RuntimeException("No Transaction Exception Test!")
    }


    // ----
    // (트랜젝션 비동작 테스트(try-catch))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun tryCatchNonTransactionTest(httpServletResponse: HttpServletResponse) {
        // @CustomTransactional 이 붙어있고, Exception 이 발생해도, 함수 내에서 try catch 로 처리하여 함수 외부로는 전파되지 않기에,
        // 트랜젝션 롤백이 발생하지 않습니다.
        try {
            db1TemplateTestsRepository.save(
                Db1_Template_TestData(
                    "error test",
                    (0..99999999).random(),
                    LocalDateTime.now()
                )
            )

            throw RuntimeException("Transaction Rollback Test!")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // ----
    // (DB Rows 조회 테스트 (중복 없는 네이티브 쿼리 페이징))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowsNoDuplicatePagingSample(
        httpServletResponse: HttpServletResponse,
        lastItemUid: Long?,
        pageElementsCount: Int
    ): JpaTestController.SelectRowsNoDuplicatePagingSampleOutputVo? {
        // 중복 없는 페이징 쿼리를 사용합니다.
        val voList = db1NativeRepository.findAllFromTemplateTestDataForNoDuplicatedPaging(
            lastItemUid,
            pageElementsCount
        )

        // 전체 개수 카운팅은 따로 해주어야 합니다.
        val count = db1NativeRepository.countFromTemplateTestDataByNotDeleted()

        val testEntityVoList =
            ArrayList<JpaTestController.SelectRowsNoDuplicatePagingSampleOutputVo.TestEntityVo>()
        for (vo in voList) {
            testEntityVoList.add(
                JpaTestController.SelectRowsNoDuplicatePagingSampleOutputVo.TestEntityVo(
                    vo.uid,
                    vo.content,
                    vo.randomNum,
                    vo.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    vo.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowsNoDuplicatePagingSampleOutputVo(
            count,
            testEntityVoList
        )
    }


    // ----
    // (DB Rows 조회 테스트 (카운팅))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowsCountSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectRowsCountSampleOutputVo? {
        val count = db1TemplateTestsRepository.countByRowDeleteDateStr("/")

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowsCountSampleOutputVo(count)
    }


    // ----
    // (DB Rows 조회 테스트 (네이티브 카운팅))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowsCountByNativeQuerySample(httpServletResponse: HttpServletResponse): JpaTestController.SelectRowsCountByNativeQuerySampleOutputVo? {
        val count = db1NativeRepository.countFromTemplateTestDataByNotDeleted()

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowsCountByNativeQuerySampleOutputVo(count)
    }


    // ----
    // (DB Row 조회 테스트 (네이티브))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectRowByNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long
    ): JpaTestController.SelectRowByNativeQuerySampleOutputVo? {
        val entity = db1NativeRepository.findFromTemplateTestDataByNotDeletedAndUid(testTableUid)

        if (entity == null) {
            httpServletResponse.status = HttpStatus.OK.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectRowByNativeQuerySampleOutputVo(
            entity.uid,
            entity.content,
            entity.randomNum,
            entity.testDatetime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            entity.rowCreateDate.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            entity.rowUpdateDate.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    // ----
    // (유니크 테스트 테이블 Row 입력 API)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun insertUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: JpaTestController.InsertUniqueTestTableRowSampleInputVo
    ): JpaTestController.InsertUniqueTestTableRowSampleOutputVo? {
        val result = db1TemplateLogicalDeleteUniqueDataRepository.save(
            Db1_Template_LogicalDeleteUniqueData(
                inputVo.uniqueValue
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.InsertUniqueTestTableRowSampleOutputVo(
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectUniqueTestTableRowsSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectUniqueTestTableRowsSampleOutputVo? {
        val resultEntityList =
            db1TemplateLogicalDeleteUniqueDataRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")
        val entityVoList =
            ArrayList<JpaTestController.SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                JpaTestController.SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo(
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
            db1TemplateLogicalDeleteUniqueDataRepository.findAllByRowDeleteDateStrNotOrderByRowCreateDate("/")
        val logicalDeleteVoList =
            ArrayList<JpaTestController.SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo>()
        for (resultEntity in logicalDeleteEntityVoList) {
            logicalDeleteVoList.add(
                JpaTestController.SelectUniqueTestTableRowsSampleOutputVo.TestEntityVo(
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
        return JpaTestController.SelectUniqueTestTableRowsSampleOutputVo(
            entityVoList,
            logicalDeleteVoList
        )
    }


    // ----
    // (유니크 테스트 테이블 Row 수정 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun updateUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: JpaTestController.UpdateUniqueTestTableRowSampleInputVo
    ): JpaTestController.UpdateUniqueTestTableRowSampleOutputVo? {
        val oldEntity =
            db1TemplateLogicalDeleteUniqueDataRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")

        if (oldEntity == null || oldEntity.rowDeleteDateStr != "/") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val uniqueValueEntity =
            db1TemplateLogicalDeleteUniqueDataRepository.findByUniqueValueAndRowDeleteDateStr(
                inputVo.uniqueValue,
                "/"
            )

        if (uniqueValueEntity != null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }


        oldEntity.uniqueValue = inputVo.uniqueValue

        val result = db1TemplateLogicalDeleteUniqueDataRepository.save(oldEntity)

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.UpdateUniqueTestTableRowSampleOutputVo(
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
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteUniqueTestTableRowSample(httpServletResponse: HttpServletResponse, index: Long) {
        val entity = db1TemplateLogicalDeleteUniqueDataRepository.findByUidAndRowDeleteDateStr(index, "/")

        if (entity == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        entity.rowDeleteDateStr =
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        db1TemplateLogicalDeleteUniqueDataRepository.save(entity)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (외래키 부모 테이블 Row 입력 API)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun insertFkParentRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: JpaTestController.InsertFkParentRowSampleInputVo
    ): JpaTestController.InsertFkParentRowSampleOutputVo? {
        val result = db1TemplateFkTestParentRepository.save(
            Db1_Template_FkTestParent(
                inputVo.fkParentName
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.InsertFkParentRowSampleOutputVo(
            result.uid!!,
            result.parentName,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    // ----
    // (외래키 부모 테이블 아래에 자식 테이블의 Row 입력 API)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun insertFkChildRowSample(
        httpServletResponse: HttpServletResponse,
        parentUid: Long,
        inputVo: JpaTestController.InsertFkChildRowSampleInputVo
    ): JpaTestController.InsertFkChildRowSampleOutputVo? {
        val parentEntityOpt = db1TemplateFkTestParentRepository.findById(parentUid)

        if (parentEntityOpt.isEmpty) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val parentEntity = parentEntityOpt.get()

        val result = db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                inputVo.fkChildName,
                parentEntity
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.InsertFkChildRowSampleOutputVo(
            result.uid!!,
            result.childName,
            result.fkTestParent.parentName,
            result.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    // ----
    // (외래키 관련 테이블 Rows 조회 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectFkTestTableRowsSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectFkTestTableRowsSampleOutputVo? {
        val resultEntityList =
            db1TemplateFkTestParentRepository.findAllByRowDeleteDateStrOrderByRowCreateDate("/")

        val entityVoList =
            ArrayList<JpaTestController.SelectFkTestTableRowsSampleOutputVo.ParentEntityVo>()
        for (resultEntity in resultEntityList) {
            val childEntityVoList: ArrayList<JpaTestController.SelectFkTestTableRowsSampleOutputVo.ParentEntityVo.ChildEntityVo> =
                arrayListOf()

            val childList =
                db1TemplateFkTestManyToOneChildRepository.findAllByFkTestParentAndRowDeleteDateStrOrderByRowCreateDate(
                    resultEntity,
                    "/"
                )

            for (childEntity in childList) {
                childEntityVoList.add(
                    JpaTestController.SelectFkTestTableRowsSampleOutputVo.ParentEntityVo.ChildEntityVo(
                        childEntity.uid!!,
                        childEntity.childName,
                        childEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                        childEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    )
                )
            }

            entityVoList.add(
                JpaTestController.SelectFkTestTableRowsSampleOutputVo.ParentEntityVo(
                    resultEntity.uid!!,
                    resultEntity.parentName,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    childEntityVoList
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectFkTestTableRowsSampleOutputVo(
            entityVoList
        )
    }


    // ----
    // (외래키 관련 테이블 Rows 조회 테스트(Native Join))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectFkTestTableRowsByNativeQuerySample(httpServletResponse: HttpServletResponse): JpaTestController.SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo? {
        val resultEntityList = db1NativeRepository.findAllFromTemplateFkTestManyToOneChildInnerJoinParentByNotDeleted()

        val entityVoList =
            ArrayList<JpaTestController.SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo.ChildEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                JpaTestController.SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo.ChildEntityVo(
                    resultEntity.childUid,
                    resultEntity.childName,
                    resultEntity.childCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.childUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.parentUid,
                    resultEntity.parentName
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo(
            entityVoList
        )
    }


    // ----
    // (Native Query 반환값 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun getNativeQueryReturnValueTest(
        httpServletResponse: HttpServletResponse,
        inputVal: Boolean
    ): JpaTestController.GetNativeQueryReturnValueTestOutputVo? {
        // boolean 값을 갖고오기 위한 테스트 테이블이 존재하지 않는다면 하나 생성하기
        val justBooleanEntity = db1TemplateJustBooleanTestRepository.findAll()
        if (justBooleanEntity.isEmpty()) {
            db1TemplateJustBooleanTestRepository.save(
                Db1_Template_JustBooleanTest(
                    true
                )
            )
        }

        val resultEntity = db1NativeRepository.multiCaseBooleanReturnTest(inputVal)

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.GetNativeQueryReturnValueTestOutputVo(
            // 쿼리문 내에서 True, False 로 반환하는 값은 Long 타입으로 받습니다.
            resultEntity.normalBoolValue == 1L,
            resultEntity.funcBoolValue == 1L,
            resultEntity.ifBoolValue == 1L,
            resultEntity.caseBoolValue == 1L,

            // 테이블 쿼리의 Boolean 값은 그대로 Boolean 타입으로 받습니다.
            resultEntity.tableColumnBoolValue
        )
    }


    // ----
    // (SQL Injection 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun sqlInjectionTest(
        httpServletResponse: HttpServletResponse,
        searchKeyword: String
    ): JpaTestController.SqlInjectionTestOutputVo? {
        // jpaRepository : Injection Safe
        val jpaRepositoryResultEntityList =
            db1TemplateTestsRepository.findAllByContentOrderByRowCreateDate(
                searchKeyword
            )

        val jpaRepositoryResultList: ArrayList<JpaTestController.SqlInjectionTestOutputVo.TestEntityVo> =
            arrayListOf()
        for (jpaRepositoryResultEntity in jpaRepositoryResultEntityList) {
            jpaRepositoryResultList.add(
                JpaTestController.SqlInjectionTestOutputVo.TestEntityVo(
                    jpaRepositoryResultEntity.uid!!,
                    jpaRepositoryResultEntity.content,
                    jpaRepositoryResultEntity.randomNum,
                    jpaRepositoryResultEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    jpaRepositoryResultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    jpaRepositoryResultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        // JPQL : Injection Safe
        val jpqlResultEntityList =
            db1TemplateTestsRepository.findAllByContentOrderByRowCreateDateJpql(
                searchKeyword
            )

        val jpqlResultList: ArrayList<JpaTestController.SqlInjectionTestOutputVo.TestEntityVo> =
            arrayListOf()
        for (jpqlEntity in jpqlResultEntityList) {
            jpqlResultList.add(
                JpaTestController.SqlInjectionTestOutputVo.TestEntityVo(
                    jpqlEntity.uid!!,
                    jpqlEntity.content,
                    jpqlEntity.randomNum,
                    jpqlEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    jpqlEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    jpqlEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        // NativeQuery : Injection Safe
        val nativeQueryResultEntityList =
            db1NativeRepository.findAllFromTemplateTestDataByContent(
                searchKeyword
            )

        val nativeQueryResultList: ArrayList<JpaTestController.SqlInjectionTestOutputVo.TestEntityVo> =
            arrayListOf()
        for (nativeQueryEntity in nativeQueryResultEntityList) {
            nativeQueryResultList.add(
                JpaTestController.SqlInjectionTestOutputVo.TestEntityVo(
                    nativeQueryEntity.uid,
                    nativeQueryEntity.content,
                    nativeQueryEntity.randomNum,
                    nativeQueryEntity.testDatetime.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    nativeQueryEntity.rowCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    nativeQueryEntity.rowUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        /*
            결론 : 위 세 방식은 모두 SQL Injection 공격에서 안전합니다.
                셋 모두 쿼리문에 직접 값을 입력하는 것이 아니며, 매개변수로 먼저 받아서 JPA 를 경유하여 입력되므로,
                라이브러리가 자동으로 인젝션 공격을 막아주게 됩니다.
         */

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SqlInjectionTestOutputVo(
            jpaRepositoryResultList,
            jpqlResultList,
            nativeQueryResultList
        )
    }


    // ----
    // (외래키 관련 테이블 Rows 조회 (네이티브 쿼리, 부모 테이블을 자식 테이블의 가장 최근 데이터만 Join))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectFkTableRowsWithLatestChildSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectFkTableRowsWithLatestChildSampleOutputVo? {
        val resultEntityList = db1NativeRepository.findAllFromTemplateFkTestParentWithNearestChildOnly()

        val entityVoList =
            ArrayList<JpaTestController.SelectFkTableRowsWithLatestChildSampleOutputVo.ParentEntityVo>()
        for (resultEntity in resultEntityList) {
            entityVoList.add(
                JpaTestController.SelectFkTableRowsWithLatestChildSampleOutputVo.ParentEntityVo(
                    resultEntity.parentUid,
                    resultEntity.parentName,
                    resultEntity.parentCreateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.parentUpdateDate.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    if (resultEntity.childUid == null) {
                        null
                    } else {
                        JpaTestController.SelectFkTableRowsWithLatestChildSampleOutputVo.ParentEntityVo.ChildEntityVo(
                            resultEntity.childUid!!,
                            resultEntity.childName!!,
                            resultEntity.childCreateDate!!.atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                            resultEntity.childUpdateDate!!.atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                        )
                    }
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectFkTableRowsWithLatestChildSampleOutputVo(
            entityVoList
        )
    }


    // ----
    // (외래키 관련 테이블 Rows 조회 (QueryDsl))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectFkTableRowsWithQueryDsl(httpServletResponse: HttpServletResponse): JpaTestController.SelectFkTableRowsWithQueryDslOutputVo? {
        val resultEntityList = db1TemplateRepositoryDsl.findParentWithChildren()

        val entityVoList =
            ArrayList<JpaTestController.SelectFkTableRowsWithQueryDslOutputVo.ParentEntityVo>()

        for (resultEntity in resultEntityList) {
            val childEntityVoList =
                ArrayList<JpaTestController.SelectFkTableRowsWithQueryDslOutputVo.ParentEntityVo.ChildEntityVo>()

            for (childEntity in resultEntity.fkTestManyToOneChildList) {
                childEntityVoList.add(
                    JpaTestController.SelectFkTableRowsWithQueryDslOutputVo.ParentEntityVo.ChildEntityVo(
                        childEntity.uid!!,
                        childEntity.childName,
                        childEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                        childEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    )
                )
            }

            entityVoList.add(
                JpaTestController.SelectFkTableRowsWithQueryDslOutputVo.ParentEntityVo(
                    resultEntity.uid!!,
                    resultEntity.parentName,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    childEntityVoList
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectFkTableRowsWithQueryDslOutputVo(
            entityVoList
        )
    }


    // ----
    // (외래키 관련 테이블 Rows 조회 및 부모 테이블 이름으로 필터링 (QueryDsl))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectFkTableRowsByParentNameFilterWithQueryDsl(
        httpServletResponse: HttpServletResponse,
        parentName: String
    ): JpaTestController.SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo? {
        val resultEntityList = db1TemplateRepositoryDsl.findParentWithChildrenByName(parentName)

        val entityVoList =
            ArrayList<JpaTestController.SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo.ParentEntityVo>()

        for (resultEntity in resultEntityList) {
            val childEntityVoList =
                ArrayList<JpaTestController.SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo.ParentEntityVo.ChildEntityVo>()

            for (childEntity in resultEntity.fkTestManyToOneChildList) {
                childEntityVoList.add(
                    JpaTestController.SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo.ParentEntityVo.ChildEntityVo(
                        childEntity.uid!!,
                        childEntity.childName,
                        childEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                        childEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    )
                )
            }

            entityVoList.add(
                JpaTestController.SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo.ParentEntityVo(
                    resultEntity.uid!!,
                    resultEntity.parentName,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    childEntityVoList
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo(
            entityVoList
        )
    }


    // ----
    // (외래키 관련 테이블 부모 테이블 고유번호로 자식 테이블 리스트 검색 (QueryDsl))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    fun selectFkTableChildListWithQueryDsl(
        httpServletResponse: HttpServletResponse,
        parentUid: Long
    ): JpaTestController.SelectFkTableChildListWithQueryDslOutputVo? {
        val resultEntityList = db1TemplateRepositoryDsl.findChildByParentId(parentUid)

        val entityVoList =
            ArrayList<JpaTestController.SelectFkTableChildListWithQueryDslOutputVo.ChildEntityVo>()

        for (resultEntity in resultEntityList) {
            entityVoList.add(
                JpaTestController.SelectFkTableChildListWithQueryDslOutputVo.ChildEntityVo(
                    resultEntity.uid!!,
                    resultEntity.childName,
                    resultEntity.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    resultEntity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.SelectFkTableChildListWithQueryDslOutputVo(
            entityVoList
        )
    }


    // ----
    // (외래키 자식 테이블 Row 삭제 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteFkChildRowSample(httpServletResponse: HttpServletResponse, index: Long) {
        val entityOpt = db1TemplateFkTestManyToOneChildRepository.findById(index)

        if (entityOpt.isEmpty) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        db1TemplateFkTestManyToOneChildRepository.deleteById(index)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (외래키 부모 테이블 Row 삭제 테스트 (Cascade 기능 확인))
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun deleteFkParentRowSample(httpServletResponse: HttpServletResponse, index: Long) {
        val entityOpt = db1TemplateFkTestParentRepository.findById(index)

        if (entityOpt.isEmpty) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        db1TemplateFkTestParentRepository.deleteById(index)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    // ----
    // (외래키 테이블 트랜젝션 동작 테스트)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun fkTableTransactionTest(
        httpServletResponse: HttpServletResponse
    ) {
        val parentEntity = db1TemplateFkTestParentRepository.save(
            Db1_Template_FkTestParent(
                "transaction test"
            )
        )

        db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                "transaction test1",
                parentEntity
            )
        )

        db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                "transaction test2",
                parentEntity
            )
        )

        throw RuntimeException("Transaction Rollback Test!")
    }


    // ----
    // (외래키 테이블 트랜젝션 비동작 테스트)
    fun fkTableNonTransactionTest(httpServletResponse: HttpServletResponse) {
        val parentEntity = db1TemplateFkTestParentRepository.save(
            Db1_Template_FkTestParent(
                "transaction test"
            )
        )

        db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                "transaction test1",
                parentEntity
            )
        )

        db1TemplateFkTestManyToOneChildRepository.save(
            Db1_Template_FkTestManyToOneChild(
                "transaction test2",
                parentEntity
            )
        )

        throw RuntimeException("No Transaction Exception Test!")
    }


    // ----
    // (ORM Datatype Mapping 테이블 Row 입력 테스트 API)
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    fun ormDatatypeMappingTest(
        httpServletResponse: HttpServletResponse,
        inputVo: JpaTestController.OrmDatatypeMappingTestInputVo
    ): JpaTestController.OrmDatatypeMappingTestOutputVo? {
        val sampleDateParts = inputVo.sampleDate.split("_")

        val result = db1TemplateDataTypeMappingTestRepository.save(
            Db1_Template_DataTypeMappingTest(
                inputVo.sampleTinyInt,
                inputVo.sampleTinyIntUnsigned,
                inputVo.sampleSmallInt,
                inputVo.sampleSmallIntUnsigned,
                inputVo.sampleMediumInt,
                inputVo.sampleMediumIntUnsigned,
                inputVo.sampleInt,
                inputVo.sampleIntUnsigned,
                inputVo.sampleBigInt,
                inputVo.sampleBigIntUnsigned,
                inputVo.sampleFloat,
                inputVo.sampleFloatUnsigned,
                inputVo.sampleDouble,
                inputVo.sampleDoubleUnsigned,
                inputVo.sampleDecimalP65S10,
                ZonedDateTime.parse(
                    "${sampleDateParts[0]}_${sampleDateParts[1]}_${sampleDateParts[2]}_T_00_00_00_000_${sampleDateParts[3]}",
                    DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
                ).withZoneSameInstant(ZoneId.systemDefault()).toLocalDate(),
                ZonedDateTime.parse(
                    inputVo.sampleDatetime,
                    DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
                ).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                LocalTime.parse(inputVo.sampleTime, DateTimeFormatter.ofPattern("HH_mm_ss_SSS")),
                ZonedDateTime.parse(
                    inputVo.sampleTimestamp,
                    DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")
                ).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return JpaTestController.OrmDatatypeMappingTestOutputVo(
            result.sampleTinyInt,
            result.sampleTinyIntUnsigned,
            result.sampleSmallInt,
            result.sampleSmallIntUnsigned,
            result.sampleMediumInt,
            result.sampleMediumIntUnsigned,
            result.sampleInt,
            result.sampleIntUnsigned,
            result.sampleBigInt,
            result.sampleBigIntUnsigned,
            result.sampleFloat,
            result.sampleFloatUnsigned,
            result.sampleDouble,
            result.sampleDoubleUnsigned,
            result.sampleDecimalP65S10,
            result.sampleDate.atStartOfDay().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_z")),
            result.sampleDateTime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            result.sampleTime.format(DateTimeFormatter.ofPattern("HH_mm_ss_SSS")),
            result.sampleTimestamp.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }
}