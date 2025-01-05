package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductCategory
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_RentableProductInfo_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductInfo, Long> {
    fun findAllByRentableProductCategoryAndRowDeleteDateStr(
        rentableProductCategory: Db1_RaillyLinkerCompany_RentableProductCategory?,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_RentableProductInfo>

    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductInfo?
}