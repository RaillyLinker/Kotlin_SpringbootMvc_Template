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
    name = "rentable_product_stock_info",
    catalog = "railly_linker_company"
)
@Comment("대여 가능 상품 재고 정보")
class Db1_RaillyLinkerCompany_RentableProductStockInfo(
    @ManyToOne
    @JoinColumn(name = "rentable_product_stock_category_uid", nullable = true)
    @Comment("rentable_product_stock_category 테이블 고유번호 (railly_linker_company.rentable_product_stock_category.uid)")
    var rentableProductStockCategory: Db1_RaillyLinkerCompany_RentableProductStockCategory?,

    @ManyToOne
    @JoinColumn(name = "front_rentable_product_stock_image_uid", nullable = true)
    @Comment("개별 상품 대표 이미지 rentable_product_stock_image 테이블 고유번호 (railly_linker_company.rentable_product_stock_image.uid)")
    var frontRentableProductStockImage: Db1_RaillyLinkerCompany_RentableProductStockImage?,

    @ManyToOne
    @JoinColumn(name = "rentable_product_info_uid", nullable = false)
    @Comment("rentable_product_info 테이블 고유번호 (railly_linker_company.rentable_product_info.uid)")
    var rentableProductInfo: Db1_RaillyLinkerCompany_RentableProductInfo,

    @Column(name = "product_desc", nullable = false, columnDefinition = "VARCHAR(3000)")
    @Comment("대여 가능 상품 개별 설명")
    var productDesc: String,

    @Column(name = "first_rentable_datetime", nullable = false, columnDefinition = "DATETIME")
    @Comment("제품 대여(손님에게 제공)가 가능한 최초 일시")
    var firstRentableDatetime: LocalDateTime,

    @Column(name = "last_rentable_datetime", nullable = true, columnDefinition = "DATETIME")
    @Comment("제품 대여 마지막 일시 (이때가 대여 마지막 날)")
    var lastRentableDatetime: LocalDateTime?,

    @Column(name = "now_reservable", nullable = false, columnDefinition = "BIT(1)")
    @Comment("상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정")
    var nowReservable: Boolean
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
    // 상품 정보 삭제시 이미지 삭제
    @OneToMany(
        mappedBy = "rentableProductStockInfo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var rentableProductStockImageList: MutableList<Db1_RaillyLinkerCompany_RentableProductStockImage> =
        mutableListOf()

    // 상품 정보 삭제시 상품 예약 내역 삭제
    @OneToMany(
        mappedBy = "rentableProductStockInfo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var rentableProductStockReservationInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductStockReservationInfo> =
        mutableListOf()
}