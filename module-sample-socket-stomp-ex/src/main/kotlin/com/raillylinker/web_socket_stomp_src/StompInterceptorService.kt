package com.raillylinker.web_socket_stomp_src

import com.raillylinker.configurations.SecurityConfig.AuthTokenFilterTotalAuth
import com.raillylinker.const_objects.ModuleConst
import com.raillylinker.redis_map_components.redis1_main.Redis1_Map_StompSessionInfo
import com.raillylinker.sys_components.ApplicationScheduler.Companion.STOMP_HEARTBEAT_MILLIS
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
    private val jwtTokenUtil: JwtTokenUtil,
    private val stompGateway: StompGateway,
    private val authTokenFilterTotalAuth: AuthTokenFilterTotalAuth,

    private val redis1MapStompSessionInfo: Redis1_Map_StompSessionInfo
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // Stomp 소켓 세션 정보(key : sessionId, value : principalUserName)
    val sessionInfoMap: HashMap<String, String> = hashMapOf()


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

        // 연결 자체에 인증/인가 제약을 걸기 위해선 아래 주석을 풀고 처리하세요.

//        // Authorization 헤더
//        val authorization: String? = accessor.getFirstNativeHeader("Authorization")
//
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

        // 소켓 세션에 개별 연결 정보 등록 (/session/queue 로 개별 메시지 발송시 이곳에서 등록한 principalUserName 을 사용합니다.)
        val principalUserName = "${ModuleConst.SERVER_UUID}/$sessionId" +
                "/${
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                }"

        accessor.user = StompPrincipalVo(principalUserName)
        sessionInfoMap[sessionId] = principalUserName

        // redis 에 세션 정보 등록
        redis1MapStompSessionInfo.saveKeyValue(
            principalUserName,
            Redis1_Map_StompSessionInfo.ValueVo(
                ModuleConst.SERVER_UUID
            ),
            // Stomp 서버 하트비트 스케쥴마다 Redis 에 정보를 갱신할 것이므로, 하트비트 타임 + 10% 간격으로 설정
            STOMP_HEARTBEAT_MILLIS + (STOMP_HEARTBEAT_MILLIS * 0.1).toLong()
        )

        return message
    }


    ////
    // (WebSocketStompConfig 의 configureClientInboundChannel 의 preSend 함수 SUBSCRIBE 처리)
    // 특정 채널 구독
    fun subscribeFromPreSend(
        message: Message<*>,
        channel: MessageChannel,
        accessor: StompHeaderAccessor
    ): Message<*>? {
        // 반환값 null 반환시 SUBSCRIBE 되지 않고, Exception 발생시엔 DISCONNECT 가 됩니다.
        return stompGateway.filterSubscribe(message, channel, accessor)
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
        return stompGateway.filterSend(message, channel, accessor)
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

        // 소켓 세션 아이디 (CONNECT 에 발행된 후 DISCONNECT 전까지 변화 없음)
//        val sessionId = accessor.sessionId!!
        // Authorization 헤더
//        val authorization: String? = accessor.getFirstNativeHeader("Authorization")
        // 요청 코드 헤더
//        val clientRequestCode: String? = accessor.getFirstNativeHeader("client-request-code")

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
        val sessionId = accessor.sessionId!!
        val sessionPrincipalName = sessionInfoMap[sessionId]

        if (sessionPrincipalName != null) {
            // redis 에서 세션 정보 삭제
            redis1MapStompSessionInfo.deleteKeyValue(sessionPrincipalName)

            // 등록 세션 정보 삭제
            sessionInfoMap.remove(sessionId)
        }

        return message
    }


    //// ---------------------------------------------------------------------------------------------------------------
    // (Stomp Principal VO)
    class StompPrincipalVo(
        // ${ServerUid}/${sessionId}/${yyyy_MM_dd_'T'_HH_mm_ss_SSS_z}
        // 서버 고유값과 세션 ID 로 메시지 전송 고유성을 확보할 수 있으며, 세션 생성 날짜로 전체 고유성을 확보하였습니다.
        private var name: String
    ) : Principal {
        override fun getName(): String = name
    }
}