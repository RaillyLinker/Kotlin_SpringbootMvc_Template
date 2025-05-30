package com.raillylinker.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.services.WebSocketStompService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

// [WebSocket STOMP 컨트롤러]
// api1 은 external_files/files_for_api_test/html_file_sample/websocket-stomp.html 파일로 테스트 가능
@Controller
class WebSocketStompController(
    private val service: WebSocketStompService
) {
    // 메세지 함수 호출 경로 (WebSocketStompConfig 의 setApplicationDestinationPrefixes 설정과 합쳐서 호출, ex : /app/send-to-topic-test)
    @MessageMapping("/send-to-topic-test")
    // 이 함수의 리턴값 반환 위치(/topic 을 구독중인 유저에게 return 값을 반환)
    @SendTo("/topic")
    fun sendToTopicTest(inputVo: SendToTopicTestInputVo): SendToTopicTestOutputVo {
        return service.sendToTopicTest(inputVo)
    }

    data class SendToTopicTestInputVo(
        @JsonProperty("chat")
        val chat: String
    )

    data class SendToTopicTestOutputVo(
        @JsonProperty("content")
        val content: String
    )
}