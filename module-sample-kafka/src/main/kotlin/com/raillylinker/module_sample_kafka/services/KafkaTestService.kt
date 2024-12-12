package com.raillylinker.module_sample_kafka.services

import com.raillylinker.module_sample_kafka.controllers.KafkaTestController
import jakarta.servlet.http.HttpServletResponse

interface KafkaTestService {
    // (Kafka 토픽 메세지 발행 테스트)
    fun sendKafkaTopicMessageTest(
        httpServletResponse: HttpServletResponse,
        inputVo: KafkaTestController.SendKafkaTopicMessageTestInputVo
    )
}