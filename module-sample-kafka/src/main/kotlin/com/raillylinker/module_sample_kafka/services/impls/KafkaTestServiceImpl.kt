package com.raillylinker.module_sample_kafka.services.impls

import com.raillylinker.module_sample_kafka.controllers.KafkaTestController
import com.raillylinker.module_sample_kafka.services.KafkaTestService
import com.raillylinker.module_sample_kafka.kafka_components.producers.Kafka1MainProducer
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class KafkaTestServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val kafka1MainProducer: Kafka1MainProducer
) : KafkaTestService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun sendKafkaTopicMessageTest(
        httpServletResponse: HttpServletResponse,
        inputVo: KafkaTestController.SendKafkaTopicMessageTestInputVo
    ) {
        // kafkaProducer1 에 토픽 메세지 발행
        kafka1MainProducer.sendMessageToTestTopic1(
            Kafka1MainProducer.SendMessageToTestTopic1InputVo(
                inputVo.message,
                1
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }
}