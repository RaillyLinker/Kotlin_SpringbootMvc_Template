package com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(
    name = "payment",
    catalog = "railly_linker_company"
)
@Comment("결재 정보 테이블")
class Db1_RaillyLinkerCompany_Payment(
    @Column(name = "payment_type", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    @Comment("결재 타입(1 : 무통장 입금, 2 : 실시간 계좌이체, 3 : 토스 페이)")
    var paymentType: String,

    @Column(name = "payment_amount", nullable = false, columnDefinition = "DECIMAL(15, 2) UNSIGNED")
    @Comment("결재 금액")
    var paymentAmount: BigDecimal,

    @Column(name = "payment_complete_datetime", nullable = true, columnDefinition = "DATETIME(3)")
    @Comment("결재 완료 일시")
    var paymentCompleteDatetime: LocalDateTime?,

    @Column(name = "payment_failed", nullable = false, columnDefinition = "BIT(1)")
    @Comment("결재 실패 여부")
    var paymentFailed: Boolean
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
    // 상위 카테고리가 삭제되면 하위 카테고리도 삭제
    @OneToMany(
        mappedBy = "payment",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var paymentRefundList: MutableList<Db1_RaillyLinkerCompany_PaymentRefund> = mutableListOf()


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}