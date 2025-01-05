package com.raillylinker.jpa_beans.db1_main.entities

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
@Comment("결제 정보 테이블")
class Db1_RaillyLinkerCompany_Payment(
    @Column(name = "payment_type", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    @Comment("결제 타입(1 : 무통장 입금, 2 : 실시간 계좌이체, 3 : 토스 페이)")
    var paymentType: Short,

    @Column(name = "payment_amount", nullable = false, columnDefinition = "DECIMAL(15, 2)")
    @Comment("결제 금액")
    var paymentAmount: BigDecimal,

    @Column(name = "payment_currency_code", nullable = false, columnDefinition = "CHAR(3)")
    @Comment("결제 금액 통화 코드(IOS 4217, ex : KRW, USD, EUR...)")
    var paymentCurrencyCode: String,

    @Column(name = "payment_complete_datetime", nullable = true, columnDefinition = "DATETIME(3)")
    @Comment("결제가 완료 및 확인 된 일시(Null 이라면 아직 완료 처리가 아님)")
    var paymentCompleteDatetime: LocalDateTime?,

    @Column(name = "payment_failed", nullable = false, columnDefinition = "BIT(1)")
    @Comment("결제 실패 여부")
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
    @OneToMany(
        mappedBy = "payment",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var paymentRefundList: MutableList<Db1_RaillyLinkerCompany_PaymentRefund> = mutableListOf()

    @OneToMany(
        mappedBy = "payment",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var rentableProductReservationPaymentList: MutableList<Db1_RaillyLinkerCompany_RentableProductReservationPayment> =
        mutableListOf()


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}