package com.raillylinker.module_sample_redis.services

import com.raillylinker.module_sample_redis.controllers.RedisTestController
import jakarta.servlet.http.HttpServletResponse

/*
    Redis 는 메모리상 저장되는 Key-Value 형식의 데이터베이스 시스템이자, 요청 순서대로 처리됩니다.
 */
interface RedisTestService {
    // (Redis Key-Value 입력 테스트)
    fun insertRedisKeyValueTest(
        httpServletResponse: HttpServletResponse,
        inputVo: RedisTestController.InsertRedisKeyValueTestInputVo
    )


    ////
    // (Redis Key-Value 조회 테스트)
    fun selectRedisValueSample(
        httpServletResponse: HttpServletResponse,
        key: String
    ): RedisTestController.SelectRedisValueSampleOutputVo?


    ////
    // (Redis Key-Value 모두 조회 테스트)
    fun selectAllRedisKeyValueSample(httpServletResponse: HttpServletResponse): RedisTestController.SelectAllRedisKeyValueSampleOutputVo?


    ////
    // (Redis Key-Value 삭제 테스트)
    fun deleteRedisKeySample(httpServletResponse: HttpServletResponse, key: String)


    ////
    // (Redis Key-Value 모두 삭제 테스트)
    fun deleteAllRedisKeySample(httpServletResponse: HttpServletResponse)


    ////
    // (Redis Lock 테스트)
    fun tryRedisLockSample(httpServletResponse: HttpServletResponse): RedisTestController.TryRedisLockSampleOutputVo?


    ////
    // (Redis unLock 테스트)
    fun unLockRedisLockSample(httpServletResponse: HttpServletResponse, lockKey: String)
}