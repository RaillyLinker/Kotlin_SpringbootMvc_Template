package com.raillylinker.module_sample_quartz.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@IdClass(Db1_QuartzMetadata_QRTZ_SIMPROP_TRIGGERS.QuartzBlobTriggersId::class) // 복합 키 클래스 지정
@Entity
@Table(
    name = "QRTZ_SIMPROP_TRIGGERS",
    catalog = "quartz_metadata"
)
@Comment("QRTZ_SIMPROP_TRIGGERS")
class Db1_QuartzMetadata_QRTZ_SIMPROP_TRIGGERS(
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

    @Column(name = "STR_PROP_1", nullable = true, columnDefinition = "VARCHAR(512)")
    @Comment("STR_PROP_1")
    var strProp1: String?,

    @Column(name = "STR_PROP_2", nullable = true, columnDefinition = "VARCHAR(512)")
    @Comment("STR_PROP_2")
    var strProp2: String?,

    @Column(name = "STR_PROP_3", nullable = true, columnDefinition = "VARCHAR(512)")
    @Comment("STR_PROP_3")
    var strProp3: String?,

    @Column(name = "INT_PROP_1", nullable = true, columnDefinition = "INT")
    @Comment("INT_PROP_1")
    var intProp1: Int?,

    @Column(name = "INT_PROP_2", nullable = true, columnDefinition = "INT")
    @Comment("INT_PROP_2")
    var intProp2: Int?,

    @Column(name = "LONG_PROP_1", nullable = true, columnDefinition = "BIGINT")
    @Comment("LONG_PROP_1")
    var longProp1: Long?,

    @Column(name = "LONG_PROP_2", nullable = true, columnDefinition = "BIGINT")
    @Comment("LONG_PROP_2")
    var longProp2: Long?,

    @Column(name = "DEC_PROP_1", nullable = true, columnDefinition = "DECIMAL(13,4)")
    @Comment("DEC_PROP_1")
    var decProp1: BigDecimal?,

    @Column(name = "DEC_PROP_2", nullable = true, columnDefinition = "DECIMAL(13,4)")
    @Comment("DEC_PROP_2")
    var decProp2: BigDecimal?,

    @Column(name = "BOOL_PROP_1", nullable = true, columnDefinition = "VARCHAR(1)")
    @Comment("BOOL_PROP_1")
    var boolProp1: String?,

    @Column(name = "BOOL_PROP_2", nullable = true, columnDefinition = "VARCHAR(1)")
    @Comment("BOOL_PROP_2")
    var boolProp2: String?
) {


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class QuartzBlobTriggersId(
        val schedName: String = "",
        val triggerName: String = "",
        val triggerGroup: String = ""
    ) : Serializable
}