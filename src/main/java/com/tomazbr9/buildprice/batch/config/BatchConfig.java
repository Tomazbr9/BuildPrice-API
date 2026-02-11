package com.tomazbr9.buildprice.batch.config;

import com.tomazbr9.buildprice.batch.step1.SinapiItemProcessor;
import com.tomazbr9.buildprice.batch.step1.SinapiItemReader;
import com.tomazbr9.buildprice.batch.SinapiJobCleanupListener;
import com.tomazbr9.buildprice.batch.step0.CleanupItemsTasklet;
import com.tomazbr9.buildprice.dto.sinapi.SinapiItemDTO;
import com.tomazbr9.buildprice.entity.SinapiItem;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public JobLauncher jobLauncher(
            JobRepository jobRepository
    ) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job sinapiJob(
            JobRepository jobRepository,
            SinapiJobCleanupListener cleanupListener,
            Step step0,
            Step step1
    ) {

        return new JobBuilder("sinapiJob", jobRepository)
                .start(step0)
                .next(step1)
                .listener(cleanupListener)
                .build();
    }

    @Bean
    public Step step0(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            CleanupItemsTasklet tasklet
    ){
        return new StepBuilder("step0", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step1(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SinapiItemReader reader,
            SinapiItemProcessor processor,
            ItemWriter<List<SinapiItem>> sinapiItemWriter) {

        return new StepBuilder("step1", jobRepository)
                .<SinapiItemDTO, List<SinapiItem>>chunk(200, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(sinapiItemWriter)
                .build();
    }

    @Bean
    public ItemWriter<List<SinapiItem>> sinapiItemWriter(EntityManagerFactory emf) {

        JpaItemWriter<SinapiItem> delegate = new JpaItemWriter<>();
        delegate.setEntityManagerFactory(emf);

        return chunk -> {
            List<SinapiItem> flattenedItems = chunk.getItems().stream()
                    .flatMap(List::stream)
                    .toList();

            delegate.write(new Chunk<>(flattenedItems));
        };
    }

}
