package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_RentableProductStockCategory_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductStockCategory, Long> {
    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductStockCategory?

    fun existsByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Boolean


    // ----
    @Query(
        nativeQuery = true,
        value = """
            WITH RECURSIVE CategoryTree AS (
                SELECT 
                rentable_product_stock_category.*, 
                0 AS depth
                FROM railly_linker_company.rentable_product_stock_category as rentable_product_stock_category
                WHERE 
                rentable_product_stock_category.uid = :stockCategoryUid and 
                rentable_product_stock_category.row_delete_date_str = "/"
                UNION ALL
                SELECT 
                child.*, 
                parent.depth + 1 AS depth
                FROM railly_linker_company.rentable_product_stock_category as child
                INNER JOIN 
                CategoryTree as parent 
                ON 
                child.parent_rentable_product_stock_category_uid = parent.uid and 
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
    fun findAllStockCategoryTreeUidList(
        @Param(value = "stockCategoryUid") stockCategoryUid: Long
    ): List<FindAllStockCategoryTreeUidListOutputVo>

    interface FindAllStockCategoryTreeUidListOutputVo {
        var uid: Long
    }
}