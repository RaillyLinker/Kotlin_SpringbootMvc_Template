package com.raillylinker.services

import com.raillylinker.jpa_beans.db1_main.repositories.*
import com.raillylinker.util_components.JwtTokenUtil
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class PaymentService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val jwtTokenUtil: JwtTokenUtil,
    private val db1RaillyLinkerCompanyTotalAuthMemberRepository: Db1_RaillyLinkerCompany_TotalAuthMember_Repository,

    private val db1RaillyLinkerCompanyPaymentRequestRepository: Db1_RaillyLinkerCompany_PaymentRequest_Repository,
    private val db1RaillyLinkerCompanyPaymentRefundRequestRepository: Db1_RaillyLinkerCompany_PaymentRefundRequest_Repository,
    private val db1RaillyLinkerCompanyPaymentRequestDetailBankTransferRepository: Db1_RaillyLinkerCompany_PaymentRequestDetailBankTransfer_Repository,
    private val db1RaillyLinkerCompanyPaymentRequestDetailTossPaymentsRepository: Db1_RaillyLinkerCompany_PaymentRequestDetailTossPayments_Repository
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // (현 프로젝트 동작 서버의 외부 접속 주소)
    // 프로필 이미지 로컬 저장 및 다운로드 주소 지정을 위해 필요
    // !!!프로필별 접속 주소 설정하기!!
    // ex : http://127.0.0.1:8080
    private val externalAccessAddress: String
        get() {
            return when (activeProfile) {
                "prod80" -> {
                    "http://127.0.0.1"
                }

                "dev8080" -> {
                    "http://127.0.0.1:8080"
                }

                else -> {
                    "http://127.0.0.1:8080"
                }
            }
        }


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (비 로그인 접속 테스트)
    fun noLoggedInAccessTest(httpServletResponse: HttpServletResponse): String? {
        httpServletResponse.status = HttpStatus.OK.value()
        return externalAccessAddress
    }
}