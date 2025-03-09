package com.raillylinker.web_socket_stomp_src

import java.security.Principal

class StompPrincipalVo(
    // userName (여기선 유저 고유번호)
    private var name: String,
    // 유저 권한 리스트
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