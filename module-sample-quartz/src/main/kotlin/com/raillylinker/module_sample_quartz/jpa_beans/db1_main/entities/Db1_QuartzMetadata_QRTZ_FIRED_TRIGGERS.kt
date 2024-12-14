package com.raillylinker.module_sample_quartz.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_FIRED_TRIGGERS.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_FIRED_TRIGGERS",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_FIRED_TRIGGERS")
class Db1_QuartzMetadata_QRTZ_FIRED_TRIGGERS(
    @Id
    @Column(name = "SCHED_NAME", nullable = false, columnDefinition = "VARCHAR(120)")
    @Comment("SCHED_NAME")
    var schedName: String,

    @Id
    @Column(name = "ENTRY_ID", nullable = false, columnDefinition = "VARCHAR(95)")
    @Comment("ENTRY_ID")
    var entryId: String,

    @Column(name = "TRIGGER_NAME", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("TRIGGER_NAME")
    var triggerName: String,

    @Column(name = "TRIGGER_GROUP", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("TRIGGER_GROUP")
    var triggerGroup: String,

    @Column(name = "INSTANCE_NAME", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("INSTANCE_NAME")
    var instanceName: String,

    @Column(name = "FIRED_TIME", nullable = false, columnDefinition = "BIGINT")
    @Comment("FIRED_TIME")
    var firedTime: Long,

    @Column(name = "SCHED_TIME", nullable = false, columnDefinition = "BIGINT")
    @Comment("SCHED_TIME")
    var schedTime: Long,

    @Column(name = "PRIORITY", nullable = false, columnDefinition = "INT")
    @Comment("PRIORITY")
    var priority: Int,

    @Column(name = "STATE", nullable = false, columnDefinition = "VARCHAR(16)")
    @Comment("STATE")
    var state: String,

    @Column(name = "JOB_NAME", nullable = true, columnDefinition = "VARCHAR(190)")
    @Comment("JOB_NAME")
    var jobName: String?,

    @Column(name = "JOB_GROUP", nullable = true, columnDefinition = "VARCHAR(190)")
    @Comment("JOB_GROUP")
    var jobGroup: String?,

    @Column(name = "IS_NONCONCURRENT", nullable = true, columnDefinition = "VARCHAR(1)")
    @Comment("IS_NONCONCURRENT")
    var isNonConcurrent: String?,

    @Column(name = "REQUESTS_RECOVERY", nullable = true, columnDefinition = "VARCHAR(1)")
    @Comment("REQUESTS_RECOVERY")
    var requestsRecovery: String?
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val entryId: String = ""
    ) : Serializable
}