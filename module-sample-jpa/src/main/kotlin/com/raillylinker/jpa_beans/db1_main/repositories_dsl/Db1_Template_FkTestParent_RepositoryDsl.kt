package com.raillylinker.jpa_beans.db1_main.repositories_dsl

import com.querydsl.jpa.impl.JPAQueryFactory
import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_FkTestParent
import com.raillylinker.jpa_beans.db1_main.entities.QDb1_Template_FkTestManyToOneChild.db1_Template_FkTestManyToOneChild
import com.raillylinker.jpa_beans.db1_main.entities.QDb1_Template_FkTestParent.db1_Template_FkTestParent
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class Db1_Template_FkTestParent_RepositoryDsl(entityManager: EntityManager) {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    // (부모 테이블과 자식 테이블을 조인하여 조회하는 예시)
    fun findParentWithChildren(): List<Db1_Template_FkTestParent> {
        return queryFactory
            .selectFrom(db1_Template_FkTestParent)
            .leftJoin(db1_Template_FkTestParent.fkTestManyToOneChildList, db1_Template_FkTestManyToOneChild)
            .fetchJoin() // fetchJoin을 사용하여 자식 엔티티를 함께 가져옴
            .fetch() // 결과를 가져옴
    }

    // (특정 조건으로 부모-자식 조회 (예: 부모 이름으로 필터링))
    fun findParentWithChildrenByName(parentName: String): List<Db1_Template_FkTestParent> {
        return queryFactory
            .selectFrom(db1_Template_FkTestParent)
            .leftJoin(db1_Template_FkTestParent.fkTestManyToOneChildList, db1_Template_FkTestManyToOneChild)
            .fetchJoin()
            .where(db1_Template_FkTestParent.parentName.like("%${parentName.replace(" ", "")}%"))
            .fetch()
    }
}