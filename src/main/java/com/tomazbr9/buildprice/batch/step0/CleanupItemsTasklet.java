package com.tomazbr9.buildprice.batch.step0;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CleanupItemsTasklet implements Tasklet {

    private JdbcTemplate jdbc;

    public CleanupItemsTasklet(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @Value("#{jobParameters['sheetName']}") private String sheetName;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        jdbc.update("DELETE FROM tb_sinapi_item WHERE tax_relief = ?;", sheetName);

        return RepeatStatus.FINISHED;
    }
}
