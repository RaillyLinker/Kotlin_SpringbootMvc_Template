package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_FkTestParent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface Db1_Template_FkTestParent_Repository : JpaRepository<Db1_Template_FkTestParent, Long> {
    fun findAllByRowDeleteDateStrOrderByRowCreateDate(
        rowDeleteDateStr: String
    ): List<Db1_Template_FkTestParent>


    // ----
    /*
        [가장 최근의 자식 테이블만 Join 쿼리 설명]
        1. 가장 외곽의 From 부분은 부모 테이블을 이용하여 정상적으로 필터링을 하면 됩니다.

        2. 자식 테이블을 Join 하는 부분은 Inner 이든 Left 든 상관 없습니다.
            가장 외곽의 Join 문의 결과는 가장 최근에 등록한 자식 테이블의 결과값이 될 것입니다.
            즉, Join 문 내부에서 그러한 처리를 마친 단 하나의 자식 테이블 결과만이 반환될 것이기에,
            ON 문에서는 외래키 일치 여부만 확인하면 됩니다.

        3. 외곽 Join 문 안을 봅시다.
            여기서는 가장 최근의 데이터 1개를 가져와야 합니다.
            서브 쿼리에서 자식 테이블을 Select 하고, 동일한 자식 테이블에서 외래키 별로 그룹지어서 그중에서 가장 최신의 데이터를 구한 후,
            동일 외래키에서 가장 최근인 데이터를 Inner Join 하여 가져오면 됩니다.
            여기서, row_delete_date_stf 필터링을 하는 것은, 지워진 데이터는 제외하고 가장 최근의 데이터를 가져온다는 것으로,
            논리적 삭제가 아니라 실제 삭제를 생각해본다면 이것을 제외하는 것이 이해가 될 것입니다.

        - 주의사항 :
            JOIN 절에 서브쿼리를 사용하는 복잡한 쿼리입니다.
            데이터베이스 종류에 따라 오동작을 할 가능성이 있으며,
            성능 문제를 야기할 수 있으므로, 동작 테스트, 부하 테스트를 거친 후 사용을 고려하세요.

            가장 좋은 것은, 아래와 같이 서브쿼리를 사용하는 것이 아니라,
            fk_test_many_to_one_child 가 등록될 때마다 이를 데이터베이스 안에 저장 / 갱신 하도록 하여,
            이 데이터를 사용하는 것이 가장 깔끔합니다. (코드와 쿼리문은 깔끔하고 단순할수록 좋다고 강조드립니다.)
     */
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            fk_test_parent.uid AS parentUid, 
            fk_test_parent.parent_name AS parentName, 
            fk_test_parent.row_create_date AS parentCreateDate, 
            fk_test_parent.row_update_date AS parentUpdateDate, 
            fk_test_many_to_one_child.uid AS childUid, 
            fk_test_many_to_one_child.child_name AS childName, 
            fk_test_many_to_one_child.row_create_date AS childCreateDate, 
            fk_test_many_to_one_child.row_update_date AS childUpdateDate 
            FROM 
            template.fk_test_parent AS fk_test_parent 
            LEFT JOIN 
            (
                SELECT 
                fk_test_many_to_one_child1.* 
                FROM 
                template.fk_test_many_to_one_child AS fk_test_many_to_one_child1 
                INNER JOIN 
                (
                    SELECT 
                    MAX(fk_test_many_to_one_child2.row_create_date) AS max_row_create_date, 
                    fk_test_many_to_one_child2.fk_test_parent_uid, 
                    fk_test_many_to_one_child2.row_delete_date_str 
                    FROM 
                    template.fk_test_many_to_one_child AS fk_test_many_to_one_child2 
                    WHERE 
                    fk_test_many_to_one_child2.row_delete_date_str = '/' 
                    GROUP BY 
                    fk_test_many_to_one_child2.fk_test_parent_uid
                ) AS latest_fk_test_many_to_one_child 
                ON 
                latest_fk_test_many_to_one_child.fk_test_parent_uid = fk_test_many_to_one_child1.fk_test_parent_uid AND 
                latest_fk_test_many_to_one_child.max_row_create_date = fk_test_many_to_one_child1.row_create_date AND 
                latest_fk_test_many_to_one_child.row_delete_date_str = '/' 
                WHERE 
                fk_test_many_to_one_child1.row_delete_date_str = '/'
            ) AS fk_test_many_to_one_child 
            ON 
            fk_test_many_to_one_child.fk_test_parent_uid = fk_test_parent.uid AND 
            fk_test_many_to_one_child.row_delete_date_str = '/' 
            WHERE 
            fk_test_parent.row_delete_date_str = '/'
            """
    )
    fun findAllFromTemplateFkTestParentWithNearestChildOnly(): List<FindAllFromTemplateFkTestParentWithNearestChildOnlyOutputVo>

    interface FindAllFromTemplateFkTestParentWithNearestChildOnlyOutputVo {
        var parentUid: Long
        var parentName: String
        var parentCreateDate: LocalDateTime
        var parentUpdateDate: LocalDateTime

        var childUid: Long?
        var childName: String?
        var childCreateDate: LocalDateTime?
        var childUpdateDate: LocalDateTime?
    }
}