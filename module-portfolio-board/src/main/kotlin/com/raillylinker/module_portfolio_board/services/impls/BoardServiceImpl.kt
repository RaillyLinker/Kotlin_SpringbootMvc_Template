package com.raillylinker.module_portfolio_board.services.impls

import com.raillylinker.module_portfolio_board.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.module_portfolio_board.configurations.SecurityConfig.AuthTokenFilterTotalAuth.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.module_portfolio_board.controllers.BoardController
import com.raillylinker.module_portfolio_board.services.BoardService
import com.raillylinker.module_portfolio_board.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.module_portfolio_board.jpa_beans.db1_main.repositories.*
import com.raillylinker.module_portfolio_board.jpa_beans.db1_main.repositories_dsl.Db1_Template_RepositoryDsl
import com.raillylinker.module_portfolio_board.util_components.JwtTokenUtil
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val jwtTokenUtil: JwtTokenUtil,

    // (Database Repository)
    private val db1NativeRepository: Db1_Native_Repository,
    private val db1RaillyLinkerCompanySampleBoardRepository: Db1_RaillyLinkerCompany_SampleBoard_Repository,
    private val db1RaillyLinkerCompanySampleBoardCommentRepository: Db1_RaillyLinkerCompany_SampleBoardComment_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberRepository: Db1_RaillyLinkerCompany_TotalAuthMember_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberEmailRepository: Db1_RaillyLinkerCompany_TotalAuthMemberEmail_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberPhoneRepository: Db1_RaillyLinkerCompany_TotalAuthMemberPhone_Repository,
    private val db1RaillyLinkerCompanyTotalAuthMemberProfileRepository: Db1_RaillyLinkerCompany_TotalAuthMemberProfile_Repository,

    // (Database Repository DSL)
    private val db1TemplateRepositoryDsl: Db1_Template_RepositoryDsl
) : BoardService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    override fun createBoard(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: BoardController.CreateBoardInputVo
    ): BoardController.CreateBoardOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
        return BoardController.CreateBoardOutputVo(
            1 // todo
        )
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    override fun getBoardPage(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        sortingTypeEnum: BoardController.GetBoardPageSortingTypeEnum,
        sortingDirectionEnum: BoardController.GetBoardPageSortingDirectionEnum
    ): BoardController.GetBoardPageOutputVo? {
        val pageable: Pageable = PageRequest.of(page - 1, pageElementsCount)
//        val entityList = db1TemplateTestsRepository.findAllByRowDeleteDateStrOrderByRowCreateDate(
//            "/",
//            pageable
//        )

        val boardItemVoList =
            ArrayList<BoardController.GetBoardPageOutputVo.BoardItemVo>()
//        for (entity in entityList) {
//            testEntityVoList.add(
//                BoardController.SelectRowsPageSampleOutputVo.TestEntityVo(
//                    entity.uid!!,
//                    entity.content,
//                    entity.randomNum,
//                    entity.testDatetime.atZone(ZoneId.systemDefault())
//                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
//                    entity.rowCreateDate!!.atZone(ZoneId.systemDefault())
//                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
//                    entity.rowUpdateDate!!.atZone(ZoneId.systemDefault())
//                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
//                )
//            )
//        }
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
        return BoardController.GetBoardPageOutputVo(
            1, // todo
            boardItemVoList
        )
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    override fun getBoardDetail(
        httpServletResponse: HttpServletResponse,
        boardUid: Long
    ): BoardController.GetBoardDetailOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        // todo
        return BoardController.GetBoardDetailOutputVo(
            "null",
            "null",
            "null",
            "null",
            1,
            1,
            1,
            "null"
        )
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    override fun updateBoard(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        testTableUid: Long,
        inputVo: BoardController.UpdateBoardInputVo
    ) {
//        val oldEntity = db1TemplateTestsRepository.findByUidAndRowDeleteDateStr(testTableUid, "/")
//
//        if (oldEntity == null || oldEntity.rowDeleteDateStr != "/") {
//            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
//            httpServletResponse.setHeader("api-result-code", "1")
//            return null
//        }
//
//        oldEntity.content = inputVo.content
//        oldEntity.testDatetime =
//            ZonedDateTime.parse(inputVo.dateString, DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
//                .toLocalDateTime()
//
//        val result = db1TemplateTestsRepository.save(oldEntity)
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    override fun updateBoardViewCount1Up(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        testTableUid: Long
    ) {
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    override fun deleteBoard(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        testTableUid: Long
    ) {
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    override fun createComment(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: BoardController.CreateCommentInputVo
    ): BoardController.CreateCommentOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
        return BoardController.CreateCommentOutputVo(
            1 // todo
        )
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME, readOnly = true)
    override fun getCommentPage(
        httpServletResponse: HttpServletResponse,
        boardUid: Long,
        commentUid: Long?,
        page: Int,
        pageElementsCount: Int
    ): BoardController.GetCommentPageOutputVo? {
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
        return BoardController.GetCommentPageOutputVo(
            1, // todo
            listOf()
        )
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    override fun updateComment(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        commentUid: Long,
        inputVo: BoardController.UpdateCommentInputVo
    ) {
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @Transactional(transactionManager = Db1MainConfig.TRANSACTION_NAME)
    override fun deleteComment(
        httpServletResponse: HttpServletResponse,
        authorization: String?,
        commentUid: Long
    ) {
        // todo

        httpServletResponse.status = HttpStatus.OK.value()
    }
}