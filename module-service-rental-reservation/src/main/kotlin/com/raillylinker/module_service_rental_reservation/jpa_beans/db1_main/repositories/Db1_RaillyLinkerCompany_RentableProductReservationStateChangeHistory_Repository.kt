package com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories

import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationInfo
import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory, Long> {
    fun findAllByRentableProductReservationInfoAndRowDeleteDateStrOrderByRowCreateDateDesc(
        rentableProductReservationInfo: Db1_RaillyLinkerCompany_RentableProductReservationInfo,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory>

    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory?
}