package com.raillylinker.module_auth.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "total_auth_member_phone",
    catalog = "railly_linker_company",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["phone_number", "row_delete_date_str"])
    ]
)
@Comment("통합 로그인 계정 회원 전화 정보 테이블")
class Db1_RaillyLinkerCompany_TotalAuthMemberPhone(
    @ManyToOne
    @JoinColumn(name = "total_auth_member_uid", nullable = false)
    @Comment("멤버 고유번호(railly_linker_company.total_auth_member.uid)")
    var totalAuthMember: Db1_RaillyLinkerCompany_TotalAuthMember,

    @Column(name = "phone_number", nullable = false, columnDefinition = "VARCHAR(45)")
    @Comment("전화번호(국가번호 + 전화번호, 중복 비허용)")
    var phoneNumber: String
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

    @Column(name = "row_delete_date_str", nullable = false, columnDefinition = "VARCHAR(50)")
    @ColumnDefault("'/'")
    @Comment("행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)")
    var rowDeleteDateStr: String = "/"

    // ---------------------------------------------------------------------------------------------
    // [@OneToMany 변수들]
    @OneToMany(mappedBy = "frontTotalAuthMemberPhone", fetch = FetchType.LAZY)
    var totalAuthMemberList: MutableList<Db1_RaillyLinkerCompany_TotalAuthMember> = mutableListOf()


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}