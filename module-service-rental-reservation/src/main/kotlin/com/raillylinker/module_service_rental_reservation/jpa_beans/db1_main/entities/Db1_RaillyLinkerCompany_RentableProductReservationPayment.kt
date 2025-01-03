package com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities

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
    name = "rentable_product_reservation_payment",
    catalog = "railly_linker_company"
)
@Comment("대여 가능 상품 예약 결제 정보(결제 모듈과 예약 모듈을 분리하기 위해 두 테이블을 연결하는 브릿지 테이블)")
class Db1_RaillyLinkerCompany_RentableProductReservationPayment(
    @ManyToOne
    @JoinColumn(name = "rentable_product_reservation_info_uid", nullable = false)
    @Comment("rentable_product_reservation_info 테이블 고유번호 (railly_linker_company.rentable_product_reservation_info.uid)")
    var rentableProductReservationInfo: Db1_RaillyLinkerCompany_RentableProductReservationInfo,

    @ManyToOne
    @JoinColumn(name = "payment_uid", nullable = false)
    @Comment("payment 테이블 고유번호 (railly_linker_company.payment.uid)")
    var payment: Db1_RaillyLinkerCompany_Payment
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
}