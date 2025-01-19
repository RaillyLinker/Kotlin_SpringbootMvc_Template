package com.raillylinker

import com.raillylinker.util_components.BitBooleanListUtil
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

    @Autowired
    private lateinit var bitBooleanListUtil: BitBooleanListUtil

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

    // (bitBooleanListUtil 테스트)
    @Test
    fun bitBooleanListUtilTest() {
        val testBitList = listOf(true, false, false, true, true, true, true, true)

        val bitListToByte = bitBooleanListUtil.bitListToByte(testBitList)
        val byteToBitList = bitBooleanListUtil.byteToBitList(bitListToByte)
        val bitListToByte2 = bitBooleanListUtil.bitListToByte(byteToBitList)
        assertEquals(bitListToByte, bitListToByte2)

        val bitListToShort = bitBooleanListUtil.bitListToShort(testBitList)
        val shortToBitList = bitBooleanListUtil.shortToBitList(bitListToShort)
        val bitListToShort2 = bitBooleanListUtil.bitListToShort(shortToBitList)
        assertEquals(bitListToShort, bitListToShort2)

        val bitListToInt = bitBooleanListUtil.bitListToInt(testBitList)
        val intToBitList = bitBooleanListUtil.intToBitList(bitListToInt)
        val bitListToInt2 = bitBooleanListUtil.bitListToInt(intToBitList)
        assertEquals(bitListToInt, bitListToInt2)

        val bitListToLong = bitBooleanListUtil.bitListToLong(testBitList)
        val longToBitList = bitBooleanListUtil.longToBitList(bitListToLong)
        val bitListToLong2 = bitBooleanListUtil.bitListToLong(longToBitList)
        assertEquals(bitListToLong, bitListToLong2)

        val bitListToBigInteger = bitBooleanListUtil.bitListToBigInteger(testBitList)
        val bigIntegerToBitList = bitBooleanListUtil.bigIntegerToBitList(bitListToBigInteger)
        val bitListToBigInteger2 = bitBooleanListUtil.bitListToBigInteger(bigIntegerToBitList)
        assertEquals(bitListToBigInteger, bitListToBigInteger2)
    }
}