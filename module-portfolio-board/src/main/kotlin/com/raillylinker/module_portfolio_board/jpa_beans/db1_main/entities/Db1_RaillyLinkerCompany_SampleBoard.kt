package com.raillylinker.module_portfolio_board.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "sample_board",
    catalog = "railly_linker_company"
)
@Comment("게시글 테이블")
class Db1_RaillyLinkerCompany_SampleBoard(
    @ManyToOne
    @JoinColumn(name = "total_auth_member_uid", nullable = false)
    @Comment("멤버 고유번호(railly_linker_company.total_auth_member.uid)")
    var totalAuthMember: Db1_RaillyLinkerCompany_TotalAuthMember,

    @Column(name = "board_title", nullable = false, columnDefinition = "VARCHAR(200)")
    @Comment("게시글 타이틀")
    var boardTitle: String,

    @Column(name = "board_content", nullable = false, columnDefinition = "TEXT")
    @Comment("게시글 본문")
    var boardContent: String,

    @Column(name = "board_hidden", nullable = false, columnDefinition = "BIT(1)")
    @Comment("게시글 숨김 여부")
    var boardHidden: Boolean,

    @Column(name = "view_count", nullable = false, columnDefinition = "BIGINT")
    @Comment("조회수")
    var viewCount: Boolean
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
    @OneToMany(mappedBy = "sampleBoard", fetch = FetchType.LAZY)
    var sampleBoardCommentList: MutableList<Db1_RaillyLinkerCompany_SampleBoardComment> = mutableListOf()


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}