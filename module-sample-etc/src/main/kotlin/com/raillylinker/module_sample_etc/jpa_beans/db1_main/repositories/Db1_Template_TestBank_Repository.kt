package com.raillylinker.module_sample_etc.jpa_beans.db1_main.repositories

import com.raillylinker.module_sample_etc.jpa_beans.db1_main.entities.Db1_Template_TestBank
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_Template_TestBank_Repository : JpaRepository<Db1_Template_TestBank, Long> {
    fun findByUserIdxAndRowDeleteDateStr(
        userIdx: Long,
        rowDeleteDateStr: String
    ): Db1_Template_TestBank?
}