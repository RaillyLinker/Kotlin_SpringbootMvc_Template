package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_FkTestManyToOneChild
import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_FkTestParent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface Db1_Template_FkTestManyToOneChild_Repository :
    JpaRepository<Db1_Template_FkTestManyToOneChild, Long> {
    // 외래키 변수로 검색시, 테이블 컬럼명이 아닌 클래스 변수명을 기준으로 하며, 데이터 타입도 부모 테이블의 클래스 타입을 선언해야합니다.
    fun findAllByFkTestParentAndRowDeleteDateStrOrderByRowCreateDate(
        fkTestParent: Db1_Template_FkTestParent,
        rowDeleteDateStr: String
    ): List<Db1_Template_FkTestManyToOneChild>


    // ----
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            fk_test_many_to_one_child.uid AS childUid, 
            fk_test_many_to_one_child.child_name AS childName, 
            fk_test_many_to_one_child.row_create_date AS childCreateDate, 
            fk_test_many_to_one_child.row_update_date AS childUpdateDate, 
            fk_test_parent.uid AS parentUid, 
            fk_test_parent.parent_name AS parentName 
            FROM 
            template.fk_test_many_to_one_child AS fk_test_many_to_one_child 
            INNER JOIN 
            template.fk_test_parent AS fk_test_parent 
            ON 
            fk_test_parent.uid = fk_test_many_to_one_child.fk_test_parent_uid AND 
            fk_test_parent.row_delete_date_str = '/' 
            WHERE 
            fk_test_many_to_one_child.row_delete_date_str = '/' 
            """
    )
    fun findAllFromTemplateFkTestManyToOneChildInnerJoinParentByNotDeleted(): List<FindAllFromTemplateFkTestManyToOneChildInnerJoinParentByNotDeletedOutputVo>

    interface FindAllFromTemplateFkTestManyToOneChildInnerJoinParentByNotDeletedOutputVo {
        var childUid: Long
        var childName: String
        var childCreateDate: LocalDateTime
        var childUpdateDate: LocalDateTime
        var parentUid: Long
        var parentName: String
    }
}