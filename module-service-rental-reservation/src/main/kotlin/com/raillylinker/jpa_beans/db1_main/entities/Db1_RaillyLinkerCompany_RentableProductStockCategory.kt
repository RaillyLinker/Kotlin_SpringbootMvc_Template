package com.raillylinker.jpa_beans.db1_main.entities

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
    name = "rentable_product_stock_category",
    catalog = "railly_linker_company"
)
@Comment("대여 가능 상품 재고 카테고리")
class Db1_RaillyLinkerCompany_RentableProductStockCategory(
    @ManyToOne
    @JoinColumn(name = "parent_rentable_product_stock_category_uid", nullable = true)
    @Comment("부모 카테고리 rentable_product_stock_category 테이블 고유번호 (railly_linker_company.rentable_product_stock_category.uid)")
    var parentRentableProductStockCategory: Db1_RaillyLinkerCompany_RentableProductStockCategory?,

    @Column(name = "category_name", nullable = false, columnDefinition = "VARCHAR(90)")
    @Comment("카테고리 이름")
    var categoryName: String
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
    // 상위 카테고리 삭제시 하위 카테고리 삭제
    @OneToMany(
        mappedBy = "parentRentableProductStockCategory",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var childRentableProductStockCategoryList: MutableList<Db1_RaillyLinkerCompany_RentableProductStockCategory> =
        mutableListOf()

    // 카테고리 삭제시 카테고리 설정 null 처리
    @OneToMany(
        mappedBy = "rentableProductStockCategory",
        fetch = FetchType.LAZY
    )
    var rentableProductStockInfoList: MutableList<Db1_RaillyLinkerCompany_RentableProductStockInfo> = mutableListOf()
}