package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockCategory
import org.springframework.data.jpa.repository.JpaRepository
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
}