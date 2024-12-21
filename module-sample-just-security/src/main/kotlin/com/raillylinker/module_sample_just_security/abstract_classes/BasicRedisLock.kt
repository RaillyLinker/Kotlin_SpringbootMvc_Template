package com.raillylinker.module_sample_just_security.abstract_classes

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import java.util.*

// [RedisLock 의 Abstract 클래스]
abstract class BasicRedisLock(
    private val redisTemplateObj: RedisTemplate<String, String>,
    private val mapName: String
) {

    // <공개 메소드 공간>
    // (락 획득 메소드 - Lua 스크립트 적용)
    fun tryLock(
        key: String,
        expireTimeMs: Long
    ): String? {
        val uuid = UUID.randomUUID().toString()

        // Redis Storage 에 실제로 저장 되는 키 (map 이름과 키를 합친 String)
        val innerKey = "$mapName:${key}" // 실제 저장되는 키 = 그룹명:키

        val scriptResult = if (expireTimeMs < 0) {
            // 만료시간 무한
            redisTemplateObj.execute(
                RedisScript.of(
                    """
                        if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then
                            return 1
                        else
                            return 0
                        end
                    """.trimIndent(),
                    Long::class.java
                ),
                listOf(innerKey),
                uuid
            )
        } else {
            // 만료시간 유한
            redisTemplateObj.execute(
                RedisScript.of(
                    """
                        if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then
                            redis.call('pexpire', KEYS[1], ARGV[2])
                            return 1
                        else
                            return 0
                        end
                    """.trimIndent(),
                    Long::class.java
                ),
                listOf(innerKey),
                uuid,
                expireTimeMs.toString()
            )
        }

        return if (scriptResult == 1L) {
            // 락을 성공적으로 획득한 경우
            uuid
        } else {
            // 락을 획득하지 못한 경우
            null
        }
    }

    // (락 해제 메소드 - Lua 스크립트 적용)
    fun unlock(
        key: String,
        uuid: String
    ) {
        // Redis Storage 에 실제로 저장 되는 키 (map 이름과 키를 합친 String)
        val innerKey = "$mapName:${key}" // 실제 저장되는 키 = 그룹명:키

        redisTemplateObj.execute(
            RedisScript.of(
                """
                    if redis.call('get', KEYS[1]) == ARGV[1] then
                        return redis.call('del', KEYS[1])
                    else
                        return 0
                    end
                """.trimIndent(),
                Long::class.java
            ),
            listOf(innerKey),
            uuid
        )
    }

    // (락 강제 삭제)
    fun deleteLock(
        key: String
    ) {
        val innerKey = "$mapName:${key}" // 실제 저장되는 키 = 그룹명:키

        redisTemplateObj.delete(innerKey)
    }
}