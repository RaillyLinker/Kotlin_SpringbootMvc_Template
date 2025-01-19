package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_RentableProductCategory_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductCategory, Long> {
    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductCategory?

    fun existsByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Boolean

    // ----
    // todo : 추후 QueryDSL 로 변환하기
    @Query(
        nativeQuery = true,
        value = """
            WITH RECURSIVE CategoryTree AS (
                SELECT 
                rentable_product_category.*, 
                0 AS depth
                FROM railly_linker_company.rentable_product_category as rentable_product_category
                WHERE 
                rentable_product_category.uid = :categoryUid and 
                rentable_product_category.row_delete_date_str = "/"
                UNION ALL
                SELECT 
                child.*, 
                parent.depth + 1 AS depth
                FROM railly_linker_company.rentable_product_category as child
                INNER JOIN 
                CategoryTree as parent 
                ON 
                child.parent_rentable_product_category_uid = parent.uid and 
                parent.row_delete_date_str = "/"
                WHERE 
                child.row_delete_date_str = "/"
            )
            SELECT 
            CategoryTree.uid
            FROM 
            CategoryTree
            ORDER BY 
            depth DESC
            """
    )
    fun findAllCategoryTreeUidList(
        @Param(value = "categoryUid") categoryUid: Long
    ): List<FindAllCategoryTreeUidListOutputVo>

    interface FindAllCategoryTreeUidListOutputVo {
        var uid: Long
    }
}