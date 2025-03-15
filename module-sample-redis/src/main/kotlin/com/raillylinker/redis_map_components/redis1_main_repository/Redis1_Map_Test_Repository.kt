package com.raillylinker.redis_map_components.redis1_main_repository

import com.google.gson.Gson
import com.raillylinker.abstract_classes.BasicRedisMap.RedisMapDataVo
import com.raillylinker.redis_map_components.redis1_main.Redis1_Map_Test
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

// [RedisMap 컴포넌트]
@Component
class Redis1_Map_Test_Repository(
    // !!!RedisConfig 종류 변경!!!
    redisObject: Redis1_Map_Test
) {
    // <멤버 변수 공간>
    // gson 객체
    private val gson = Gson()

    // redisTemplate 객체
    val redisTemplate = redisObject.redisTemplate

    // "$mapName:${key}" // 실제 저장되는 키 = 그룹명:키
    val mapName = Redis1_Map_Test.MAP_NAME


    //// ---------------------------------------------------------------------------------------------------------------
    // <Repository 함수 공간>
    // (content 가 test 로 시작되는 리스트 반환)
    fun repositoryTest(): List<RedisMapDataVo<Redis1_Map_Test.ValueVo>> {
        val resultList = mutableListOf<RedisMapDataVo<Redis1_Map_Test.ValueVo>>()

        val scanOptions = ScanOptions.scanOptions().match("$mapName:*").build()
        val cursor = redisTemplate.scan(scanOptions)

        cursor.use {
            while (it.hasNext()) {
                val innerKey = it.next()

                // 키에서 mapName 제거하여 외부에서 사용할 key 추출
                val key = innerKey.removePrefix("$mapName:")

                // Redis Storage 에 실제로 저장되는 Value (Json String 형식)
                val innerValue = redisTemplate.opsForValue()[innerKey] ?: continue

                // JSON → 객체 변환 (Jackson 사용 가능)
                val valueObject = gson.fromJson(innerValue, Redis1_Map_Test.ValueVo::class.java)

                // content 값이 "test"로 시작하는 경우만 추가
                if (valueObject.content.startsWith("test")) {
                    resultList.add(
                        RedisMapDataVo(
                            key,
                            valueObject,
                            redisTemplate.getExpire(innerKey, TimeUnit.MILLISECONDS) ?: -1L // null 방지
                        )
                    )
                }
            }
        }

        return resultList
    }
}