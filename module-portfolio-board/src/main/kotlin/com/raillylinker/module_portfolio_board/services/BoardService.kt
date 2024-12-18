package com.raillylinker.module_portfolio_board.services

import com.raillylinker.module_portfolio_board.controllers.BoardController
import jakarta.servlet.http.HttpServletResponse

interface BoardService {
    // (게시글 입력 API)
    fun createBoard(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: BoardController.CreateBoardInputVo
    ): BoardController.CreateBoardOutputVo?


    ////
    // (게시글 리스트 (페이징))
    fun getBoardPage(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        sortingTypeEnum: BoardController.GetBoardPageSortingTypeEnum,
        sortingDirectionEnum: BoardController.GetBoardPageSortingDirectionEnum
    ): BoardController.GetBoardPageOutputVo?


    ////
    // (게시판 상세 화면)
    fun getBoardDetail(
        httpServletResponse: HttpServletResponse,
        boardUid: Long
    ): BoardController.GetBoardDetailOutputVo?


    ////
    // (게시글 수정)
    fun updateBoard(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        testTableUid: Long,
        inputVo: BoardController.UpdateBoardInputVo
    )


    ////
    // (게시글 조회수 1 상승)
    fun updateBoardViewCount1Up(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        testTableUid: Long
    )


    ////
    // (게시글 삭제)
    fun deleteBoard(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        testTableUid: Long
    )


    // (댓글 입력 API)
    fun createComment(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: BoardController.CreateCommentInputVo
    ): BoardController.CreateCommentOutputVo?


    ////
    // (댓글 리스트 (페이징))
    fun getCommentPage(
        httpServletResponse: HttpServletResponse,
        boardUid: Long,
        commentUid: Long?,
        page: Int,
        pageElementsCount: Int
    ): BoardController.GetCommentPageOutputVo?


    ////
    // (댓글 수정)
    fun updateComment(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        commentUid: Long,
        inputVo: BoardController.UpdateCommentInputVo
    )


    ////
    // (댓글 삭제)
    fun deleteComment(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        commentUid: Long
    )
}