package com.raillylinker.module_sample_batch.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "BATCH_STEP_EXECUTION",
    catalog = "batch_metadata"
)
@Comment("BATCH_STEP_EXECUTION")
class Db1_BatchMetadata_BATCH_STEP_EXECUTION(
    @Column(name = "VERSION", nullable = false, columnDefinition = "BIGINT")
    @Comment("VERSION")
    var version: Long,

    @Column(name = "STEP_NAME", nullable = false, columnDefinition = "VARCHAR(100)")
    @Comment("STEP_NAME")
    var stepName: String,

    @ManyToOne
    @JoinColumn(name = "JOB_EXECUTION_ID", nullable = false)
    @Comment("JOB_EXECUTION_ID")
    var batchJobExecution: Db1_BatchMetadata_BATCH_JOB_EXECUTION,

    @Column(name = "START_TIME", nullable = true, columnDefinition = "DATETIME(6)")
    @Comment("START_TIME")
    var startTime: LocalDateTime?,

    @Column(name = "END_TIME", nullable = true, columnDefinition = "DATETIME(6)")
    @Comment("END_TIME")
    var endTime: LocalDateTime?,

    @Column(name = "STATUS", nullable = true, columnDefinition = "VARCHAR(10)")
    @Comment("STATUS")
    var status: String?,

    @Column(name = "COMMIT_COUNT", nullable = true, columnDefinition = "BIGINT")
    @Comment("COMMIT_COUNT")
    var commitCount: Long?,

    @Column(name = "READ_COUNT", nullable = true, columnDefinition = "BIGINT")
    @Comment("READ_COUNT")
    var readCount: Long?,

    @Column(name = "FILTER_COUNT", nullable = true, columnDefinition = "BIGINT")
    @Comment("FILTER_COUNT")
    var filterCount: Long?,

    @Column(name = "WRITE_COUNT", nullable = true, columnDefinition = "BIGINT")
    @Comment("WRITE_COUNT")
    var writeCount: Long?,

    @Column(name = "READ_SKIP_COUNT", nullable = true, columnDefinition = "BIGINT")
    @Comment("READ_SKIP_COUNT")
    var readSkipCount: Long?,

    @Column(name = "WRITE_SKIP_COUNT", nullable = true, columnDefinition = "BIGINT")
    @Comment("WRITE_SKIP_COUNT")
    var writeSkipCount: Long?,

    @Column(name = "PROCESS_SKIP_COUNT", nullable = true, columnDefinition = "BIGINT")
    @Comment("PROCESS_SKIP_COUNT")
    var processSkipCount: Long?,

    @Column(name = "ROLLBACK_COUNT", nullable = true, columnDefinition = "BIGINT")
    @Comment("ROLLBACK_COUNT")
    var rollbackCount: Long?,

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STEP_EXECUTION_ID", columnDefinition = "BIGINT")
    @Comment("STEP_EXECUTION_ID")
    var stepExecutionId: Long? = null

    @Column(name = "CREATE_TIME", nullable = false, columnDefinition = "DATETIME(6)")
    @CreationTimestamp
    @Comment("CREATE_TIME")
    var createTime: LocalDateTime? = null


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}