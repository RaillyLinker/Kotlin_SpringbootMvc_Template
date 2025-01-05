package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_BLOB_TRIGGERS.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_BLOB_TRIGGERS",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_BLOB_TRIGGERS")
class Db1_QuartzMetadata_QRTZ_BLOB_TRIGGERS(
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

    @Column(name = "BLOB_DATA", nullable = true, columnDefinition = "BLOB")
    @Comment("BLOB_DATA")
    var blobData: ByteArray?
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val triggerName: String = "",
        val triggerGroup: String = ""
    ) : Serializable
}