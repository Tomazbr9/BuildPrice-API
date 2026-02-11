package com.tomazbr9.buildprice.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SinapiJobCleanupListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution){

        String filePath = jobExecution.getJobParameters().getString("tempFile");

        if(filePath == null) return;

        try {
            Path path = Path.of(filePath);

            if(Files.exists(path)){
                Files.delete(path);
            }
        } catch (Exception e){
            throw new FailedRemovingTempFileException("Erro ao remover arquivo temporario");
        }
    }

}
