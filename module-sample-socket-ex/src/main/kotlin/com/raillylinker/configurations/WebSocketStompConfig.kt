package com.raillylinker.configurations

import com.raillylinker.services.WebSocketStompConfigService
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration

// [WebSocket STOMP 설정]
@EnableWebSocketMessageBroker
@Configuration
class WebSocketStompConfig(
    private val webSocketStompConfigService: WebSocketStompConfigService
) : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            // 접속 EndPoint
            // ex : var socket = new SockJS('http://localhost:8080/stomp');
            .addEndpoint("/stomp")
            // webSocket 연결 CORS 는 WebConfig 가 아닌 여기서 설정
            .setAllowedOriginPatterns("*")
            .withSockJS()
            .setClientLibraryUrl("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.2/sockjs.js")
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        /*
             WebSocketStompController 의 MessageMapping 연결 주소 prefix
             이 설정이 /app 이고, @MessageMapping("/test") 라고 되어있다면,
             stompClient.send("/app/test", {}, JSON.stringify({'chat': "sample Text"}));
             이처럼 요청 가능.

             클라이언트 -> 서버 방향의 메시지입니다.
         */
        registry.setApplicationDestinationPrefixes("")

        /*
             구독 주소
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
        registry.enableSimpleBroker("/topic", "/queue")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            /*
                [ChannelInterceptor 콜백 실행 순서]
                메시지가 발생되면 preSend -> postSend -> afterSendCompletion 순으로 실행됩니다.
                인터셉터 내에서 Exception 이 발생되면 preSend 의 DISCONNECT -> postSend -> afterSendCompletion 순서로 연결이 제거되며,
                DISCONNECT 에서 Exception 이 발생되면 더이상 진행되지 않습니다.
             */

            /*
                (메시지가 전송되기 전에 실행됨)
                Message 가 실제로 채널로 전송되기 전에 호출됩니다.
                필요한 경우 Message 를 수정할 수 있습니다.
                이 메서드가 null 을 반환하면 실제 전송 호출이 발생하지 않습니다.
             */
            override fun preSend(
                message: Message<*>,
                channel: MessageChannel
            ): Message<*>? {
                val accessor: StompHeaderAccessor = StompHeaderAccessor.wrap(message)

                // 각 이벤트 콜백 처리를 서비스로 이관
                return when (accessor.command) {
                    StompCommand.CONNECT, StompCommand.STOMP -> {
                        webSocketStompConfigService.connectFromPreSend(message, channel, accessor)
                    }

                    StompCommand.SUBSCRIBE -> {
                        webSocketStompConfigService.subscribeFromPreSend(message, channel, accessor)
                    }

                    StompCommand.SEND -> {
                        webSocketStompConfigService.sendFromPreSend(message, channel, accessor)
                    }

                    StompCommand.UNSUBSCRIBE -> {
                        webSocketStompConfigService.unSubscribeFromPreSend(message, channel, accessor)
                    }

                    StompCommand.DISCONNECT -> {
                        webSocketStompConfigService.disconnectFromPreSend(message, channel, accessor)
                    }

                    else -> {
                        message
                    }
                }
            }

            /*
                (메시지가 전송된 후 실행됨)
                send 호출 직후에 호출됩니다.
                sent 파라미터로 메시지 전송 성공 여부를 알 수 있습니다.
                preSend 함수가 null 을 반환한 경우 호출되지 않습니다.
             */
            override fun postSend(message: Message<*>, channel: MessageChannel, sent: Boolean) {
                super.postSend(message, channel, sent)
            }

            /*
                (메시지 전송이 완료된 후 실행됨)
                예외가 발생했는지 여부에 관계없이 전송이 완료된 후 호출됩니다.
                preSend 함수가 null 을 반환한 경우 호출되지 않습니다.
             */
            override fun afterSendCompletion(
                message: Message<*>,
                channel: MessageChannel,
                sent: Boolean,
                ex: Exception?
            ) {
                super.afterSendCompletion(message, channel, sent, ex)
            }
        })
    }

    override fun configureWebSocketTransport(registry: WebSocketTransportRegistration) {
        // WebSocket 으로 전송되는 메시지의 최대 크기를 설정
        registry.setMessageSizeLimit(160 * 64 * 1024)
        // 메시지 전송에 대한 시간 제한을 설정
        registry.setSendTimeLimit(100 * 10000)
        // 송신 버퍼의 크기 제한을 설정
        registry.setSendBufferSizeLimit(3 * 512 * 1024)
    }
}