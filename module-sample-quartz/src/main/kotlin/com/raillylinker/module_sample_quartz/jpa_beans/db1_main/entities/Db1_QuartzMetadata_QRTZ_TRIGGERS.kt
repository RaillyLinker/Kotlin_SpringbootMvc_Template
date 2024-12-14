package com.raillylinker.module_sample_quartz.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_TRIGGERS.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_TRIGGERS",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_TRIGGERS")
class Db1_QuartzMetadata_QRTZ_TRIGGERS(
    @Id
    @Column(name = "SCHED_NAME", nullable = false, columnDefinition = "VARCHAR(120)")
    @Comment("SCHED_NAME")
    var schedName: String,

    @Id
    @Column(name = "TRIGGER_NAME", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("TRIGGER_NAME")
    var triggerName: String,

    @Id
    @Column(name = "TRIGGER_GROUP", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("TRIGGER_GROUP")
    var triggerGroup: String,

    @ManyToOne
    @JoinColumns(
        JoinColumn(name = "JOB_NAME", referencedColumnName = "JOB_NAME"),
        JoinColumn(name = "JOB_GROUP", referencedColumnName = "JOB_GROUP")
    )
    @Comment("JOB_NAME, JOB_GROUP")
    var jobDetails: Db1_QuartzMetadata_QRTZ_JOB_DETAILS,

    @Column(name = "DESCRIPTION", nullable = true, columnDefinition = "VARCHAR(250)")
    @Comment("DESCRIPTION")
    var description: String?,

    @Column(name = "NEXT_FIRE_TIME", nullable = true, columnDefinition = "BIGINT")
    @Comment("NEXT_FIRE_TIME")
    var nextFireTime: Long?,

    @Column(name = "PREV_FIRE_TIME", nullable = true, columnDefinition = "BIGINT")
    @Comment("PREV_FIRE_TIME")
    var prevFireTime: Long?,

    @Column(name = "PRIORITY", nullable = true, columnDefinition = "INT")
    @Comment("PRIORITY")
    var priority: Int?,

    @Column(name = "TRIGGER_STATE", nullable = false, columnDefinition = "VARCHAR(16)")
    @Comment("TRIGGER_STATE")
    var triggerState: String,

    @Column(name = "TRIGGER_TYPE", nullable = false, columnDefinition = "VARCHAR(8)")
    @Comment("TRIGGER_TYPE")
    var triggerType: String,

    @Column(name = "START_TIME", nullable = false, columnDefinition = "BIGINT")
    @Comment("START_TIME")
    var startTime: Long,

    @Column(name = "END_TIME", nullable = true, columnDefinition = "BIGINT")
    @Comment("END_TIME")
    var endTime: Long?,

    @Column(name = "CALENDAR_NAME", nullable = true, columnDefinition = "VARCHAR(190)")
    @Comment("CALENDAR_NAME")
    var calendarName: String?,

    @Column(name = "MISFIRE_INSTR", nullable = true, columnDefinition = "SMALLINT")
    @Comment("MISFIRE_INSTR")
    var misfireInstr: Short?,

    @Column(name = "JOB_DATA", nullable = true, columnDefinition = "BLOB")
    @Comment("JOB_DATA")
    var jobData: ByteArray?
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val triggerName: String = "",
        val triggerGroup: String = ""
    ) : Serializable
}