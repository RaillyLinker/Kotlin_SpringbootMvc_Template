package com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.repositories

import com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_RentableProductReservationInfo_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductReservationInfo, Long> {
    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductReservationInfo?
}