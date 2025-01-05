package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_BatchMetadata_BATCH_JOB_EXECUTION_SEQ
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// (JPA 레포지토리)
// : 함수 작성 명명법에 따라 데이터베이스 SQL 동작을 자동지원
@Repository
interface Db1_BatchMetadata_BATCH_JOB_EXECUTION_SEQ_Repository :
    JpaRepository<Db1_BatchMetadata_BATCH_JOB_EXECUTION_SEQ, Long> {
}