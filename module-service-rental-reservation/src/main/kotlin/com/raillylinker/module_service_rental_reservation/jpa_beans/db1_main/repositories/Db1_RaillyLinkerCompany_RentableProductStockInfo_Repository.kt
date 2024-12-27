package com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories

import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockCategory
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_RentableProductStockInfo_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductStockInfo, Long> {
    fun findAllByRentableProductStockCategoryAndRowDeleteDateStr(
        rentableProductStockCategory: Db1_RaillyLinkerCompany_RentableProductStockCategory?,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_RentableProductStockInfo>
}