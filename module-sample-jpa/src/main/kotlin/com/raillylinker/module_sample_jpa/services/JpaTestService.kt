package com.raillylinker.module_sample_jpa.services

import com.raillylinker.module_sample_jpa.controllers.JpaTestController
import jakarta.servlet.http.HttpServletResponse

interface JpaTestService {
    // (DB Row 입력 테스트 API)
    fun insertDataSample(
        httpServletResponse: HttpServletResponse,
        inputVo: JpaTestController.InsertDataSampleInputVo
    ): JpaTestController.InsertDataSampleOutputVo?


    ////
    // (DB Rows 삭제 테스트 API)
    fun deleteRowsSample(httpServletResponse: HttpServletResponse, deleteLogically: Boolean)


    ////
    // (DB Row 삭제 테스트)
    fun deleteRowSample(httpServletResponse: HttpServletResponse, index: Long, deleteLogically: Boolean)


    ////
    // (DB Rows 조회 테스트)
    fun selectRowsSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectRowsSampleOutputVo?


    ////
    // (DB 테이블의 random_num 컬럼 근사치 기준으로 정렬한 리스트 조회 API)
    fun selectRowsOrderByRandomNumSample(
        httpServletResponse: HttpServletResponse,
        num: Int
    ): JpaTestController.SelectRowsOrderByRandomNumSampleOutputVo?


    ////
    // (DB 테이블의 row_create_date 컬럼 근사치 기준으로 정렬한 리스트 조회 API)
    fun selectRowsOrderByRowCreateDateSample(
        httpServletResponse: HttpServletResponse,
        dateString: String
    ): JpaTestController.SelectRowsOrderByRowCreateDateSampleOutputVo?


    ////
    // (DB Rows 조회 테스트 (페이징))
    fun selectRowsPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int
    ): JpaTestController.SelectRowsPageSampleOutputVo?


    ////
    // (DB Rows 조회 테스트 (네이티브 쿼리 페이징))
    fun selectRowsNativeQueryPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        num: Int
    ): JpaTestController.SelectRowsNativeQueryPageSampleOutputVo?


    ////
    // (DB Row 수정 테스트)
    fun updateRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: JpaTestController.UpdateRowSampleInputVo
    ): JpaTestController.UpdateRowSampleOutputVo?


    ////
    // (DB Row 수정 테스트 (네이티브 쿼리))
    fun updateRowNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: JpaTestController.UpdateRowNativeQuerySampleInputVo
    )


    ////
    // (DB 정보 검색 테스트)
    fun selectRowWhereSearchingKeywordSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        searchKeyword: String
    ): JpaTestController.SelectRowWhereSearchingKeywordSampleOutputVo?


    ////
    // (트랜젝션 동작 테스트)
    fun transactionTest(
        httpServletResponse: HttpServletResponse
    )


    ////
    // (트랜젝션 비동작 테스트)
    fun nonTransactionTest(httpServletResponse: HttpServletResponse)


    ////
    // (트랜젝션 비동작 테스트(try-catch))
    fun tryCatchNonTransactionTest(httpServletResponse: HttpServletResponse)


    ////
    // (DB Rows 조회 테스트 (중복 없는 네이티브 쿼리 페이징))
    fun selectRowsNoDuplicatePagingSample(
        httpServletResponse: HttpServletResponse,
        lastItemUid: Long?,
        pageElementsCount: Int
    ): JpaTestController.SelectRowsNoDuplicatePagingSampleOutputVo?


    ////
    // (DB Rows 조회 테스트 (카운팅))
    fun selectRowsCountSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectRowsCountSampleOutputVo?


    ////
    // (DB Rows 조회 테스트 (네이티브 카운팅))
    fun selectRowsCountByNativeQuerySample(httpServletResponse: HttpServletResponse): JpaTestController.SelectRowsCountByNativeQuerySampleOutputVo?


    ////
    // (DB Row 조회 테스트 (네이티브))
    fun selectRowByNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long
    ): JpaTestController.SelectRowByNativeQuerySampleOutputVo?


    ////
    // (유니크 테스트 테이블 Row 입력 API)
    fun insertUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: JpaTestController.InsertUniqueTestTableRowSampleInputVo
    ): JpaTestController.InsertUniqueTestTableRowSampleOutputVo?


    ////
    // (유니크 테스트 테이블 Rows 조회 테스트)
    fun selectUniqueTestTableRowsSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectUniqueTestTableRowsSampleOutputVo?


    ////
    // (유니크 테스트 테이블 Row 수정 테스트)
    fun updateUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: JpaTestController.UpdateUniqueTestTableRowSampleInputVo
    ): JpaTestController.UpdateUniqueTestTableRowSampleOutputVo?


    ////
    // (유니크 테스트 테이블 Row 삭제 테스트)
    fun deleteUniqueTestTableRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    // (외래키 부모 테이블 Row 입력 API)
    fun insertFkParentRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: JpaTestController.InsertFkParentRowSampleInputVo
    ): JpaTestController.InsertFkParentRowSampleOutputVo?


    ////
    // (외래키 부모 테이블 아래에 자식 테이블의 Row 입력 API)
    fun insertFkChildRowSample(
        httpServletResponse: HttpServletResponse,
        parentUid: Long,
        inputVo: JpaTestController.InsertFkChildRowSampleInputVo
    ): JpaTestController.InsertFkChildRowSampleOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 테스트)
    fun selectFkTestTableRowsSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectFkTestTableRowsSampleOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 테스트(Native Join))
    fun selectFkTestTableRowsByNativeQuerySample(httpServletResponse: HttpServletResponse): JpaTestController.SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo?


    ////
    // (Native Query 반환값 테스트)
    fun getNativeQueryReturnValueTest(
        httpServletResponse: HttpServletResponse,
        inputVal: Boolean
    ): JpaTestController.GetNativeQueryReturnValueTestOutputVo?


    ////
    // (SQL Injection 테스트)
    fun sqlInjectionTest(
        httpServletResponse: HttpServletResponse,
        searchKeyword: String
    ): JpaTestController.SqlInjectionTestOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 (네이티브 쿼리, 부모 테이블을 자식 테이블의 가장 최근 데이터만 Join))
    fun selectFkTableRowsWithLatestChildSample(httpServletResponse: HttpServletResponse): JpaTestController.SelectFkTableRowsWithLatestChildSampleOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 (QueryDsl))
    fun selectFkTableRowsWithQueryDsl(httpServletResponse: HttpServletResponse): JpaTestController.SelectFkTableRowsWithQueryDslOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 및 부모 테이블 이름으로 필터링 (QueryDsl))
    fun selectFkTableRowsByParentNameFilterWithQueryDsl(
        httpServletResponse: HttpServletResponse,
        parentName: String
    ): JpaTestController.SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo?


    ////
    // (외래키 관련 테이블 부모 테이블 고유번호로 자식 테이블 리스트 검색 (QueryDsl))
    fun selectFkTableChildListWithQueryDsl(
        httpServletResponse: HttpServletResponse,
        parentUid: Long
    ): JpaTestController.SelectFkTableChildListWithQueryDslOutputVo?


    ////
    // (외래키 자식 테이블 Row 삭제 테스트)
    fun deleteFkChildRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    // (외래키 부모 테이블 Row 삭제 테스트 (Cascade 기능 확인))
    fun deleteFkParentRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    // (외래키 테이블 트랜젝션 동작 테스트)
    fun fkTableTransactionTest(
        httpServletResponse: HttpServletResponse
    )


    ////
    // (외래키 테이블 트랜젝션 비동작 테스트)
    fun fkTableNonTransactionTest(httpServletResponse: HttpServletResponse)
}