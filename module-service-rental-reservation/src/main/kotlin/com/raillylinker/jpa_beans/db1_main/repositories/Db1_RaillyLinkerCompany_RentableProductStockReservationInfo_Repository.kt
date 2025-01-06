package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockInfo
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockReservationInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface Db1_RaillyLinkerCompany_RentableProductStockReservationInfo_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductStockReservationInfo, Long> {
    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductStockReservationInfo?

    fun findAllByRentableProductStockInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
        rentableProductStockInfo: Db1_RaillyLinkerCompany_RentableProductStockInfo,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_RentableProductStockReservationInfo>

    fun existsByRentableProductStockInfoAndRowDeleteDateStrAndProductReadyDatetime(
        rentableProductStockInfo: Db1_RaillyLinkerCompany_RentableProductStockInfo,
        rowDeleteDateStr: String,
        productReadyDatetime: LocalDateTime?
    ): Boolean
}