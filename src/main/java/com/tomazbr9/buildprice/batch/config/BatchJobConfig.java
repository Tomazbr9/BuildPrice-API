package com.tomazbr9.buildprice.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchJobConfig {

    @Bean
    public Job sinapiJob(
            JobRepository jobRepository,
            Step step) {

        return new JobBuilder("sinapiJob", jobRepository)
                .start(step)
                .build();
    }

}

