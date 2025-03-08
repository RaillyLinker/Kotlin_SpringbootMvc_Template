package com.raillylinker.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Service
import org.springframework.messaging.Message
import org.springframework.messaging.simp.stomp.StompHeaderAccessor

@Service
class WebSocketStompConfigService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 CONNECT 처리)
    // 클라이언트가 CONNECT 혹은 STOMP 함수를 사용하여 서버에 연결됨
    fun stompConnect(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        val sessionId = accessor.sessionId
        val destination = accessor.destination
        val token: String? = accessor.getFirstNativeHeader("Authorization")

//        if (token.isNullOrBlank()) {
//            throw IllegalArgumentException("Authorization header is missing")
//        }

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 SUBSCRIBE 처리)
    // 특정 채널(토픽)을 구독
    fun stompSubscribe(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        val sessionId = accessor.sessionId
        val destination = accessor.destination
        val token: String? = accessor.getFirstNativeHeader("Authorization")

//        if (token.isNullOrBlank()) {
//            throw IllegalArgumentException("Authorization header is missing")
//        }

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 UNSUBSCRIBE 처리)
    // 특정 대상(Destination)으로 메시지를 보낼 때
    fun stompSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<out Any>? {
        val sessionId = accessor.sessionId
        val destination = accessor.destination
        val token: String? = accessor.getFirstNativeHeader("Authorization")

//        if (token.isNullOrBlank()) {
//            throw IllegalArgumentException("Authorization header is missing")
//        }

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 UNSUBSCRIBE 처리)
    // 특정 채널(토픽) 구독을 취소
    fun stompUnSubscribe(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        val sessionId = accessor.sessionId
        val destination = accessor.destination
        val token: String? = accessor.getFirstNativeHeader("Authorization")

//        if (token.isNullOrBlank()) {
//            throw IllegalArgumentException("Authorization header is missing")
//        }

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 DISCONNECT 처리)
    // 클라이언트가 서버와의 연결을 종료함
    fun stompDisconnect(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        val sessionId = accessor.sessionId
        val destination = accessor.destination
        val token: String? = accessor.getFirstNativeHeader("Authorization")

//        if (token.isNullOrBlank()) {
//            throw IllegalArgumentException("Authorization header is missing")
//        }

        return message
    }
}