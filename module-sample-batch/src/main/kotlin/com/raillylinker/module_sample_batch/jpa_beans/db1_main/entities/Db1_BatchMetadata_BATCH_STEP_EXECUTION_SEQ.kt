package com.raillylinker.module_sample_batch.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "BATCH_STEP_EXECUTION_SEQ",
    catalog = "batch_metadata"
)
@Comment("BATCH_STEP_EXECUTION_SEQ")
class Db1_BatchMetadata_BATCH_STEP_EXECUTION_SEQ(
    @Id
    @Column(name = "UNIQUE_KEY", nullable = false, columnDefinition = "CHAR(1)", unique = true)
    @Comment("UNIQUE_KEY")
    var uniqueKey: Char,

    @Column(name = "ID", columnDefinition = "BIGINT")
    @Comment("ID")
    var id: Long
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}