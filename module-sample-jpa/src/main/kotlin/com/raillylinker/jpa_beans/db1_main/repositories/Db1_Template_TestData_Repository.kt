package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_TestData
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime

@Repository
interface Db1_Template_TestData_Repository : JpaRepository<Db1_Template_TestData, Long> {
    fun findAllByRowDeleteDateStrOrderByRowCreateDate(
        rowDeleteDateStr: String,
        pageable: Pageable
    ): Page<Db1_Template_TestData>

    fun countByRowDeleteDateStr(
        rowDeleteDateStr: String
    ): Long

    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_Template_TestData?

    fun findAllByRowDeleteDateStrOrderByRowCreateDate(
        rowDeleteDateStr: String
    ): List<Db1_Template_TestData>

    fun findAllByRowDeleteDateStrNotOrderByRowCreateDate(
        rowDeleteDateStr: String
    ): List<Db1_Template_TestData>

    fun findAllByContentOrderByRowCreateDate(
        content: String
    ): List<Db1_Template_TestData>

    @Query(
        """
        SELECT 
        template_test_data 
        FROM 
        Db1_Template_TestData AS template_test_data 
        WHERE 
        template_test_data.content = :content 
        order by 
        template_test_data.rowCreateDate desc
    """
    )
    fun findAllByContentOrderByRowCreateDateJpql(
        @Param("content") content: String
    ): List<Db1_Template_TestData>


    // <C7>
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime, 
            ABS(test_data.random_num-:num) AS distance 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/' 
            ORDER BY 
            distance
            """
    )
    fun findAllFromTemplateTestDataByNotDeletedWithRandomNumDistance(
        @Param(value = "num") num: Int
    ): List<FindAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo>

    interface FindAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var distance: Int
    }


    // ----
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            ABS(TIMESTAMPDIFF(MICROSECOND, test_data.row_create_date, :date)) AS timeDiffMicroSec 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/' 
            ORDER BY 
            timeDiffMicroSec
            """
    )
    fun findAllFromTemplateTestDataByNotDeletedWithRowCreateDateDistance(
        @Param(value = "date") date: LocalDateTime
    ): List<FindAllFromTemplateTestDataByNotDeletedWithRowCreateDateDistanceOutputVo>

    interface FindAllFromTemplateTestDataByNotDeletedWithRowCreateDateDistanceOutputVo {
        var uid: Long
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var timeDiffMicroSec: Long
    }


    // ----
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime, 
            ABS(test_data.random_num-:num) AS distance 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/' 
            ORDER BY 
            distance
            """,
        countQuery = """
            SELECT 
            COUNT(*) 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/'
            """
    )
    fun findPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistance(
        @Param(value = "num") num: Int,
        pageable: Pageable
    ): Page<FindPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo>

    interface FindPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var distance: Int
    }


    // ----
    @Modifying // Native Query 에서 Delete, Update 문은 이것을 붙여야함
    @Query(
        nativeQuery = true,
        value = """
            UPDATE 
            template.test_data 
            SET 
            content = :content, 
            test_datetime = :testDatetime 
            WHERE 
            uid = :uid
            """
    )
    fun updateToTemplateTestDataSetContentAndTestDateTimeByUid(
        @Param(value = "uid") uid: Long,
        @Param(value = "content") content: String,
        @Param(value = "testDatetime") testDatetime: LocalDateTime
    )


    // ----
    // like 문을 사용할 때, replace 로 검색어와 탐색 정보의 공백을 없애줌으로써 공백에 유연한 검색이 가능
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime 
            FROM 
            template.test_data AS test_data 
            WHERE 
            REPLACE(test_data.content, ' ', '') LIKE REPLACE(CONCAT('%',:searchKeyword,'%'), ' ', '') AND 
            test_data.row_delete_date_str = '/' 
            ORDER BY 
            test_data.row_create_date DESC
            """,
        countQuery = """
            SELECT 
            COUNT(*) 
            FROM 
            template.test_data AS test_data 
            WHERE 
            REPLACE(test_data.content, ' ', '') LIKE REPLACE(CONCAT('%',:searchKeyword,'%'), ' ', '') AND 
            test_data.row_delete_date_str = '/'
            """
    )
    fun findPageAllFromTemplateTestDataBySearchKeyword(
        @Param(value = "searchKeyword") searchKeyword: String,
        pageable: Pageable
    ): Page<FindPageAllFromTemplateTestDataBySearchKeywordOutputVo>

    interface FindPageAllFromTemplateTestDataBySearchKeywordOutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
    }


    // ----
    /*
        [중복 없는 페이징 구현 쿼리 설명]
        - 아래 방법은, 논리적 삭제를 사용하는 테이블에만 사용이 가능하며,
            행 자체가 삭제되어 행 번호가 변경될 가능성이 존재하는 테이블에는 사용할 수 없습니다.

        - FROM 절에 서브쿼리를 사용하는 복잡한 쿼리입니다.
            데이터베이스 종류에 따라 오동작을 할 가능성이 있으며,
            성능 문제를 야기할 수 있습니다.

        (From 외곽 부분)
        1. 먼저 가져올 정보를 인터페이스에 맞게 가져옵니다.
            주의할 점은, where 문을 절대 사용하지 않으며, order by 로 순서만 정렬해야 합니다.
            그리고, 여기서 순서를 정하므로, 바깥 부분에서는 ORDER BY 를 사용하면 안됩니다.
            이것이 orderedCoreTable 입니다. (순서 정렬)

        2. orderedCoreTable 에 rowNum 을 붙여줍니다.
            이것이 rowNumTable 입니다. (행 번호 붙이기)

        - 여기까지 하여, 정렬 기준에 따라 행이 붙여진 rowNumTable 가 생성됩니다.
            이곳에 여러 필터링을 하면 되는데,
            먼저 중복 방지 기준이 되는 아이템(클라이언트에서 받은 마지막 아이템)의 고유값을 사용하여 그 행번호를 찾아야 합니다.

        3. rowNumTable 에서 where 문을 사용하여, 원하는 uid 의 rowNum 을 찾고,
            이보다 큰 rowNum 을 추려냅니다. (앵커의 행 번호 검색 후, 그것을 포함하여 앞의 결과를 제거)

        4. 3번을 했다면, 이제부터 where 문과 limit 을 사용하여 결과를 마음껏 필터링 하면 됩니다. (본격적인 필터링)
     */
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            * 
            FROM 
            (
                SELECT 
                *, 
                @rownum \:= @rownum + 1 AS rowNum 
                FROM 
                (
                    SELECT 
                    test_data.uid AS uid, 
                    test_data.row_create_date AS rowCreateDate, 
                    test_data.row_update_date AS rowUpdateDate, 
                    test_data.content as content, 
                    test_data.random_num AS randomNum, 
                    test_data.test_datetime AS testDatetime, 
                    test_data.row_delete_date_str AS rowDeleteDateStr 
                    FROM 
                    template.test_data AS test_data 
                    ORDER BY 
                    test_data.row_create_date DESC
                ) AS orderedCoreTable,
                (SELECT @rownum \:= 0) AS rowNumStart
            ) AS rowNumTable 
            WHERE 
            (
                :lastItemUid IS NULL OR 
                rowNumTable.rowNum > 
                (
                    SELECT 
                    rowNumCopy 
                    FROM 
                    (
                        SELECT 
                        *, 
                        @rownumCopy \:= @rownumCopy + 1 AS rowNumCopy 
                        FROM 
                        (
                            SELECT 
                            test_data.uid AS uid, 
                            test_data.row_create_date AS rowCreateDate, 
                            test_data.row_update_date AS rowUpdateDate, 
                            test_data.content as content, 
                            test_data.random_num AS randomNum, 
                            test_data.test_datetime AS testDatetime, 
                            test_data.row_delete_date_str AS rowDeleteDateStr 
                            FROM 
                            template.test_data AS test_data 
                            ORDER BY 
                            test_data.row_create_date DESC
                        ) AS orderedCoreTableCopy, 
                        (SELECT @rownumCopy \:= 0) AS rowNumStartCopy
                    ) AS rowNumTableCopy 
                    WHERE 
                    uid = :lastItemUid
                )
            ) AND 
            rowNumTable.rowDeleteDateStr = '/' 
            LIMIT :pageElementsCount
            """
    )
    fun findAllFromTemplateTestDataForNoDuplicatedPaging(
        @Param(value = "lastItemUid") lastItemUid: Long?,
        @Param(value = "pageElementsCount") pageElementsCount: Int
    ): List<FindAllFromTemplateTestDataForNoDuplicatedPagingOutputVo>

    interface FindAllFromTemplateTestDataForNoDuplicatedPagingOutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var rowDeleteDateStr: String
    }


    // ----
    /*
        forC7N14 에 대해서 전체 카운팅 쿼리를 따로 만들어야 합니다.
        forC7N14 에서 From 문에서 사용하는 서브 쿼리를 제거하고,
        lastItemUid 비교 부분을 제거하며,
        LIMIT 을 제거하면 됩니다.
     */
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            COUNT(*) 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/'
            """
    )
    fun countFromTemplateTestDataByNotDeleted(): Long


    // ----
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.row_delete_date_str = '/' AND 
            test_data.uid = :testTableUid
            """
    )
    fun findFromTemplateTestDataByNotDeletedAndUid(
        @Param(value = "testTableUid") testTableUid: Long
    ): FindFromTemplateTestDataByNotDeletedAndUidOutputVo?

    interface FindFromTemplateTestDataByNotDeletedAndUidOutputVo {
        var uid: Long
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
    }


    // ----
    @Query(
        nativeQuery = true,
        value = """
            SELECT 
            test_data.uid AS uid, 
            test_data.row_create_date AS rowCreateDate, 
            test_data.row_update_date AS rowUpdateDate, 
            test_data.content AS content, 
            test_data.random_num AS randomNum, 
            test_data.test_datetime AS testDatetime 
            FROM 
            template.test_data AS test_data 
            WHERE 
            test_data.content = :content 
            ORDER BY 
            test_data.row_create_date DESC
            """
    )
    fun findAllFromTemplateTestDataByContent(
        @Param(value = "content") content: String
    ): List<FindAllFromTemplateTestDataByContentOutputVo>

    interface FindAllFromTemplateTestDataByContentOutputVo {
        var uid: Long
        var rowCreateDate: LocalDateTime
        var rowUpdateDate: LocalDateTime
        var content: String
        var randomNum: Int
        var testDatetime: LocalDateTime
    }
}