package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_Template_PublicHolidayKorea
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_Template_PublicHolidayKorea_Repository : JpaRepository<Db1_Template_PublicHolidayKorea, Long> {
}