package com.raillylinker.module_sample_batch.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "BATCH_JOB_EXECUTION_CONTEXT",
    catalog = "batch_metadata"
)
@Comment("BATCH_JOB_EXECUTION_CONTEXT")
class Db1_BatchMetadata_BATCH_JOB_EXECUTION_CONTEXT(
    @Column(name = "SHORT_CONTEXT", nullable = false, columnDefinition = "VARCHAR(2500)")
    @Comment("SHORT_CONTEXT")
    var shortContext: String,

    @Column(name = "SERIALIZED_CONTEXT", nullable = true, columnDefinition = "TEXT")
    @Comment("SERIALIZED_CONTEXT")
    var serializedContext: String?
) {
    @Id
    @Column(name = "JOB_EXECUTION_ID", columnDefinition = "BIGINT")
    @Comment("JOB_EXECUTION_ID")
    var jobExecutionId: Long? = null


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}