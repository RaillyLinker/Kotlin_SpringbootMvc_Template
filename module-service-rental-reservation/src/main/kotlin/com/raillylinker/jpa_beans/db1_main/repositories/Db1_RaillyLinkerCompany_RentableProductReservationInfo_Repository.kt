package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_RentableProductReservationInfo
import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_TotalAuthMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_RaillyLinkerCompany_RentableProductReservationInfo_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_RentableProductReservationInfo, Long> {
    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductReservationInfo?

    fun findByUidAndTotalAuthMemberAndRowDeleteDateStr(
        uid: Long,
        totalAuthMember: Db1_RaillyLinkerCompany_TotalAuthMember,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_RentableProductReservationInfo?

    fun findAllByTotalAuthMemberUidAndRowDeleteDateStr(
        totalAuthMemberUid: Long,
        rowDeleteDateStr: String
    ): List<Db1_RaillyLinkerCompany_RentableProductReservationInfo>
}