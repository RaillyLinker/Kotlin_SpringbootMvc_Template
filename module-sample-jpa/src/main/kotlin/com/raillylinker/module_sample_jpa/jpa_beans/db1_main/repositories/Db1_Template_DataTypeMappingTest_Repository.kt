package com.raillylinker.module_sample_jpa.jpa_beans.db1_main.repositories

import com.raillylinker.module_sample_jpa.jpa_beans.db1_main.entities.Db1_Template_DataTypeMappingTest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface Db1_Template_DataTypeMappingTest_Repository : JpaRepository<Db1_Template_DataTypeMappingTest, Long> {
}