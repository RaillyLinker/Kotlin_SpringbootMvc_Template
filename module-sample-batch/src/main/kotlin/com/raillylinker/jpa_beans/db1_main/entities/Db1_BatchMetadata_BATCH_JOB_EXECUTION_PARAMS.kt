package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(
    name = "BATCH_JOB_EXECUTION_PARAMS",
    catalog = "batch_metadata"
)
@Comment("BATCH_JOB_EXECUTION_PARAMS")
class Db1_BatchMetadata_BATCH_JOB_EXECUTION_PARAMS(
    @ManyToOne
    @JoinColumn(name = "JOB_EXECUTION_ID", nullable = false)
    @Comment("JOB_EXECUTION_ID")
    var batchJobExecution: Db1_BatchMetadata_BATCH_JOB_EXECUTION,

    @Column(name = "PARAMETER_NAME", nullable = false, columnDefinition = "VARCHAR(100)")
    @Comment("PARAMETER_NAME")
    var parameterName: String,

    @Column(name = "PARAMETER_TYPE", nullable = false, columnDefinition = "VARCHAR(100)")
    @Comment("PARAMETER_TYPE")
    var parameterType: String,

    @Column(name = "PARAMETER_VALUE", nullable = true, columnDefinition = "VARCHAR(2500)")
    @Comment("PARAMETER_VALUE")
    var parameterValue: String?,

    @Column(name = "IDENTIFYING", nullable = false, columnDefinition = "CHAR(1)")
    @Comment("IDENTIFYING")
    var identifying: Char
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT UNSIGNED")
    @Comment("행 고유값")
    var uid: Long? = null


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
}