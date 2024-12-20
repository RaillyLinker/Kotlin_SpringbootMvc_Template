package com.raillylinker.module_auth.util_components

import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

// [커스텀 유틸 함수 모음]
@Component
class CustomUtil {
    // (ThymeLeaf 엔진으로 랜더링 한 HTML String 을 반환)
    fun parseHtmlFileToHtmlString(
        justHtmlFileNameWithOutSuffix: String,
        variableDataMap: Map<String, Any?>
    ): String {
        // 타임리프 resolver 설정
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.prefix = "templates/" // static/templates 경로 아래에 있는 파일을 읽는다
        templateResolver.suffix = ".html" // .html로 끝나는 파일을 읽는다
        templateResolver.templateMode = TemplateMode.HTML // 템플릿은 html 형식

        // 스프링 template 엔진을 thymeleafResolver 를 사용하도록 설정
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver)

        // 템플릿 엔진에서 사용될 변수 입력
        val context = Context()
        context.setVariables(variableDataMap)

        // 지정한 html 파일과 context 를 읽어 String 으로 반환
        return templateEngine.process(justHtmlFileNameWithOutSuffix, context)
    }

    // (byteArray 를 Hex String 으로 반환)
    fun bytesToHex(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (b in bytes) {
            builder.append(String.format("%02x", b))
        }
        return builder.toString()
    }


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}