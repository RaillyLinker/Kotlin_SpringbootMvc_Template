package com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories

import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockReservationInfo
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory, Long> {
    fun findAllByRentableProductStockReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
        rentableProductStockReservationInfo: Db1_RaillyLinkerCompany_RentableProductStockReservationInfo,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory>

    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductStockReservationStateChangeHistory?
}