package com.raillylinker.util_components

import org.springframework.stereotype.Component
import java.math.BigInteger

// [변수를 List<Boolean> 형태의 Bit List 로 상호 변환하는 유틸]
// bitListTo 함수에서 패딩값은 0으로, 리스트 앞쪽에 붙습니다.
@Component
class BitBooleanListUtil {
    // Byte -> List<Boolean>
    fun byteToBitList(value: Byte): List<Boolean> =
        String.format("%8s", Integer.toBinaryString(value.toInt() and 0xFF)).replace(' ', '0')
            .map { it == '1' }

    // List<Boolean> -> Byte
    fun bitListToByte(bits: List<Boolean>): Byte {
        if (bits.size > 8) {
            throw IllegalArgumentException("Byte 타입의 비트 길이를 초과한 비트 리스트입니다. (최대 8비트)")
        }
        return bits.reversed().foldIndexed(0) { index, acc, bit ->
            if (bit) acc or (1 shl index) else acc
        }.toByte()
    }

    // ----
    // Short -> List<Boolean>
    fun shortToBitList(value: Short): List<Boolean> =
        String.format("%16s", Integer.toBinaryString(value.toInt() and 0xFFFF)).replace(' ', '0')
            .map { it == '1' }

    // List<Boolean> -> Short
    fun bitListToShort(bits: List<Boolean>): Short {
        if (bits.size > 16) {
            throw IllegalArgumentException("Short 타입의 비트 길이를 초과한 비트 리스트입니다. (최대 16비트)")
        }
        return bits.reversed().foldIndexed(0) { index, acc, bit ->
            if (bit) acc or (1 shl index) else acc
        }.toShort()
    }

    // ----
    // Int -> List<Boolean>
    fun intToBitList(value: Int): List<Boolean> =
        String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0')
            .map { it == '1' }

    // List<Boolean> -> Int
    fun bitListToInt(bits: List<Boolean>): Int {
        if (bits.size > 32) {
            throw IllegalArgumentException("Int 타입의 비트 길이를 초과한 비트 리스트입니다. (최대 32비트)")
        }
        return bits.reversed().foldIndexed(0) { index, acc, bit ->
            if (bit) acc or (1 shl index) else acc
        }
    }

    // ----
    // Long -> List<Boolean>
    fun longToBitList(value: Long): List<Boolean> =
        String.format("%64s", java.lang.Long.toBinaryString(value)).replace(' ', '0')
            .map { it == '1' }

    // List<Boolean> -> Long
    fun bitListToLong(bits: List<Boolean>): Long {
        if (bits.size > 64) {
            throw IllegalArgumentException("Long 타입의 비트 길이를 초과한 비트 리스트입니다. (최대 64비트)")
        }
        return bits.reversed().foldIndexed(0L) { index, acc, bit ->
            if (bit) acc or (1L shl index) else acc
        }
    }

    // ----
    // BigInteger -> List<Boolean>
    fun bigIntegerToBitList(value: BigInteger): List<Boolean> =
        value.toString(2).padStart(64, '0').map { it == '1' }

    // List<Boolean> -> BigInteger
    fun bitListToBigInteger(bits: List<Boolean>): BigInteger {
        return bits.reversed().foldIndexed(BigInteger.ZERO) { index, acc, bit ->
            if (bit) acc.or(BigInteger.ONE.shiftLeft(index)) else acc
        }
    }
}