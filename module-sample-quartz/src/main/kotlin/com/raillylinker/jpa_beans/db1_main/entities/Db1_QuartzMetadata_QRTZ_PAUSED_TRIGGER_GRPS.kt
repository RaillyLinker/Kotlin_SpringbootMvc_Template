package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_PAUSED_TRIGGER_GRPS.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_PAUSED_TRIGGER_GRPS",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_PAUSED_TRIGGER_GRPS")
class Db1_QuartzMetadata_QRTZ_PAUSED_TRIGGER_GRPS(
    @Id
    @Column(name = "SCHED_NAME", nullable = false, columnDefinition = "VARCHAR(120)")
    @Comment("SCHED_NAME")
    var schedName: String,

    @Id
    @Column(name = "TRIGGER_GROUP", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("TRIGGER_GROUP")
    var triggerGroup: String
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val triggerGroup: String = ""
    ) : Serializable
}