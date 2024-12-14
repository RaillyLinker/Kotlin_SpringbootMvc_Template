package com.raillylinker.sample_mongodb.services

import com.raillylinker.sample_mongodb.controllers.MongoDbTestController
import jakarta.servlet.http.HttpServletResponse

interface MongoDbTestService {
    // (DB document 입력 테스트 API)
    fun insertDocumentTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MongoDbTestController.InsertDocumentTestInputVo
    ): MongoDbTestController.InsertDocumentTestOutputVo?


    ////
    // (DB Rows 삭제 테스트 API)
    fun deleteAllDocumentTest(httpServletResponse: HttpServletResponse)


    ////
    // (DB Row 삭제 테스트)
    fun deleteDocumentTest(httpServletResponse: HttpServletResponse, id: String)


    ////
    // (DB Rows 조회 테스트)
    fun selectAllDocumentsTest(httpServletResponse: HttpServletResponse): MongoDbTestController.SelectAllDocumentsTestOutputVo?


    ////
    // (트랜젝션 동작 테스트)
    fun transactionRollbackTest(
        httpServletResponse: HttpServletResponse
    )


    ////
    // (트랜젝션 비동작 테스트)
    fun noTransactionRollbackTest(
        httpServletResponse: HttpServletResponse
    )
}