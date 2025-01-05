package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "BATCH_JOB_EXECUTION",
    catalog = "batch_metadata"
)
@Comment("BATCH_JOB_EXECUTION")
class Db1_BatchMetadata_BATCH_JOB_EXECUTION(
    @Id
    @Column(name = "JOB_EXECUTION_ID", nullable = false, columnDefinition = "BIGINT")
    @Comment("JOB_EXECUTION_ID")
    var jobExecutionId: Long,

    @Column(name = "VERSION", nullable = true, columnDefinition = "BIGINT")
    @Comment("VERSION")
    var version: String?,

    @ManyToOne
    @JoinColumn(name = "JOB_INSTANCE_ID", nullable = false)
    @Comment("JOB_INSTANCE_ID")
    var batchJobInstance: Db1_BatchMetadata_BATCH_JOB_INSTANCE,

    @Column(name = "CREATE_TIME", nullable = false, columnDefinition = "DATETIME(6)")
    @Comment("CREATE_TIME")
    var createTime: LocalDateTime,

    @Column(name = "START_TIME", nullable = true, columnDefinition = "DATETIME(6)")
    @Comment("START_TIME")
    var startTime: LocalDateTime?,

    @Column(name = "END_TIME", nullable = true, columnDefinition = "DATETIME(6)")
    @Comment("END_TIME")
    var endTime: LocalDateTime?,

    @Column(name = "STATUS", nullable = true, columnDefinition = "VARCHAR(10)")
    @Comment("STATUS")
    var status: String?,

    @Column(name = "EXIT_CODE", nullable = true, columnDefinition = "VARCHAR(2500)")
    @Comment("EXIT_CODE")
    var exitCode: String?,

    @Column(name = "EXIT_MESSAGE", nullable = true, columnDefinition = "VARCHAR(2500)")
    @Comment("EXIT_MESSAGE")
    var exitMessage: String?,

    @Column(name = "LAST_UPDATED", nullable = true, columnDefinition = "DATETIME(6)")
    @Comment("LAST_UPDATED")
    var lastUpdated: LocalDateTime?
) {
    @OneToMany(
        mappedBy = "batchJobExecution",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var batchJobExecutionParamsList: MutableList<Db1_BatchMetadata_BATCH_JOB_EXECUTION_PARAMS> = mutableListOf()

    @OneToMany(
        mappedBy = "batchJobExecution",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var batchStepExecutionList: MutableList<Db1_BatchMetadata_BATCH_STEP_EXECUTION> = mutableListOf()


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}