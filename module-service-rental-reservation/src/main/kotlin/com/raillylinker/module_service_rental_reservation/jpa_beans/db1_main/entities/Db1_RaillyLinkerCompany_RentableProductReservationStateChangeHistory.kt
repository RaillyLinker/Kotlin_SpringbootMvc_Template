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
    name = "rentable_product_reservation_state_change_history",
    catalog = "railly_linker_company"
)
@Comment("대여 가능 상품 예약 상태 변경 히스토리")
class Db1_RaillyLinkerCompany_RentableProductReservationStateChangeHistory(
    @ManyToOne
    @JoinColumn(name = "rentable_product_reservation_info_uid", nullable = false)
    @Comment("rentable_product_reservation_info 테이블 고유번호 (railly_linker_company.rentable_product_reservation_info.uid)")
    var rentableProductReservationInfo: Db1_RaillyLinkerCompany_RentableProductReservationInfo,

    @Column(name = "state_code", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    @Comment("예약 상태 코드 (1 : 예약 신청, 2 : 결재 완료, 3 : 예약 승인, 4 : 예약 거부, 5 : 예약 취소 신청, 6 : 예약 취소 승인, 7 : 예약 취소 거부, 8 : 예약 취소 후 환불 완료, 9 : 조기 반납)")
    var stateCode: Int,

    @Column(name = "state_change_desc", nullable = false, columnDefinition = "VARCHAR(600)")
    @Comment("상태 변경 상세")
    var stateDesc: String
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
    // <중첩 클래스 공간>

}