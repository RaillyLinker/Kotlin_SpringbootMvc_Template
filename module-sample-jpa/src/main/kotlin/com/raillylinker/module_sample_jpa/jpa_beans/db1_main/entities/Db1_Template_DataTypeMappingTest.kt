package com.raillylinker.module_sample_jpa.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "data_type_mapping_test",
    catalog = "template"
)
@Comment("ORM 과 Database 간 데이터 타입 매핑을 위한 테이블")
class Db1_Template_DataTypeMappingTest(
    @Column(name = "tiny_int", nullable = false, columnDefinition = "TINYINT")
    @Comment("-128 ~ 127")
    var tinyInt: Byte,
    @Column(name = "tiny_int_unsigned", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    @Comment("0 ~ 255")
    var tinyIntUnsigned: Short,
    @Column(name = "small_int", nullable = false, columnDefinition = "SMALLINT")
    @Comment("-32,768 ~ 32,767")
    var smallInt: Short,
    @Column(name = "small_int_unsigned", nullable = false, columnDefinition = "SMALLINT UNSIGNED")
    @Comment("0 ~ 65,535")
    var smallIntUnsigned: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT UNSIGNED")
    @Comment("행 고유값")
    var uid: Long? = null

    @Column(name = "row_create_date", nullable = false, columnDefinition = "DATETIME(3)")
    @CreationTimestamp
    @Comment("행 생성일")
    var rowCreateDate: LocalDateTime? = null

    @Column(name = "row_update_date", nullable = false, columnDefinition = "DATETIME(3)")
    @UpdateTimestamp
    @Comment("행 수정일")
    var rowUpdateDate: LocalDateTime? = null


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}