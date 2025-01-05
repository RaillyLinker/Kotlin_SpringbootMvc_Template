package com.raillylinker.services

import com.raillylinker.controllers.WebSocketStompController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class WebSocketStompService(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (/test 로 받아서 /topic 토픽을 구독중인 모든 클라이언트에 메시지 전달)
    fun sendToTopicTest(inputVo: WebSocketStompController.SendToTopicTestInputVo): WebSocketStompController.TopicVo {
        // 이렇게 SimpMessagingTemplate 객체로 메세지를 전달할 수 있습니다.
        // /topic 을 구독하는 모든 유저에게 메시지를 전달하였습니다.
        simpMessagingTemplate.convertAndSend(
            "/topic",
            WebSocketStompController.TopicVo("$inputVo : SimpMessagingTemplate Test")
        )

        Thread.sleep(1000)

        // 이렇게 @SendTo 함수 결과값으로 메세지를 전달할 수도 있습니다.
        // 앞서 @SendTo 에 설정한 /topic 을 구독하는 모든 유저에게 마시지를 전달하였습니다.
        return WebSocketStompController.TopicVo("$inputVo : @SendTo Test")
    }
}