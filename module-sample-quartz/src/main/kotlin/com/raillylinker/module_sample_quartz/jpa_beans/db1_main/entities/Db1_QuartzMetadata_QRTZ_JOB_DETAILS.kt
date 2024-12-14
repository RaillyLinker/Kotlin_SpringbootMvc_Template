package com.raillylinker.module_sample_quartz.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_JOB_DETAILS.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_JOB_DETAILS",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_JOB_DETAILS")
class Db1_QuartzMetadata_QRTZ_JOB_DETAILS(
    @Id
    @Column(name = "SCHED_NAME", nullable = false, columnDefinition = "VARCHAR(120)")
    @Comment("SCHED_NAME")
    var schedName: String,

    @Id
    @Column(name = "JOB_NAME", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("JOB_NAME")
    var jobName: String,

    @Id
    @Column(name = "JOB_GROUP", nullable = false, columnDefinition = "VARCHAR(190)")
    @Comment("JOB_GROUP")
    var jobGroup: String,

    @Column(name = "DESCRIPTION", nullable = true, columnDefinition = "VARCHAR(250)")
    @Comment("DESCRIPTION")
    var description: String?,

    @Column(name = "JOB_CLASS_NAME", nullable = false, columnDefinition = "VARCHAR(250)")
    @Comment("JOB_CLASS_NAME")
    var jobClassName: String,

    @Column(name = "IS_DURABLE", nullable = false, columnDefinition = "VARCHAR(120)")
    @Comment("IS_DURABLE")
    var isDurable: String,

    @Column(name = "IS_NONCONCURRENT", nullable = false, columnDefinition = "VARCHAR(1)")
    @Comment("IS_NONCONCURRENT")
    var isNonConcurrent: String,

    @Column(name = "IS_UPDATE_DATA", nullable = false, columnDefinition = "VARCHAR(1)")
    @Comment("IS_UPDATE_DATA")
    var isUpdateData: String,

    @Column(name = "REQUESTS_RECOVERY", nullable = false, columnDefinition = "VARCHAR(1)")
    @Comment("REQUESTS_RECOVERY")
    var requestsRecovery: String,

    @Column(name = "JOB_DATA", nullable = true, columnDefinition = "BLOB")
    @Comment("JOB_DATA")
    var jobData: ByteArray?
) {
    @OneToMany(
        mappedBy = "jobDetails",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var qrtzTriggersList: MutableList<Db1_QuartzMetadata_QRTZ_TRIGGERS> = mutableListOf()


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val jobName: String = "",
        val jobGroup: String = ""
    ) : Serializable
}