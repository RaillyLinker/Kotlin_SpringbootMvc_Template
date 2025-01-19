package com.raillylinker.jpa_beans.db1_main.repositories_dsl

import com.querydsl.jpa.impl.JPAQueryFactory
import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_FkTestManyToOneChild
import com.raillylinker.jpa_beans.db1_main.entities.QDb1_Template_FkTestManyToOneChild.db1_Template_FkTestManyToOneChild
import com.raillylinker.jpa_beans.db1_main.entities.QDb1_Template_FkTestParent.db1_Template_FkTestParent
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class Db1_Template_FkTestManyToOneChild_RepositoryDsl(entityManager: EntityManager) {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    // (부모-자식 테이블의 특정 자식 데이터 조회)
    fun findChildByParentId(parentId: Long): List<Db1_Template_FkTestManyToOneChild> {
        return queryFactory
            .selectFrom(db1_Template_FkTestManyToOneChild)
            .leftJoin(db1_Template_FkTestManyToOneChild.fkTestParent, db1_Template_FkTestParent)
            .where(db1_Template_FkTestParent.uid.eq(parentId))
            .fetch()
    }
}