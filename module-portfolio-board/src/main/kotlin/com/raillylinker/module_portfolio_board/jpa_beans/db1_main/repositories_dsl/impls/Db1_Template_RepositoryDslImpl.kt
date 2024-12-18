package com.raillylinker.module_portfolio_board.jpa_beans.db1_main.repositories_dsl.impls

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.raillylinker.module_portfolio_board.controllers.BoardController.GetBoardPageSortingDirectionEnum
import com.raillylinker.module_portfolio_board.controllers.BoardController.GetBoardPageSortingTypeEnum
import com.raillylinker.module_portfolio_board.jpa_beans.db1_main.entities.QDb1_RaillyLinkerCompany_SampleBoard
import com.raillylinker.module_portfolio_board.jpa_beans.db1_main.entities.QDb1_RaillyLinkerCompany_TotalAuthMember
import com.raillylinker.module_portfolio_board.jpa_beans.db1_main.entities.QDb1_RaillyLinkerCompany_TotalAuthMemberProfile
import com.raillylinker.module_portfolio_board.jpa_beans.db1_main.repositories_dsl.Db1_Template_RepositoryDsl
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

@Repository
class Db1_Template_RepositoryDslImpl(entityManager: EntityManager) : Db1_Template_RepositoryDsl {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)


    ////
    override fun findPageAllFromBoardByNotDeleted(
        sortingType: GetBoardPageSortingTypeEnum,
        sortingDirection: GetBoardPageSortingDirectionEnum,
        pageable: Pageable
    ): Page<Db1_Template_RepositoryDsl.FindPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo> {
        // Q 엔티티
        val sampleBoard = QDb1_RaillyLinkerCompany_SampleBoard.db1_RaillyLinkerCompany_SampleBoard
        val totalAuthMember = QDb1_RaillyLinkerCompany_TotalAuthMember.db1_RaillyLinkerCompany_TotalAuthMember
        val totalAuthMemberProfile =
            QDb1_RaillyLinkerCompany_TotalAuthMemberProfile.db1_RaillyLinkerCompany_TotalAuthMemberProfile

        // 동적 정렬 조건
        val orderBy = when (sortingType) {
            GetBoardPageSortingTypeEnum.CREATE_DATE -> sampleBoard.rowCreateDate
            GetBoardPageSortingTypeEnum.UPDATE_DATE -> sampleBoard.rowUpdateDate
            GetBoardPageSortingTypeEnum.TITLE -> sampleBoard.boardTitle
            GetBoardPageSortingTypeEnum.VIEW_COUNT -> sampleBoard.viewCount
            GetBoardPageSortingTypeEnum.WRITER_USER_NICKNAME -> totalAuthMember.accountId
        }

        val orderSpecifier = if (sortingDirection == GetBoardPageSortingDirectionEnum.ASC) {
            orderBy.asc()
        } else {
            orderBy.desc()
        }

        val query = queryFactory.select(
            Projections.bean(
                Db1_Template_RepositoryDsl.FindPageAllFromTemplateTestDataByNotDeletedWithRandomNumDistanceOutputVo::class.java,
                sampleBoard.uid.`as`("boardUid"),
                sampleBoard.boardTitle.`as`("title"),
                sampleBoard.rowCreateDate.`as`("createDate"),
                sampleBoard.rowUpdateDate.`as`("updateDate"),
                sampleBoard.viewCount.`as`("viewCount"),
                sampleBoard.totalAuthMember.uid.`as`("writerUserUid"),
                totalAuthMember.accountId.`as`("writerUserNickname"),
                totalAuthMemberProfile.imageFullUrl.`as`("writerUserProfileFullUrl")
            )
        )
            .from(sampleBoard)
            .innerJoin(totalAuthMember).on(
                totalAuthMember.rowDeleteDateStr.eq("/")
                    .and(totalAuthMember.eq(sampleBoard.totalAuthMember))
            )
            .leftJoin(totalAuthMemberProfile).on(
                totalAuthMemberProfile.rowDeleteDateStr.eq("/")
                    .and(totalAuthMemberProfile.eq(totalAuthMember.frontTotalAuthMemberProfile))
            )
            .where(sampleBoard.rowDeleteDateStr.eq("/"))
            .orderBy(orderSpecifier)

        // Pageable 처리
        val offset = pageable.pageNumber * pageable.pageSize.toLong()
        val limit = pageable.pageSize.toLong()

        val queryWithPagination = query.offset(offset).limit(limit)

        // 결과 가져오기
        val results = queryWithPagination.fetch()

        // 전체 데이터 수 가져오기
        val totalCount = queryFactory.selectFrom(sampleBoard)
            .where(sampleBoard.rowDeleteDateStr.eq("/"))
            .fetchCount()

        return PageImpl(results, pageable, totalCount)
    }
}