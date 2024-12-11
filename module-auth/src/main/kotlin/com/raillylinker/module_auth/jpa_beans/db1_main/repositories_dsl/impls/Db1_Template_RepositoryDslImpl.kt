package com.raillylinker.module_auth.jpa_beans.db1_main.repositories_dsl.impls

import com.querydsl.jpa.impl.JPAQueryFactory
import com.raillylinker.module_auth.jpa_beans.db1_main.repositories_dsl.Db1_Template_RepositoryDsl
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class Db1_Template_RepositoryDslImpl(entityManager: EntityManager) : Db1_Template_RepositoryDsl {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    // 부모 테이블과 자식 테이블을 조인하여 조회하는 예시
//    override fun findParentWithChildren(): List<Db1_Template_FkTestParent> {
//        return queryFactory
//            .selectFrom(db1_Template_FkTestParent)
//            .leftJoin(db1_Template_FkTestParent.fkTestManyToOneChildList, db1_Template_FkTestManyToOneChild)
//            .fetchJoin() // fetchJoin을 사용하여 자식 엔티티를 함께 가져옴
//            .fetch() // 결과를 가져옴
//    }
}