package com.raillylinker.module_portfolio_rental_reservation.jpa_beans.db1_main.repositories

import com.raillylinker.module_portfolio_rental_reservation.jpa_beans.db1_main.entities.Db1_Raillylinker_RentableProductInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_Raillylinker_RentableProductInfo_Repository :
    JpaRepository<Db1_Raillylinker_RentableProductInfo, Long> {
}