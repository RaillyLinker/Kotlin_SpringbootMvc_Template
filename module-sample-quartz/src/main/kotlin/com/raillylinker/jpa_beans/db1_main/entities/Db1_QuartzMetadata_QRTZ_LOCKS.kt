package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_LOCKS.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_LOCKS",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_LOCKS")
class Db1_QuartzMetadata_QRTZ_LOCKS(
    @Id
    @Column(name = "SCHED_NAME", nullable = false, columnDefinition = "VARCHAR(120)")
    @Comment("SCHED_NAME")
    var schedName: String,

    @Id
    @Column(name = "LOCK_NAME", nullable = false, columnDefinition = "VARCHAR(40)")
    @Comment("LOCK_NAME")
    var lockName: String
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val lockName: String = ""
    ) : Serializable
}