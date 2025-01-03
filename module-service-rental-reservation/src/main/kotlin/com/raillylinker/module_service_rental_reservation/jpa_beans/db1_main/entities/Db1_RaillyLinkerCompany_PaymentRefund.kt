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
    name = "payment_refund",
    catalog = "railly_linker_company"
)
@Comment("결제 환불 정보 테이블")
class Db1_RaillyLinkerCompany_PaymentRefund(
    @ManyToOne
    @JoinColumn(name = "payment_uid", nullable = false)
    @Comment("payment 테이블 고유번호 (railly_linker_company.payment.uid)")
    var payment: Db1_RaillyLinkerCompany_Payment,

    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(15, 2)")
    @Comment("환불 금액")
    var amount: BigDecimal,

    @Column(name = "currency_code", nullable = false, columnDefinition = "CHAR(3)")
    @Comment("환불 금액 통화 코드(IOS 4217, ex : KRW, USD, EUR...)")
    var currencyCode: String,

    @Column(name = "refund_datetime", nullable = true, columnDefinition = "DATETIME(3)")
    @Comment("환불 완료 일시")
    var paymentCompleteDatetime: LocalDateTime?
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


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}