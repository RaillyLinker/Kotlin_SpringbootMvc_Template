package com.raillylinker.module_auth.util_components

// [커스텀 유틸 함수 모음]
interface CustomUtil {
    // (ThymeLeaf 엔진으로 랜더링 한 HTML String 을 반환)
    fun parseHtmlFileToHtmlString(justHtmlFileNameWithOutSuffix: String, variableDataMap: Map<String, Any?>): String

    // (byteArray 를 Hex String 으로 반환)
    fun bytesToHex(bytes: ByteArray): String


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}