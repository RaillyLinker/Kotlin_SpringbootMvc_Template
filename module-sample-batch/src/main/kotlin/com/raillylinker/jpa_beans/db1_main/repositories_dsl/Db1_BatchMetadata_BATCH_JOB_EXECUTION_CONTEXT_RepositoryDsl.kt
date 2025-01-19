package com.raillylinker.jpa_beans.db1_main.repositories_dsl

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class Db1_BatchMetadata_BATCH_JOB_EXECUTION_CONTEXT_RepositoryDsl(entityManager: EntityManager) {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)
}