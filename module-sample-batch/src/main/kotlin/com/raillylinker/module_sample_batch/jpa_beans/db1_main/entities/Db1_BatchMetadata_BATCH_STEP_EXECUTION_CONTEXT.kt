package com.raillylinker.module_sample_batch.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "BATCH_STEP_EXECUTION_CONTEXT",
    catalog = "batch_metadata"
)
@Comment("BATCH_STEP_EXECUTION_CONTEXT")
class Db1_BatchMetadata_BATCH_STEP_EXECUTION_CONTEXT(
    @Column(name = "SHORT_CONTEXT", nullable = false, columnDefinition = "VARCHAR(2500)")
    @Comment("SHORT_CONTEXT")
    var shortContext: String,

    @Column(name = "SERIALIZED_CONTEXT", nullable = true, columnDefinition = "TEXT")
    @Comment("SERIALIZED_CONTEXT")
    var serializedContext: String?
) {
    @Id
    @Column(name = "STEP_EXECUTION_ID", columnDefinition = "BIGINT")
    @Comment("STEP_EXECUTION_ID")
    var stepExecutionId: Long? = null


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}