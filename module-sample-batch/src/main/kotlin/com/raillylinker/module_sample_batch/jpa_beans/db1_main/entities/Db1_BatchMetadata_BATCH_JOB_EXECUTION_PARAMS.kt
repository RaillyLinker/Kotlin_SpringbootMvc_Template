package com.raillylinker.module_sample_batch.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@IdClass(Db1_BatchMetadata_BATCH_JOB_EXECUTION_PARAMS.BatchJobExecutionParamsId::class)
@Table(
    name = "BATCH_JOB_EXECUTION_PARAMS",
    catalog = "batch_metadata"
)
@Comment("BATCH_JOB_EXECUTION_PARAMS")
class Db1_BatchMetadata_BATCH_JOB_EXECUTION_PARAMS(
    @Id
    @ManyToOne
    @JoinColumn(name = "JOB_EXECUTION_ID", nullable = false)
    @Comment("JOB_EXECUTION_ID")
    var batchJobExecution: Db1_BatchMetadata_BATCH_JOB_EXECUTION,

    @Id
    @Column(name = "PARAMETER_NAME", nullable = false, columnDefinition = "VARCHAR(100)")
    @Comment("PARAMETER_NAME")
    var parameterName: String,

    @Id
    @Column(name = "PARAMETER_TYPE", nullable = false, columnDefinition = "VARCHAR(100)")
    @Comment("PARAMETER_TYPE")
    var parameterType: String,

    @Id
    @Column(name = "PARAMETER_VALUE", nullable = true, columnDefinition = "VARCHAR(2500)")
    @Comment("PARAMETER_VALUE")
    var parameterValue: String?,

    @Id
    @Column(name = "IDENTIFYING", nullable = false, columnDefinition = "CHAR(1)")
    @Comment("IDENTIFYING")
    var identifying: Char
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class BatchJobExecutionParamsId(
        val batchJobExecution: Long = 0,  // Foreign Key 필드의 식별자 (주의: `@ManyToOne` 대상의 PK 타입으로 설정)
        val parameterName: String = "",
        val parameterType: String = "",
        val parameterValue: String? = null,
        val identifying: Char = 'N'
    ) : Serializable
}