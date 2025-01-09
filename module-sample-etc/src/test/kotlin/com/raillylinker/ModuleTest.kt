package com.raillylinker

import com.raillylinker.util_components.CustomUtil
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class ModuleTest {
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var customUtil: CustomUtil

    // (파일명, 경로, 확장자 분리 함수 테스트)
    @Test
    fun splitFilePathTest() {
        val result = customUtil.splitFilePath("sample.jpg")
        assertEquals("sample", result.fileName)
        assertEquals("jpg", result.extension)

        val result2 = customUtil.splitFilePath("sample")
        assertEquals("sample", result2.fileName)
        assertEquals(null, result2.extension)
    }
}