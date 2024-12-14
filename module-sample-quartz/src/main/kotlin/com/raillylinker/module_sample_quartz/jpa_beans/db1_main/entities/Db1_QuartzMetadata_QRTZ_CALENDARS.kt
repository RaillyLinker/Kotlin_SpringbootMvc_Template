package com.raillylinker.module_sample_quartz.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_CALENDARS.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_CALENDARS",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_CALENDARS")
class Db1_QuartzMetadata_QRTZ_CALENDARS(
    @Id
    @Column(name = "SCHED_NAME", nullable = false, columnDefinition = "VARCHAR(120)")
    @Comment("SCHED_NAME")
    var schedName: String,

    @Id
    @Column(name = "CALENDAR_NAME", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("CALENDAR_NAME")
    var calendarName: String,

    @Column(name = "CALENDAR", nullable = false, columnDefinition = "BLOB")
    @Comment("CALENDAR")
    var calendar: ByteArray
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val calendarName: String = ""
    ) : Serializable
}