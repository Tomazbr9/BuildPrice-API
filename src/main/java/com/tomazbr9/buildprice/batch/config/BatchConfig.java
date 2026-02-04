package com.tomazbr9.buildprice.batch.config;

import com.tomazbr9.buildprice.batch.SinapiItemProcessor;
import com.tomazbr9.buildprice.batch.SinapiItemReader;
import com.tomazbr9.buildprice.batch.SinapiJobCleanupListener;
import com.tomazbr9.buildprice.dto.sinapi.SinapiItemDTO;
import com.tomazbr9.buildprice.entity.SinapiItem;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public Job sinapiJob(
            JobRepository jobRepository,
            SinapiJobCleanupListener cleanupListener,
            Step step) {

        return new JobBuilder("sinapiJob", jobRepository)
                .start(step)
                .listener(cleanupListener)
                .build();
    }

    @Bean
    public JpaItemWriter<SinapiItem> writer(EntityManagerFactory emf) {
        JpaItemWriter<SinapiItem> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public Step sinapiStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SinapiItemReader reader,
            SinapiItemProcessor processor,
            JpaItemWriter<SinapiItem> writer) {

        return new StepBuilder("sinapiStep", jobRepository)
                .<SinapiItemDTO, SinapiItem>chunk(200, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
