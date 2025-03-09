package com.raillylinker.configurations

import com.raillylinker.web_socket_stomp_src.StompConst
import com.raillylinker.web_socket_stomp_src.StompInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration

// [WebSocket STOMP 설정]
@EnableWebSocketMessageBroker
@Configuration
class WebSocketStompConfig(
    private val stompInterceptor: StompInterceptor
) : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            // STOMP 접속 EndPoint (ex : var socket = new SockJS('http://localhost:8080/stomp');)
            .addEndpoint(StompConst.STOMP_END_POINT)
            // webSocket 연결 CORS 는 WebConfig 가 아닌 여기서 설정 (* 는 모든 것을 허용합니다.)
            .setAllowedOriginPatterns(*StompConst.CORS_PATTERNS)
            .withSockJS()
            .setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.2/sockjs.js")
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        /*
             WebSocketStompController 의 MessageMapping 연결 주소 prefix
             이 설정이 /app 이고, @MessageMapping("/test") 라고 되어있다면,
             stompClient.send("/app/test", {}, JSON.stringify({'chat': "sample Text"}));
             이처럼 요청 합니다.
         */
        registry.setApplicationDestinationPrefixes(StompConst.APPLICATION_DESTINATION_PREFIXES)

        /*
             구독 주소 prefix
             stompClient.subscribe('/topic', function (topic) {
                 // 구독 콜백 : 구독된 채널에 메세지가 날아오면 여기서 받음
             });
             위와 같이 topic 이라는 것을 구독하면,
             @SendTo("/topic") 로 설정 된 메세지 함수 실행 혹은
             simpMessagingTemplate.convertAndSend("/topic", TopicVo("waiting..."))
             이렇게 메세지 전달시 그 메세지를 받을 수 있습니다.

             /topic 은 전체 공지사항, 채팅방 공지사항, 브로드 캐스팅 데이터를 의미하며, 여러 사용자가 동시에 같은 데이터를 받을 때를 의미합니다.
             /queue 는 1:1 개인 메시지, 개인 공지사항, 작업 큐 등 한번에 하나의 이벤트를 전달할 때를 의미합니다.
         */
        registry.enableSimpleBroker(*StompConst.BROKER_DESTINATION_PREFIXES)
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompInterceptor)
    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        // WebSocket 으로 전송되는 메시지의 최대 크기 설정
        registry.setMessageSizeLimit(StompConst.MESSAGE_SIZE_LIMIT)
        // 메시지 전송에 대한 시간 제한 설정
        registry.setSendTimeLimit(StompConst.SEND_TIME_LIMIT)
        // 송신 버퍼의 크기 제한을 설정
        registry.setSendBufferSizeLimit(StompConst.SEND_BUFFER_SIZE_LIMIT)
    }
}