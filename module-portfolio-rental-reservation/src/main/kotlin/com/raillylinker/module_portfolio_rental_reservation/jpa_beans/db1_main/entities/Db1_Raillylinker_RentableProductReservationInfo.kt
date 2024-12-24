package com.raillylinker.module_portfolio_rental_reservation.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

// Fk 관계 중 OneToOne 은 논리적 삭제를 적용할시 사용이 불가능합니다.
//     이때는, One to One 역시 Many to One 을 사용하며, 합성 Unique 로 FK 변수를 유니크 처리 한 후,
//     로직상으로 활성화된 행이 한개 뿐이라고 처리하면 됩니다.
@Entity
@Table(
    name = "rentable_product_reservation_info",
    catalog = "railly_linker_company"
)
@Comment("대여 가능 상품 예약 정보")
class Db1_Raillylinker_RentableProductReservationInfo(
    @ManyToOne
    @JoinColumn(name = "rentable_product_info_uid", nullable = false)
    @Comment("rentable_product_info 테이블 고유번호 (railly_linker_company.rentable_product_info.uid)")
    var rentableProductInfo: Db1_Raillylinker_RentableProductInfo,

    @Column(name = "customer_name", nullable = false, columnDefinition = "VARCHAR(90)")
    @Comment("예약자 이름")
    var customerName: String,

    @Column(name = "customer_phone_number", nullable = false, columnDefinition = "VARCHAR(45)")
    @Comment("예약자 전화번호(국가번호 + 전화번호)")
    var customerPhoneNumber: String,

    @Column(name = "rental_start_datetime", nullable = false, columnDefinition = "DATETIME")
    @Comment("대여가 시작되는 일시")
    var rentalStartDatetime: LocalDateTime,

    @Column(name = "rental_end_datetime", nullable = false, columnDefinition = "DATETIME")
    @Comment("대여가 끝나는 일시 (회수 시간은 포함되지 않는 순수 서비스 이용 시간)")
    var rentalEndDatetime: LocalDateTime,

    @Column(name = "payment_deadline_datetime", nullable = false, columnDefinition = "DATETIME")
    @Comment("예약 결재 기한 (예약 요청일로부터 생성, 이 시점이 지났고, payment_complete_datetime 가 충족되지 않았다면 취소로 간주)")
    var paymentDeadlineDatetime: LocalDateTime,

    @Column(name = "cancelable_deadline_datetime", nullable = false, columnDefinition = "DATETIME")
    @Comment("예약 요청일로부터 생성, 예약 취소 신청 가능 기한")
    var cancelableDeadlineDatetime: LocalDateTime,

    @Column(name = "reservation_approval_deadline_datetime", nullable = false, columnDefinition = "DATETIME")
    @Comment("관리자 승인 기한 (이 시점이 지났고, reservation_approval_datetime 가 충족되지 않았다면 취소로 간주)")
    var reservationApprovalDeadlineDatetime: LocalDateTime,


    @Column(name = "payment_complete_datetime", nullable = true, columnDefinition = "DATETIME")
    @Comment("결재 완료 일시 (deadline 보다 결재 완료 여부 판단 우선순위가 높음)")
    var paymentCompleteDatetime: LocalDateTime?,

    @Column(name = "reservation_approved_datetime", nullable = true, columnDefinition = "DATETIME")
    @Comment("예약 승인 일시 (deadline 보다 예약 승인 여부 판단 우선순위가 높음)")
    var reservationApprovedDatetime: LocalDateTime?,

    @Column(name = "reservation_approval_deny_datetime", nullable = true, columnDefinition = "DATETIME")
    @Comment("예약 거부 일시 (reservation_approved_datetime 보다 예약 거부 여부 판단 우선순위가 높음)")
    var reservationApprovalDenyDatetime: LocalDateTime?,

    @Column(name = "reservation_approval_deny_reason", nullable = false, columnDefinition = "VARCHAR(600)")
    @Comment("예약 거부 이유")
    @ColumnDefault("''")
    var reservationApprovalDenyReason: String,

    @Column(name = "refund_datetime", nullable = true, columnDefinition = "DATETIME")
    @Comment("환불 완료 일시 (환불 완료된 시점 이후에는 본 예약 상태는 결코 되돌릴 수 없음)")
    var refundDatetime: LocalDateTime?,

    @Column(name = "cancel_request_datetime", nullable = true, columnDefinition = "DATETIME")
    @Comment("예약 취소 신청 일시 (예약 취소 신청은 한 예약당 한번만 가능 = 이 컬럼이 null 이 아니라면 취소 신청 불가)")
    var cancelRequestDatetime: LocalDateTime?,

    @Column(name = "cancel_request_reason", nullable = false, columnDefinition = "VARCHAR(600)")
    @Comment("예약 취소 신청 이유")
    @ColumnDefault("''")
    var cancelRequestReason: String,

    @Column(name = "cancel_request_complete_datetime", nullable = true, columnDefinition = "DATETIME")
    @Comment("예약 취소 신청 완료 일시 (예약 취소 신청이 승인되어 취소 상태가 되었을 때의 일시, 자동 취소 간주시에는 이곳에 기입하지 않음)")
    var cancelRequestCompleteDatetime: LocalDateTime?,

    @Column(name = "cancel_request_approval_deny_datetime", nullable = true, columnDefinition = "DATETIME")
    @Comment("예약 취소 신청 거부 일시 (cancel_request_complete_datetime 보다 예약 거부 여부 판단 우선순위가 높음)")
    var cancelRequestApprovalDenyDatetime: LocalDateTime?,

    @Column(name = "cancel_request_approval_deny_reason", nullable = false, columnDefinition = "VARCHAR(600)")
    @Comment("예약 취소 신청 거부 이유")
    @ColumnDefault("''")
    var cancelRequestApprovalDenyReason: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT UNSIGNED")
    @Comment("행 고유값")
    var uid: Long? = null

    @Column(name = "row_create_date", nullable = false, columnDefinition = "DATETIME(3)")
    @CreationTimestamp
    @Comment("행 생성일(= 예약 신청일)")
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
    @OneToMany(
        mappedBy = "rentableProductReservationInfo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var rentableProductStockReservationInfoList: MutableList<Db1_Raillylinker_RentableProductStockReservationInfo> =
        mutableListOf()


    @OneToMany(
        mappedBy = "rentableProductReservationInfo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var rentableProductReservationStateChangeHistoryList: MutableList<Db1_Raillylinker_RentableProductReservationStateChangeHistory> =
        mutableListOf()
}