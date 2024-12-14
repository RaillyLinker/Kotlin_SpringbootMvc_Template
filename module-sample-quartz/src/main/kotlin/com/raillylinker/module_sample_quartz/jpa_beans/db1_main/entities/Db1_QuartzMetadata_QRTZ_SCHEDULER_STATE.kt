package com.raillylinker.module_sample_quartz.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_SCHEDULER_STATE.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_SCHEDULER_STATE",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_SCHEDULER_STATE")
class Db1_QuartzMetadata_QRTZ_SCHEDULER_STATE(
    @Id
    @Column(name = "SCHED_NAME", nullable = false, columnDefinition = "VARCHAR(120)")
    @Comment("SCHED_NAME")
    var schedName: String,

    @Id
    @Column(name = "INSTANCE_NAME", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("INSTANCE_NAME")
    var instanceName: String,

    @Column(name = "LAST_CHECKIN_TIME", nullable = false, columnDefinition = "BIGINT")
    @Comment("LAST_CHECKIN_TIME")
    var lastCheckinTime: Long,

    @Column(name = "CHECKIN_INTERVAL", nullable = false, columnDefinition = "BIGINT")
    @Comment("CHECKIN_INTERVAL")
    var checkinInterval: Long
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val instanceName: String = ""
    ) : Serializable
}