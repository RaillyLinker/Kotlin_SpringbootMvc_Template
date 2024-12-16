package com.raillylinker.sample_mongodb.services.impls

import com.raillylinker.sample_mongodb.controllers.MongoDbTestController
import com.raillylinker.sample_mongodb.services.MongoDbTestService
import com.raillylinker.sample_mongodb.configurations.mongodb_configs.Mdb1MainConfig
import com.raillylinker.sample_mongodb.mongodb_beans.mdb1_main.documents.Mdb1_Test
import com.raillylinker.sample_mongodb.mongodb_beans.mdb1_main.repositories.Mdb1_Test_Repository
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class MongoDbTestServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,
    private val mdb1TestRepository: Mdb1_Test_Repository
) : MongoDbTestService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME)
    override fun insertDocumentTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MongoDbTestController.InsertDocumentTestInputVo
    ): MongoDbTestController.InsertDocumentTestOutputVo? {
        val resultCollection = mdb1TestRepository.save(
            Mdb1_Test(
                inputVo.content,
                (0..99999999).random(),
                inputVo.nullableValue,
                true
            )
        )

        httpServletResponse.setHeader("api-result-code", "")
        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.InsertDocumentTestOutputVo(
            resultCollection.uid!!.toString(),
            resultCollection.content,
            resultCollection.nullableValue,
            resultCollection.randomNum,
            resultCollection.rowCreateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
            resultCollection.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    override fun deleteAllDocumentTest(httpServletResponse: HttpServletResponse) {
        mdb1TestRepository.deleteAll()

        httpServletResponse.setHeader("api-result-code", "")
        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    override fun deleteDocumentTest(httpServletResponse: HttpServletResponse, id: String) {
        val testDocument = mdb1TestRepository.findById(id)

        if (testDocument.isEmpty) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        mdb1TestRepository.deleteById(id)

        httpServletResponse.setHeader("api-result-code", "")
        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME, readOnly = true) // ReplicaSet 환경이 아니면 에러가 납니다.
    override fun selectAllDocumentsTest(httpServletResponse: HttpServletResponse): MongoDbTestController.SelectAllDocumentsTestOutputVo? {
        val testCollectionList = mdb1TestRepository.findAll()

        val resultVoList: ArrayList<MongoDbTestController.SelectAllDocumentsTestOutputVo.TestEntityVo> =
            arrayListOf()

        for (testCollection in testCollectionList) {
            resultVoList.add(
                MongoDbTestController.SelectAllDocumentsTestOutputVo.TestEntityVo(
                    testCollection.uid!!.toString(),
                    testCollection.content,
                    testCollection.nullableValue,
                    testCollection.randomNum,
                    testCollection.rowCreateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                    testCollection.rowUpdateDate!!.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                )
            )
        }

        httpServletResponse.setHeader("api-result-code", "")
        httpServletResponse.status = HttpStatus.OK.value()
        return MongoDbTestController.SelectAllDocumentsTestOutputVo(
            resultVoList
        )
    }


    ////
    @Transactional(transactionManager = Mdb1MainConfig.TRANSACTION_NAME) // ReplicaSet 환경이 아니면 에러가 납니다.
    override fun transactionRollbackTest(
        httpServletResponse: HttpServletResponse
    ) {
        mdb1TestRepository.save(
            Mdb1_Test(
                "test",
                (0..99999999).random(),
                null,
                true
            )
        )

        throw RuntimeException("Transaction Rollback Test!")

        httpServletResponse.setHeader("api-result-code", "")
        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun noTransactionRollbackTest(
        httpServletResponse: HttpServletResponse
    ) {
        mdb1TestRepository.save(
            Mdb1_Test(
                "test",
                (0..99999999).random(),
                null,
                true
            )
        )

        throw RuntimeException("No Transaction Exception Test!")

        httpServletResponse.setHeader("api-result-code", "")
        httpServletResponse.status = HttpStatus.OK.value()
    }
}