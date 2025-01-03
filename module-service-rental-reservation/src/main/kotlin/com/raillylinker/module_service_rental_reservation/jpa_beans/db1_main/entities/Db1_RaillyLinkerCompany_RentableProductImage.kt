package com.raillylinker.module_service_rental_reservation.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "rentable_product_image",
    catalog = "railly_linker_company"
)
@Comment("대여 가능 상품 이미지 테이블")
class Db1_RaillyLinkerCompany_RentableProductImage(
    @ManyToOne
    @JoinColumn(name = "rentable_product_info_uid", nullable = false)
    @Comment("rentable_product_info 테이블 고유번호 (railly_linker_company.rentable_product_info.uid)")
    var rentableProductInfo: Db1_RaillyLinkerCompany_RentableProductInfo,

    @Column(name = "image_full_url", nullable = false, columnDefinition = "VARCHAR(200)")
    @Comment("상품 이미지 Full URL")
    var imageFullUrl: String
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
    // 이미지 삭제시 이미지 설정을 null 로 변경
    @OneToMany(
        mappedBy = "frontRentableProductImage",
        fetch = FetchType.LAZY
    )
    var rentableProductInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductInfo> =
        mutableListOf()

    // 이미지 삭제시 이미지 설정을 null 로 변경
    @OneToMany(
        mappedBy = "frontRentableProductImage",
        fetch = FetchType.LAZY
    )
    var rentableProductReservationInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductReservationInfo> =
        mutableListOf()
}