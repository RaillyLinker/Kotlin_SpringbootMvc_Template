package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(
    name = "rentable_product_stock_image",
    catalog = "railly_linker_company"
)
@Comment("대여 가능 상품 재고 이미지 테이블")
class Db1_RaillyLinkerCompany_RentableProductStockImage(
    @ManyToOne
    @JoinColumn(name = "rentable_product_stock_info_uid", nullable = false)
    @Comment("rentable_product_stock_info 테이블 고유번호 (railly_linker_company.rentable_product_stock_info.uid)")
    var rentableProductStockInfo: Db1_RaillyLinkerCompany_RentableProductStockInfo,

    @Column(name = "image_full_url", nullable = false, columnDefinition = "VARCHAR(200)")
    @Comment("상품 이미지 Full URL")
    var imageFullUrl: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT")
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
    // 이미지 삭제시 이미지 설정 null 처리
    @OneToMany(
        mappedBy = "frontRentableProductStockImage",
        fetch = FetchType.LAZY
    )
    var rentableProductStockInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductStockInfo> =
        mutableListOf()
}