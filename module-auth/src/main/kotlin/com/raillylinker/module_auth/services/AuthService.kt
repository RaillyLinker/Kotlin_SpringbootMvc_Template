package com.raillylinker.module_auth.services

import com.raillylinker.module_auth.controllers.AuthController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity

interface AuthService {
    // (비 로그인 접속 테스트)
    fun noLoggedInAccessTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (로그인 진입 테스트 <>)
    fun loggedInAccessTest(httpServletResponse: HttpServletResponse, authorization: String): String?


    ////
    // (ADMIN 권한 진입 테스트 <'ADMIN'>)
    fun adminAccessTest(httpServletResponse: HttpServletResponse, authorization: String): String?


    ////
    // (Developer 권한 진입 테스트 <'ADMIN' or 'Developer'>)
    fun developerAccessTest(httpServletResponse: HttpServletResponse, authorization: String): String?


    ////
    // (특정 회원의 발행된 Access 토큰 만료 처리)
    fun doExpireAccessToken(
        httpServletResponse: HttpServletResponse,
        memberUid: Long,
        inputVo: AuthController.DoExpireAccessTokenInputVo
    )


    ////
    // (계정 비밀번호 로그인)
    fun loginWithPassword(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.LoginWithPasswordInputVo
    ): AuthController.LoginOutputVo?


    ////
    // (OAuth2 Code 로 OAuth2 AccessToken 발급)
    fun getOAuth2AccessToken(
        httpServletResponse: HttpServletResponse,
        oauth2TypeCode: Int,
        oauth2Code: String
    ): AuthController.GetOAuth2AccessTokenOutputVo?


    ////
    // (OAuth2 로그인 (Access Token))
    fun loginWithOAuth2AccessToken(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.LoginWithOAuth2AccessTokenInputVo
    ): AuthController.LoginOutputVo?


    ////
    // (OAuth2 로그인 (ID Token))
    fun loginWithOAuth2IdToken(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.LoginWithOAuth2IdTokenInputVo
    ): AuthController.LoginOutputVo?


    ////
    // (로그아웃 처리 <>)
    fun logout(authorization: String, httpServletResponse: HttpServletResponse)


    ////
    // (토큰 재발급 <>)
    fun reissueJwt(
        authorization: String?,
        inputVo: AuthController.ReissueJwtInputVo,
        httpServletResponse: HttpServletResponse
    ): AuthController.LoginOutputVo?


    ////
    // (멤버의 현재 발행된 모든 토큰 비활성화 (= 모든 기기에서 로그아웃) <>)
    fun deleteAllJwtOfAMember(authorization: String, httpServletResponse: HttpServletResponse)


    ////
    // (회원 정보 가져오기 <>)
    fun getMemberInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): AuthController.GetMemberInfoOutputVo?


    ////
    // (아이디 중복 검사)
    fun checkIdDuplicate(
        httpServletResponse: HttpServletResponse,
        id: String
    ): AuthController.CheckIdDuplicateOutputVo?


    ////
    // (아이디 수정하기 <>)
    fun updateId(httpServletResponse: HttpServletResponse, authorization: String, id: String)


    ////
    // (테스트 회원 회원가입)
    fun joinTheMembershipForTest(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.JoinTheMembershipForTestInputVo
    )


    ////
    // (이메일 회원가입 본인 인증 이메일 발송)
    fun sendEmailVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.SendEmailVerificationForJoinInputVo
    ): AuthController.SendEmailVerificationForJoinOutputVo?


    ////
    // (이메일 회원가입 본인 확인 이메일에서 받은 코드 검증하기)
    fun checkEmailVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        email: String,
        verificationCode: String
    )


    ////
    // (이메일 회원가입)
    fun joinTheMembershipWithEmail(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.JoinTheMembershipWithEmailInputVo
    )


    ////
    // (전화번호 회원가입 본인 인증 문자 발송)
    fun sendPhoneVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.SendPhoneVerificationForJoinInputVo
    ): AuthController.SendPhoneVerificationForJoinOutputVo?


    ////
    // (전화번호 회원가입 본인 확인 문자에서 받은 코드 검증하기)
    fun checkPhoneVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        phoneNumber: String,
        verificationCode: String
    )


    ////
    // (전화번호 회원가입)
    fun joinTheMembershipWithPhoneNumber(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.JoinTheMembershipWithPhoneNumberInputVo
    )


    ////
    // (OAuth2 AccessToken 으로 회원가입 검증)
    fun checkOauth2AccessTokenVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.CheckOauth2AccessTokenVerificationForJoinInputVo
    ): AuthController.CheckOauth2AccessTokenVerificationForJoinOutputVo?


    ////
    // (OAuth2 IdToken 으로 회원가입 검증)
    fun checkOauth2IdTokenVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.CheckOauth2IdTokenVerificationForJoinInputVo
    ): AuthController.CheckOauth2IdTokenVerificationForJoinOutputVo?


    ////
    // (OAuth2 회원가입)
    fun joinTheMembershipWithOauth2(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.JoinTheMembershipWithOauth2InputVo
    )


    ////
    // (계정 비밀번호 변경 <>)
    fun updateAccountPassword(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: AuthController.UpdateAccountPasswordInputVo
    )


    ////
    // (이메일 비밀번호 찾기 본인 인증 이메일 발송)
    fun sendEmailVerificationForFindPassword(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.SendEmailVerificationForFindPasswordInputVo
    ): AuthController.SendEmailVerificationForFindPasswordOutputVo?


    ////
    // (이메일 비밀번호 찾기 본인 확인 이메일에서 받은 코드 검증하기)
    fun checkEmailVerificationForFindPassword(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        email: String,
        verificationCode: String
    )


    ////
    // (이메일 비밀번호 찾기 완료)
    fun findPasswordWithEmail(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.FindPasswordWithEmailInputVo
    )


    ////
    // (전화번호 비밀번호 찾기 본인 인증 문자 발송)
    fun sendPhoneVerificationForFindPassword(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.SendPhoneVerificationForFindPasswordInputVo
    ): AuthController.SendPhoneVerificationForFindPasswordOutputVo?


    ////
    // (전화번호 비밀번호 찾기 본인 확인 문자에서 받은 코드 검증하기)
    fun checkPhoneVerificationForFindPassword(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        phoneNumber: String,
        verificationCode: String
    )


    ////
    // (전화번호 비밀번호 찾기 완료)
    fun findPasswordWithPhoneNumber(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.FindPasswordWithPhoneNumberInputVo
    )


    ////
    // (내 이메일 리스트 가져오기 <>)
    fun getMyEmailList(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): AuthController.GetMyEmailListOutputVo?


    ////
    // (내 전화번호 리스트 가져오기 <>)
    fun getMyPhoneNumberList(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): AuthController.GetMyPhoneNumberListOutputVo?


    ////
    // (내 OAuth2 로그인 리스트 가져오기 <>)
    fun getMyOauth2List(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): AuthController.GetMyOauth2ListOutputVo?


    ////
    // (이메일 추가하기 본인 인증 이메일 발송 <>)
    fun sendEmailVerificationForAddNewEmail(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.SendEmailVerificationForAddNewEmailInputVo,
        authorization: String
    ): AuthController.SendEmailVerificationForAddNewEmailOutputVo?


    ////
    // (이메일 추가하기 본인 확인 이메일에서 받은 코드 검증하기 <>)
    fun checkEmailVerificationForAddNewEmail(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        email: String,
        verificationCode: String,
        authorization: String
    )


    ////
    // (이메일 추가하기 <>)
    fun addNewEmail(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.AddNewEmailInputVo,
        authorization: String
    ): AuthController.AddNewEmailOutputVo?


    ////
    // (내 이메일 제거하기 <>)
    fun deleteMyEmail(
        httpServletResponse: HttpServletResponse,
        emailUid: Long,
        authorization: String
    )


    ////
    // (전화번호 추가하기 본인 인증 문자 발송 <>)
    fun sendPhoneVerificationForAddNewPhoneNumber(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.SendPhoneVerificationForAddNewPhoneNumberInputVo,
        authorization: String
    ): AuthController.SendPhoneVerificationForAddNewPhoneNumberOutputVo?


    ////
    // (전화번호 추가하기 본인 확인 문자에서 받은 코드 검증하기 <>)
    fun checkPhoneVerificationForAddNewPhoneNumber(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        phoneNumber: String,
        verificationCode: String,
        authorization: String
    )


    ////
    // (전화번호 추가하기 <>)
    fun addNewPhoneNumber(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.AddNewPhoneNumberInputVo,
        authorization: String
    ): AuthController.AddNewPhoneNumberOutputVo?


    ////
    // (내 전화번호 제거하기 <>)
    fun deleteMyPhoneNumber(
        httpServletResponse: HttpServletResponse,
        phoneUid: Long,
        authorization: String
    )


    ////
    // (OAuth2 추가하기 (Access Token) <>)
    fun addNewOauth2WithAccessToken(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.AddNewOauth2WithAccessTokenInputVo,
        authorization: String
    )


    ////
    // (OAuth2 추가하기 (Id Token) <>)
    fun addNewOauth2WithIdToken(
        httpServletResponse: HttpServletResponse,
        inputVo: AuthController.AddNewOauth2WithIdTokenInputVo,
        authorization: String
    )


    ////
    // (내 OAuth2 제거하기 <>)
    fun deleteMyOauth2(
        httpServletResponse: HttpServletResponse,
        oAuth2Uid: Long,
        authorization: String
    )


    ////
    // (회원탈퇴 <>)
    fun withdrawalMembership(
        httpServletResponse: HttpServletResponse,
        authorization: String
    )


    ////
    // (내 Profile 이미지 정보 리스트 가져오기 <>)
    fun getMyProfileList(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): AuthController.GetMyProfileListOutputVo?


    ////
    // (내 대표 Profile 이미지 정보 가져오기 <>)
    fun getMyFrontProfile(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): AuthController.GetMyFrontProfileOutputVo?


    ////
    // (내 대표 프로필 설정하기 <>)
    fun setMyFrontProfile(httpServletResponse: HttpServletResponse, authorization: String, profileUid: Long?)


    ////
    // (내 프로필 삭제 <>)
    fun deleteMyProfile(authorization: String, httpServletResponse: HttpServletResponse, profileUid: Long)


    ////
    // (내 프로필 이미지 추가 <>)
    fun addNewProfile(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: AuthController.AddNewProfileInputVo
    ): AuthController.AddNewProfileOutputVo?


    ////
    // (by_product_files/member/profile 폴더에서 파일 다운받기)
    fun downloadProfileFile(
        httpServletResponse: HttpServletResponse,
        fileName: String
    ): ResponseEntity<Resource>?


    ////
    // (내 대표 이메일 정보 가져오기 <>)
    fun getMyFrontEmail(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): AuthController.GetMyFrontEmailOutputVo?


    ////
    // (내 대표 이메일 설정하기 <>)
    fun setMyFrontEmail(httpServletResponse: HttpServletResponse, authorization: String, emailUid: Long?)


    ////
    // (내 대표 전화번호 정보 가져오기 <>)
    fun getMyFrontPhoneNumber(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): AuthController.GetMyFrontPhoneNumberOutputVo?


    ////
    // (내 대표 전화번호 설정하기 <>)
    fun setMyFrontPhoneNumber(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        phoneNumberUid: Long?
    )


    ////
    // (Redis Key-Value 모두 조회 테스트)
    fun selectAllRedisKeyValueSample(httpServletResponse: HttpServletResponse): AuthController.SelectAllRedisKeyValueSampleOutputVo?
}