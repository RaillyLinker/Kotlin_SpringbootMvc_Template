package com.raillylinker.web_socket_stomp_src

import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth
import com.raillylinker.controllers.WebSocketStompController
import com.raillylinker.util_components.JwtTokenUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Service
import org.springframework.messaging.Message
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor

@Service
class StompInterceptorService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,
    private val authTokenFilterTotalAuth: AuthTokenFilterTotalAuth,
    @Lazy private val simpMessagingTemplate: SimpMessagingTemplate,
    private val jwtTokenUtil: JwtTokenUtil
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 CONNECT 처리)
    // 소켓 연결
    fun connectFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        // 반환값 null 반환시 메시지 전달이 되지 않고, Exception 발생시엔 DISCONNECT 가 됩니다. (CONNECT 도 되지 않습니다.)

//        val sessionId = accessor.sessionId
//        val destination = accessor.destination
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

        // 최초 소켓 연결시 로그인이 되어 있지 않으면 거부
        // 이후 토큰 만료시에는 에러 메시지를 발송하여 클라이언트 측에서 처리하도록 함.
        // 이렇게 하면 연결 불가의 원인이 인증/인가가 안 된 이유로 좁혀지므로 클라이언트 처리가 단순해집니다.
        if (authorization.isNullOrBlank() || authTokenFilterTotalAuth.checkRequestAuthorization(authorization) == null) {
            // Authorization 인증 실패
            throw IllegalArgumentException("Authorization header is missing")
        }

        // 소켓 세션에 유저 정보 등록
        val token = authorization.split(" ")[1].trim()
        val memberUid = jwtTokenUtil.getMemberUid(
            token,
            authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
            authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
        )
        val roleList = jwtTokenUtil.getRoleList(
            token,
            authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
            authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
        )
        accessor.user = StompPrincipalVo(memberUid.toString(), roleList)

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 SUBSCRIBE 처리)
    // 특정 채널(토픽)을 구독
    fun subscribeFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        // 반환값 null 반환시 메시지 전달이 되지 않고, Exception 발생시엔 DISCONNECT 가 됩니다. (SUBSCRIBE 도 되지 않습니다.)

        val sessionId = accessor.sessionId
        val destination = accessor.destination // 구독 경로 (ex : /topic)
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

        if (authorization.isNullOrBlank() || authTokenFilterTotalAuth.checkRequestAuthorization(authorization) == null) {
            // Authorization 인증 실패
            if (sessionId == null) {
                return null
            }
//            simpMessagingTemplate.convertAndSendToUser(sessionId, "/send-to-topic-test", errorMessage)
            simpMessagingTemplate.convertAndSend(
                "/topic",
                WebSocketStompController.SendToTopicTestOutputVo("Subscription denied: Unauthorized user.")
            )
            return null
        }

        // todo 구독 요청 유저가 해당 토픽 구독한지 판단하기(에러/시스템 정보를 전달할 queue 채널 구독 실패시엔 에러 발생, 나머지는 에러 채널로 메시지 보내기)

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 UNSUBSCRIBE 처리)
    // 특정 대상(Destination)으로 메시지를 보낼 때
    fun sendFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<out Any>? {
        // 반환값 null 반환시 메시지 전달이 되지 않고, Exception 발생시엔 DISCONNECT 가 됩니다.

        val sessionId = accessor.sessionId
        val destination = accessor.destination
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

        if (authorization.isNullOrBlank() || authTokenFilterTotalAuth.checkRequestAuthorization(authorization) == null) {
            // Authorization 인증 실패
            if (sessionId == null) {
                return null
            }
//            simpMessagingTemplate.convertAndSendToUser(sessionId, "/send-to-topic-test", errorMessage)
            simpMessagingTemplate.convertAndSend(
                "/topic",
                WebSocketStompController.SendToTopicTestOutputVo("Subscription denied: Unauthorized user. ${accessor.user?.name}")
            )
            return null
        }

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 UNSUBSCRIBE 처리)
    // 특정 채널(토픽) 구독을 취소
    fun unSubscribeFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        // 반환값 null 반환시 메시지 전달이 되지 않고, Exception 발생시엔 DISCONNECT 가 됩니다.

        val sessionId = accessor.sessionId
        val destination = accessor.destination
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 DISCONNECT 처리)
    // 클라이언트가 서버와의 연결을 종료함
    fun disconnectFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        // 반환값 null 반환시 메시지 전달이 되지 않고, Exception 발생시엔 DISCONNECT 가 됩니다.

        val sessionId = accessor.sessionId
        val destination = accessor.destination
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

        return message
    }
}