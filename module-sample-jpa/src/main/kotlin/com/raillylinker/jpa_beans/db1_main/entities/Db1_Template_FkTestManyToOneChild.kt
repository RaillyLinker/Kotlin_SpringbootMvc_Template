package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

// 서로 다른 스키마의 테이블 끼리도 fk 가 가능합니다.
// 테스트 하기 위해선 이 테이블의 스키마를 다른 스키마로 변경하여 테스트 해보세요.

// Fk 관계 중 OneToOne 은 논리적 삭제를 적용할시 사용이 불가능합니다.
//     이때는, One to One 역시 Many to One 을 사용하며, 합성 Unique 로 FK 변수를 유니크 처리 한 후,
//     로직상으로 활성화된 행이 한개 뿐이라고 처리하면 됩니다.
@Entity
@Table(
    name = "fk_test_many_to_one_child",
    catalog = "template"
)
@Comment("Foreign Key 테스트용 테이블 (one to many 테스트용 자식 테이블)")
class Db1_Template_FkTestManyToOneChild(
    @Column(name = "child_name", nullable = false, columnDefinition = "VARCHAR(255)")
    @Comment("자식 테이블 이름")
    var childName: String,

    @ManyToOne
    @JoinColumn(name = "fk_test_parent_uid", nullable = false)
    @Comment("FK 부모 테이블 고유번호 (template.fk_test_parent.uid)")
    var fkTestParent: Db1_Template_FkTestParent
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT")
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

    @Column(name = "row_delete_date_str", nullable = false, columnDefinition = "VARCHAR(50)")
    @ColumnDefault("'/'")
    @Comment("행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)")
    var rowDeleteDateStr: String = "/"


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}