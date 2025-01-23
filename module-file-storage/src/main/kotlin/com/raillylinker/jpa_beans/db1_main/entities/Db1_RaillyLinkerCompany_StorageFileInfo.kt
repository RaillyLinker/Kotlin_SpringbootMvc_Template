package com.raillylinker.jpa_beans.db1_main.entities

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

// Fk 관계 중 OneToOne 은 논리적 삭제를 적용할시 사용이 불가능합니다.
//     이때는, One to One 역시 Many to One 을 사용하며, 합성 Unique 로 FK 변수를 유니크 처리 한 후,
//     로직상으로 활성화된 행이 한개 뿐이라고 처리하면 됩니다.
@Entity
@Table(
    name = "storage_file_info",
    catalog = "railly_linker_company",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["storage_folder_info_uid", "file_name"])
    ]
)
@Comment("스토리지 파일 정보")
class Db1_RaillyLinkerCompany_StorageFileInfo(
    @ManyToOne
    @JoinColumn(name = "storage_folder_info_uid", nullable = false)
    @Comment("폴더 정보 고유값 (railly_linker_company.storage_folder_info.uid)")
    var storageFolderInfo: Db1_RaillyLinkerCompany_StorageFolderInfo,

    @Column(name = "file_name", nullable = false, columnDefinition = "VARCHAR(45)")
    @Comment("파일명")
    var fileName: String,

    @Column(name = "file_server_address", nullable = false, columnDefinition = "VARCHAR(30)")
    @Comment("파일이 저장된 서버의 주소 (ex : https://my-server or http://127.0.0.1:8080)")
    var fileServerAddress: String,

    @Column(name = "file_secret_code", nullable = true, columnDefinition = "VARCHAR(30)")
    @Comment("파일 다운로드 시크릿 코드(이 값이 null 이 아니라면, 본 파일을 다운로드 하기 위해 시크릿 코드가 필요합니다.)")
    var fileSecretCode: String?
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
}