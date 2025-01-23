package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_RaillyLinkerCompany_StorageFolderInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

// (JPA 레포지토리)
// : 함수 작성 명명법에 따라 데이터베이스 SQL 동작을 자동지원
@Repository
interface Db1_RaillyLinkerCompany_StorageFolderInfo_Repository :
    JpaRepository<Db1_RaillyLinkerCompany_StorageFolderInfo, Long> {
    fun findByUidAndRowDeleteDateStr(
        uid: Long,
        rowDeleteDateStr: String
    ): Db1_RaillyLinkerCompany_StorageFolderInfo?

    
    // ----
    // (특정 폴더에서 모든 하위 폴더들을 탐색합니다.)
    @Query(
        nativeQuery = true,
        value = """
            WITH RECURSIVE StorageFolderTree AS (
                SELECT 
                storage_folder_info.*, 
                0 AS depth
                FROM railly_linker_company.storage_folder_info as storage_folder_info
                WHERE 
                storage_folder_info.uid = :storageFolderUid and 
                storage_folder_info.row_delete_date_str = "/"
                UNION ALL
                SELECT 
                child.*, 
                parent.depth + 1 AS depth
                FROM railly_linker_company.storage_folder_info as child
                INNER JOIN 
                StorageFolderTree as parent 
                ON 
                child.parent_storage_folder_info_uid = parent.uid and 
                parent.row_delete_date_str = "/"
                WHERE 
                child.row_delete_date_str = "/"
            )
            SELECT 
            StorageFolderTree.uid
            FROM 
            StorageFolderTree
            ORDER BY 
            depth DESC
            """
    )
    fun findAllStorageFolderTreeUidList(
        @Param(value = "storageFolderUid") storageFolderUid: Long
    ): List<FindAllStorageFolderTreeUidListOutputVo>

    interface FindAllStorageFolderTreeUidListOutputVo {
        var uid: Long
    }
}