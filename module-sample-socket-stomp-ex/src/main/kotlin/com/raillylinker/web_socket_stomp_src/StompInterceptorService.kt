package com.raillylinker.web_socket_stomp_src

import com.google.gson.Gson
import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth
import com.raillylinker.const_objects.ModuleConst
import com.raillylinker.kafka_components.producers.Kafka1MainProducer
import com.raillylinker.util_components.JwtTokenUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Service
import org.springframework.messaging.Message
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import java.security.Principal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class StompInterceptorService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,
    private val authTokenFilterTotalAuth: AuthTokenFilterTotalAuth,
    private val kafka1MainProducer: Kafka1MainProducer,
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
        // 반환값 null 반환 혹은 Exception 발생시 메시지 전달이 되지 않고 CONNECT 도 되지 않습니다.

        // 소켓 세션 아이디 (CONNECT 에 발행된 후 DISCONNECT 전까지 변화 없음)
        val sessionId = accessor.sessionId!!
        // Authorization 헤더
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

//        if (authorization.isNullOrBlank() ||
//            authTokenFilterTotalAuth.checkRequestAuthorization(authorization) == null
//        ) {
//            // 인증 실패
//        } else {
//            // 인증 성공
//            val token = authorization.split(" ")[1].trim()
//            val memberUid = jwtTokenUtil.getMemberUid(
//                token,
//                authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
//                authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
//            )
//            val roleList = jwtTokenUtil.getRoleList(
//                token,
//                authTokenFilterTotalAuth.authJwtClaimsAes256InitializationVector,
//                authTokenFilterTotalAuth.authJwtClaimsAes256EncryptionKey
//            )
//        }

        // 소켓 세션에 유저 정보 등록
        accessor.user = StompPrincipalVo(
            "${ModuleConst.SERVER_UUID}/$sessionId" +
                    "/${
                        LocalDateTime.now().atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                    }"
        )

        userName = accessor.user!!.name

        return message
    }

    /*
        todo
            1. 서버별 소켓 세션 정보 저장
            2. kafka 로는 모든 위치에 요청 전달(kafka 로 받아서 세션 정보 리스트로 메시지 전달)
            3. 서버 고유값으로 경로 최적화
     */
    // todo 아래 수정하기


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 SUBSCRIBE 처리)
    // 특정 채널 구독
    fun subscribeFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        // 반환값 null 반환시 메시지 전달이 되지 않고 SUBSCRIBE 되지 않습니다.
        // Exception 발생시엔 DISCONNECT 가 됩니다.

        // 소켓 세션 아이디 (CONNECT 에 발행된 후 DISCONNECT 전까지 변화 없음)
        val sessionId = accessor.sessionId
        // 구독 경로 (ex : /topic)
        val destination = accessor.destination ?: return null
        // Authorization 헤더
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

        // 경로에 따른 구독 허용 여부 판단
        if (destination.startsWith("/")) {
            return message
        }

        // todo 개별 메시지 전송 queue 테스트, 주소 체계 결정, 구독 허용 처리

        // 위에서 허용한 경로 외의 모든 구독 요청을 거절
        return null
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 SEND 처리)
    // 특정 대상(Destination)으로 메시지를 보낼 때
    fun sendFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<out Any>? {
        // 반환값 null 반환시 메시지 전달이 되지 않고, Exception 발생시엔 DISCONNECT 가 됩니다.

        // 소켓 세션 아이디 (CONNECT 에 발행된 후 DISCONNECT 전까지 변화 없음)
        val sessionId = accessor.sessionId
        // 메시지 발행 경로 (ex : /app/send-to-topic-test)
        val destination = accessor.destination
        // Authorization 헤더
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

        if (authorization.isNullOrBlank() || authTokenFilterTotalAuth.checkRequestAuthorization(authorization) == null) {
            // Authorization 인증 실패
            if (sessionId == null) {
                return null
            }

            kafka1MainProducer.sendMessageToStomp(
                Kafka1MainProducer.SendMessageToStompInputVo(
                    userName,
                    "/queue/test-channel",
                    Gson().toJson(StompSubVos.QueueTestChannelVo("Subscription denied: Unauthorized user. ${accessor.user?.name}"))
                )
            )

            return null
        }

        return message
    }

    var userName = ""


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 UNSUBSCRIBE 처리)
    // 특정 채널(토픽) 구독을 취소
    fun unSubscribeFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        // 반환값 null 반환시 메시지 전달이 되지 않고, Exception 발생시엔 DISCONNECT 가 됩니다.

        // 소켓 세션 아이디 (CONNECT 에 발행된 후 DISCONNECT 전까지 변화 없음)
        val sessionId = accessor.sessionId
        // Authorization 헤더
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

        // 소켓 세션 아이디 (CONNECT 에 발행된 후 DISCONNECT 전까지 변화 없음)
        val sessionId = accessor.sessionId
        // Authorization 헤더
        val authorization: String? = accessor.getFirstNativeHeader("Authorization")

        return message
    }


    // (Stomp Principal VO)
    class StompPrincipalVo(
        // ${ServerUid}/${sessionId}/${yyyy_MM_dd_'T'_HH_mm_ss_SSS_z}
        // 서버 고유값과 세션 ID 로 메시지 전송 고유성을 확보할 수 있으며, 세션 생성 날짜로 전체 고유성을 확보하였습니다.
        private var name: String
    ) : Principal {
        override fun getName(): String = name
    }
}