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
    name = "rentable_product_info",
    catalog = "railly_linker_company"
)
@Comment("대여 가능 상품 정보")
class Db1_RaillyLinkerCompany_RentableProductInfo(
    @Column(name = "product_name", nullable = false, columnDefinition = "VARCHAR(90)")
    @Comment("고객에게 보일 상품명")
    var productName: String,

    @ManyToOne
    @JoinColumn(name = "rentable_product_category_uid", nullable = true)
    @Comment("rentable_product_category 테이블 고유번호 (railly_linker_company.rentable_product_category.uid)")
    var rentableProductCategory: Db1_RaillyLinkerCompany_RentableProductCategory?,

    @Column(name = "product_intro", nullable = false, columnDefinition = "VARCHAR(6000)")
    @Comment("고객에게 보일 상품 소개")
    var productIntro: String,

    @ManyToOne
    @JoinColumn(name = "front_rentable_product_image_uid", nullable = true)
    @Comment("상품 대표 이미지 rentable_product_image 테이블 고유번호 (railly_linker_company.rentable_product_image.uid)")
    var frontRentableProductImage: Db1_RaillyLinkerCompany_RentableProductImage?,

    @Column(name = "address_country", nullable = false, columnDefinition = "VARCHAR(60)")
    @Comment("상품이 위치한 주소 (대여 가능 위치의 기준으로 사용됨) - 국가")
    var addressCountry: String,

    @Column(name = "address_main", nullable = false, columnDefinition = "VARCHAR(300)")
    @Comment("상품이 위치한 주소(대여 가능 위치의 기준으로 사용됨) - 국가와 상세 주소를 제외")
    var addressMain: String,

    @Column(name = "address_detail", nullable = false, columnDefinition = "VARCHAR(300)")
    @Comment("상품이 위치한 주소(대여 가능 위치의 기준으로 사용됨) - 상세")
    var addressDetail: String,

    @Column(name = "first_reservable_datetime", nullable = false, columnDefinition = "DATETIME")
    @Comment("상품 예약이 가능한 최초 일시(콘서트 티켓 예매와 같은 서비스를 가정, 예약 러시 처리가 필요)")
    var firstReservableDatetime: LocalDateTime,

    @Column(name = "reservation_unit_minute", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @Comment("예약 추가 할 수 있는 최소 시간 단위 (분)")
    var reservationUnitMinute: Long,

    @Column(name = "minimum_reservation_unit_count", nullable = false, columnDefinition = "INT UNSIGNED")
    @Comment("단위 예약 시간을 대여일 기준에서 최소 몇번 추가 해야 하는지")
    var minimumReservationUnitCount: Long,

    @Column(name = "maximum_reservation_unit_count", nullable = true, columnDefinition = "INT UNSIGNED")
    @Comment("단위 예약 시간을 대여일 기준에서 최대 몇번 추가 가능한지 (Null 이라면 제한 없음)")
    var maximumReservationUnitCount: Long?,

    @Column(name = "reservation_unit_price", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @Comment("단위 예약 시간에 대한 가격 (예약 시간 / 단위 예약 시간 * 예약 단가 = 예약 최종가)")
    var reservationUnitPrice: Long,

    @Column(name = "preparation_minute", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @Comment(
        "대여 상품 반납일시로부터 다음 대여까지 걸리는 시간. " +
                "(분, 회수 시간, 준비 시간, 평균 지연 시간을 포함하여 반납 후 다음 대여 사이의 시간, " +
                "식당 예약으로 치면, 고객이 식사를 마치고 나온 이후 식탁 정리 등 테이블 세팅에 들어가는 시간. " +
                "이 시간을 기반으로 다음 대여 가능 시간을 가늠하므로 다음 대여한 고객에게 피해를 끼치지 않도록 넉넉히 설정할것)"
    )
    var preparationMinute: Long,

    @Column(name = "now_reservable", nullable = false, columnDefinition = "BIT(1)")
    @Comment("재고, 상품 상태와 상관 없이 현 시점 예약 가능한지에 대한 관리자의 설정 = 활성/비활성 플래그")
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
    @OneToMany(
        mappedBy = "rentableProductInfo",
        fetch = FetchType.LAZY
    )
    var rentableProductStockInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductStockInfo> = mutableListOf()

    @OneToMany(
        mappedBy = "rentableProductInfo",
        fetch = FetchType.LAZY
    )
    var rentableProductReservationInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductReservationInfo> =
        mutableListOf()
}