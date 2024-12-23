package com.raillylinker.module_portfolio_reservation.jpa_beans.db1_main.repositories

import com.raillylinker.module_portfolio_reservation.jpa_beans.db1_main.entities.Db1_Raillylinker_Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_Raillylinker_Reservation_Repository :
    JpaRepository<Db1_Raillylinker_Reservation, Long> {
}