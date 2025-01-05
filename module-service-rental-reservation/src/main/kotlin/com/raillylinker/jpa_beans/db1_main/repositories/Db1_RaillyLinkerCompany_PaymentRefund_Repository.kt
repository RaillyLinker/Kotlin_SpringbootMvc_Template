package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_PaymentRefund
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_PaymentRefund_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_PaymentRefund, Long> {
}