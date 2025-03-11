package com.raillylinker.kafka_components.producers

import com.google.gson.Gson
import com.raillylinker.configurations.kafka_configs.Kafka1MainConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class Kafka1MainProducer(
    @Qualifier(Kafka1MainConfig.PRODUCER_BEAN_NAME) private val kafka1MainProducerTemplate: KafkaTemplate<String, Any>,
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (stomp 에 메시지 발송)
    fun sendMessageToStomp(message: SendMessageToStompInputVo) {
        // stomp 에 토픽 메세지 발행
        kafka1MainProducerTemplate.send("stomp", Gson().toJson(message))
    }

    data class SendMessageToStompInputVo(
        val stompVoTypeCode: Int,
        val stompVoJsonString: String
    )
}