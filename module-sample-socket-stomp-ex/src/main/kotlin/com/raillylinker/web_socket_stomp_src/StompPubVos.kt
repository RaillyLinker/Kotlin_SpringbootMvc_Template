package com.raillylinker.web_socket_stomp_src

// (Socket STOMP 발행 Value Object)
// STOMP 채널별 구독시 발행받는 메시지에 대한 Value Object
class StompPubVos {
    // (/topic/server-heartbeat)
    // Stomp 서버 동작 여부 확인용 하트비트 VO
    data class TopicServerHeartbeatVo(
        // Stomp 서버 고유값
        val serverUuid: String,
        // 서버 하트비트 간격 MilliSec(이 간격 + 20% 까지 하트비트가 오지 않는다면 클라이언트는 서버가 죽은 것으로 간주합니다.)
        val heartbeatIntervalMs: Long
    )

    // (/topic/test-channel)
    data class TopicTestChannelVo(
        val content: String
    )

    // (/session/queue/test-channel)
    data class QueueTestChannelVo(
        val content: String
    )
}