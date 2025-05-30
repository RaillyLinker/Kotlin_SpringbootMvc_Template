package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "BATCH_JOB_SEQ",
    catalog = "batch_metadata"
)
@Comment("BATCH_JOB_SEQ")
class Db1_BatchMetadata_BATCH_JOB_SEQ(
    @Column(name = "ID", nullable = false, columnDefinition = "BIGINT")
    @Comment("ID")
    var id: Long,

    @Column(name = "UNIQUE_KEY", nullable = false, columnDefinition = "CHAR(1)", unique = true)
    @Comment("UNIQUE_KEY")
    var uniqueKey: Char
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT")
    @Comment("행 고유값")
    var uid: Long? = null


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}