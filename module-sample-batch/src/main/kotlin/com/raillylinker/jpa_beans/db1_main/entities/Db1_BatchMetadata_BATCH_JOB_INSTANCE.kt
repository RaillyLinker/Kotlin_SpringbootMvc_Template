package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "BATCH_JOB_INSTANCE",
    catalog = "batch_metadata"
)
@Comment("BATCH_JOB_INSTANCE")
class Db1_BatchMetadata_BATCH_JOB_INSTANCE(
    @Id
    @Column(name = "JOB_INSTANCE_ID", nullable = true, columnDefinition = "BIGINT")
    @Comment("JOB_INSTANCE_ID")
    var jobInstanceId: Long,

    @Column(name = "VERSION", nullable = true, columnDefinition = "BIGINT")
    @Comment("VERSION")
    var version: String?,

    @Column(name = "JOB_NAME", nullable = false, columnDefinition = "VARCHAR(100)")
    @Comment("JOB_NAME")
    var jobName: String,

    @Column(name = "JOB_KEY", nullable = false, columnDefinition = "VARCHAR(32)")
    @Comment("JOB_KEY")
    var jobKey: String
) {
    @OneToMany(
        mappedBy = "batchJobInstance",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var batchJobExecutionList: MutableList<Db1_BatchMetadata_BATCH_JOB_EXECUTION> = mutableListOf()


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}