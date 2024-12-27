package com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
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

    @Column(name = "reservation_unit_price", nullable = false, columnDefinition = "DECIMAL(15, 2) UNSIGNED")
    @Comment("단위 예약 시간에 대한 가격 (예약 시간 / 단위 예약 시간 * 예약 단가 = 예약 최종가)")
    var reservationUnitPrice: BigDecimal,

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

    @Column(name = "update_version_seq", nullable = false, columnDefinition = "INT UNSIGNED")
    @ColumnDefault("0")
    @Comment("업데이트 버전 시퀀스 (0에서 시작해서 정보 업데이트가 될 때마다 1 씩 올라갑니다. 예약 프로세스 진행중 버전 정보가 맞지 않으면 진행이 불가능하게 할 것.)")
    var updateVersionSeq: Long = 0


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    // 상품 정보 삭제시 이미지 정보도 삭제
    @OneToMany(
        mappedBy = "rentableProductInfo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var rentableProductImageList: MutableList<Db1_RaillyLinkerCompany_RentableProductImage> =
        mutableListOf()

    // 상품 정보 삭제시 재고 정보도 삭제
    @OneToMany(
        mappedBy = "rentableProductInfo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var rentableProductStockInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductStockInfo> = mutableListOf()

    // 상품 정보 삭제시 예약 내역에서 null 처리
    @OneToMany(
        mappedBy = "rentableProductInfo",
        fetch = FetchType.LAZY
    )
    var rentableProductReservationInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductReservationInfo> =
        mutableListOf()
}