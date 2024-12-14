package com.raillylinker.module_sample_batch.batch_components

import com.raillylinker.module_sample_batch.configurations.jpa_configs.Db1MainConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager

@Component
class TaskletBatchTest(
    private val jobRepository: JobRepository,
    @Qualifier(Db1MainConfig.TRANSACTION_NAME)
    private val transactionManager: PlatformTransactionManager
) {
    companion object {
        // 배치 Job 이름
        const val BATCH_JOB_NAME = "TaskletBatchTest"
    }

    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // [Batch Job 및 하위 작업 작성]
    // BatchConfig 하나당 하나의 Job 을 가져야 합니다.

    // (Batch Job)
    @Bean(BATCH_JOB_NAME)
    fun batchJob(): Job {
        return JobBuilder("${BATCH_JOB_NAME}_batchJob", jobRepository)
            .start(taskletTestStep())
            .build()
    }

    // (Tasklet 테스트 Step)
    fun taskletTestStep(): Step {
        return StepBuilder("${BATCH_JOB_NAME}_taskletTestStep", jobRepository)
            .tasklet(justLoggingTasklet(), transactionManager)
            .build()
    }

    // (단순히 로깅하는 Tasklet)
    fun justLoggingTasklet(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            classLogger.info("TaskletBatchTest : Tasklet Batch Test Complete!")
            RepeatStatus.FINISHED
        }
    }
}