package com.raillylinker.module_sample_quartz.jpa_beans.db1_main.repositories_dsl.impls

import com.querydsl.jpa.impl.JPAQueryFactory
import com.raillylinker.module_sample_quartz.jpa_beans.db1_main.repositories_dsl.Db1_Template_RepositoryDsl
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class Db1_Template_RepositoryDslImpl(entityManager: EntityManager) : Db1_Template_RepositoryDsl {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)
}