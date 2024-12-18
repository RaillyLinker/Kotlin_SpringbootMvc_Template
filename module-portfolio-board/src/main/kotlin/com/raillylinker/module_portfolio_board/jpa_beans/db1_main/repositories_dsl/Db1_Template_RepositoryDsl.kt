package com.raillylinker.module_portfolio_board.jpa_beans.db1_main.repositories_dsl

import com.raillylinker.module_portfolio_board.controllers.BoardController.GetBoardPageSortingDirectionEnum
import com.raillylinker.module_portfolio_board.controllers.BoardController.GetBoardPageSortingTypeEnum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

interface Db1_Template_RepositoryDsl {
    // (railly_linker_company.sample_board 테이블 페이징)
    fun findPageAllFromBoardByNotDeleted(
        sortingType: GetBoardPageSortingTypeEnum,
        sortingDirection: GetBoardPageSortingDirectionEnum,
        pageable: Pageable
    ): Page<FindPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo>

    data class FindPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo(
        var boardUid: Long = 0L,
        var title: String = "",
        var createDate: LocalDateTime = LocalDateTime.now(),
        var updateDate: LocalDateTime = LocalDateTime.now(),
        var viewCount: Long = 0L,
        var writerUserUid: Long = 0L,
        var writerUserNickname: String = "",
        var writerUserProfileFullUrl: String = ""
    )
}