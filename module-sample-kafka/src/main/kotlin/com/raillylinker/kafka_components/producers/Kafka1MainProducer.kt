package com.raillylinker.kafka_components.producers

import com.google.gson.Gson
import com.raillylinker.configurations.kafka_configs.Kafka1MainConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

// kafka 토픽은 Producer 에서 결정합니다.
// _ 로 구분하며, {모듈 고유값}_{Topic 고유값} 의 형태로 정합니다.
@Component
class Kafka1MainProducer(
    @Qualifier(Kafka1MainConfig.PRODUCER_BEAN_NAME) private val kafka1MainProducerTemplate: KafkaTemplate<String, Any>,
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (testTopic1 에 메시지 발송)
    fun sendMessageToTestTopic1(message: SendMessageToTestTopic1InputVo) {
        // kafkaProducer1 에 토픽 메세지 발행
        kafka1MainProducerTemplate.send("sample-kafka_test-topic1", Gson().toJson(message))
    }

    data class SendMessageToTestTopic1InputVo(
        val test: String,
        val test1: Int
    )
}