package com.raillylinker.web_socket_stomp_src

import java.security.Principal

class StompPrincipalVo(
    // userName (회원 : usr_${유저 고유번호}, 비회원 : nusr_${sessionId})
    private var name: String,
    // 유저 권한 리스트 (비회원일 때는 Empty List)
    private var roleList: List<String>
) : Principal {
    override fun getName(): String = name
    fun setName(name: String) {
        this.name = name
    }

    fun getRoleList(): List<String> = roleList
    fun setRoleList(roleList: List<String>) {
        this.roleList = roleList
    }
}