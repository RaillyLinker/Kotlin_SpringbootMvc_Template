package com.raillylinker.jpa_beans.db1_main.repositories_dsl

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class Db1_QuartzMetadata_QRTZ_BLOB_TRIGGERS_RepositoryDsl(entityManager: EntityManager) {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)
}