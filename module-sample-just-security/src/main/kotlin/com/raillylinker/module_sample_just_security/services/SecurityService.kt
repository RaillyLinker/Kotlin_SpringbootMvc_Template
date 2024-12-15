package com.raillylinker.module_sample_just_security.services

import jakarta.servlet.http.HttpServletResponse

interface SecurityService {
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
}