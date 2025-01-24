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
    name = "storage_folder_info",
    catalog = "railly_linker_company",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["total_auth_member_uid", "parent_storage_folder_info_uid_nn", "folder_name"])
    ]
)
@Comment("스토리지 폴더 정보")
class Db1_RaillyLinkerCompany_StorageFolderInfo(
    @ManyToOne
    @JoinColumn(name = "total_auth_member_uid", nullable = false)
    @Comment("멤버 고유번호(railly_linker_company.total_auth_member.uid)")
    var totalAuthMember: Db1_RaillyLinkerCompany_TotalAuthMember,

    @ManyToOne
    @JoinColumn(name = "parent_storage_folder_info_uid", nullable = true)
    @Comment("부모 폴더 정보 고유값 (railly_linker_company.storage_folder_info.uid)")
    var parentStorageFolderInfo: Db1_RaillyLinkerCompany_StorageFolderInfo?,

    @Column(name = "parent_storage_folder_info_uid_nn", nullable = false, columnDefinition = "BIGINT")
    @Comment(
        "부모 폴더 정보 고유값 (railly_linker_company.storage_folder_info.uid) Not Null, " +
                "parent_storage_folder_info_uid 와 동일하고, null 이라면 0 으로 처리합니다. " +
                "null 값은 unique 처리시 서로 다른 null 을 다르다고 판단하기에 올바른 제약 적용을 위한 처리."
    )
    @ColumnDefault("0")
    var parentStorageFolderInfoUidNn: Long,

    @Column(name = "folder_name", nullable = false, columnDefinition = "VARCHAR(45)")
    @Comment("폴더 이름")
    var folderName: String
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


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    // 상위 카테고리가 삭제되면 하위 카테고리도 삭제
    @OneToMany(
        mappedBy = "parentStorageFolderInfo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var childStorageFolderInfoList: MutableList<Db1_RaillyLinkerCompany_StorageFolderInfo> = mutableListOf()

    @OneToMany(
        mappedBy = "storageFolderInfo",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var storageFileInfoList: MutableList<Db1_RaillyLinkerCompany_StorageFileInfo> = mutableListOf()
}