package com.raillylinker.kafka_components.consumers

import com.google.gson.Gson
import com.raillylinker.configurations.kafka_configs.Kafka1MainConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.apache.kafka.clients.consumer.ConsumerRecord

// kafka 토픽은 Producer 에서 결정합니다.
// _ 로 구분하며, {모듈 고유값}_{Topic 고유값} 의 형태로 정합니다.
@Component
class Kafka1MainConsumer {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        // !!!모듈 컨슈머 그룹 아이디!!!
        private const val CONSUMER_GROUP_ID = "com.raillylinker.sample_kafka"
    }

    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (testTopic1 에 대한 리스너)
    @KafkaListener(
        topics = ["sample-kafka_test-topic1"],
        groupId = CONSUMER_GROUP_ID,
        containerFactory = Kafka1MainConfig.CONSUMER_BEAN_NAME
    )
    fun testTopic1Group0Listener(data: ConsumerRecord<String, String>) {
        classLogger.info(
            """
                KafkaConsumerLog>>
                {
                    "data" : {
                        "$data"
                    }
                }
            """.trimIndent()
        )

        // JSON 문자열을 객체로 변환
        val testTopic1Group0ListenerInputVo = Gson().fromJson(data.value(), TestTopic1Group0ListenerInputVo::class.java)
        classLogger.info(">> testTopic1Group0ListenerInputVo : $testTopic1Group0ListenerInputVo")
    }

    data class TestTopic1Group0ListenerInputVo(
        val test: String,
        val test1: Int
    )


    // ----
    // (testTopic2 에 대한 리스너)
    @KafkaListener(
        topics = ["sample-kafka_test-topic2"],
        groupId = CONSUMER_GROUP_ID,
        containerFactory = Kafka1MainConfig.CONSUMER_BEAN_NAME
    )
    fun testTopic2Group0Listener(data: ConsumerRecord<String, String>) {
        classLogger.info(
            """
                KafkaConsumerLog>>
                {
                    "data" : {
                        "$data"
                    }
                }
            """.trimIndent()
        )
    }


    // ----
    // (testTopic2 에 대한 동일 그룹 테스트 리스너)
    // 동일 topic 에 동일 group 을 설정할 경우, 리스너는 한개만을 선택하고 다른 하나는 침묵합니다.
    @KafkaListener(
        topics = ["sample-kafka_test-topic2"],
        groupId = CONSUMER_GROUP_ID,
        containerFactory = Kafka1MainConfig.CONSUMER_BEAN_NAME
    )
    fun testTopic2Group0Listener2(data: ConsumerRecord<String, String>) {
        classLogger.info(
            """
                KafkaConsumerLog>>
                {
                    "data" : {
                        "$data"
                    }
                }
            """.trimIndent()
        )
    }


    // ----
    // (testTopic2 에 대한 리스너 - 그룹 변경)
    @KafkaListener(
        topics = ["sample-kafka_test-topic2"],
        groupId = CONSUMER_GROUP_ID + "_2",
        containerFactory = Kafka1MainConfig.CONSUMER_BEAN_NAME
    )
    fun testTopic2Group1Listener(data: ConsumerRecord<String, String>) {
        classLogger.info(
            """
                KafkaConsumerLog>>
                {
                    "data" : {
                        "$data"
                    }
                }
            """.trimIndent()
        )
    }
}