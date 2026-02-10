package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.sinapi.BatchStatusDTO;
import com.tomazbr9.buildprice.dto.sinapi.ImportResponseDTO;
import com.tomazbr9.buildprice.dto.user.UserResponseDTO;
import com.tomazbr9.buildprice.entity.Role;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    @Value("${spring.sinapi.upload-dir}")
    private String uploadDir;

    private final JobLauncher jobLauncher;
    private final Job sinapiJob;
    private final JobExplorer jobExplorer;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    List<String> TABS = Arrays.asList("ISD", "ICD", "ISE");

    public AdminService(JobLauncher jobLauncher, Job sinapiJob, JobExplorer jobExplorer, UserRepository userRepository, RoleRepository roleRepository){
        this.jobLauncher = jobLauncher;
        this.sinapiJob = sinapiJob;
        this.jobExplorer = jobExplorer;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public ImportResponseDTO importSinapi(MultipartFile file, String tab) {

        if(file.isEmpty()){
            throw new RuntimeException("Arquivo vazio");
        }

        if(!TABS.contains(tab)){
            throw new RuntimeException("Aba não encontrada para importação.");
        }

        try {

            Path tempFile = Files.createTempFile("sinapi-", ".xlsx");
            file.transferTo(tempFile.toFile());

            JobParameters params = new JobParametersBuilder()
                    .addString("tempFile", tempFile.toAbsolutePath().toString())
                    .addString("sheetName", tab)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution response = jobLauncher.run(sinapiJob, params);

            return new ImportResponseDTO(response.getJobId(), response.getId(), response.getStatus().name());


        } catch (Exception e){
            throw new RuntimeException("Erro ao executar job SINAPI", e);
        }
    }

    public BatchStatusDTO getImportProcessStatus(Long jobId){
        JobExecution jobExecution = jobExplorer.getJobExecution(jobId);

        if(jobExecution == null){
            throw new RuntimeException("JobExecution não encontrado");
        }

        StepExecution step = jobExecution
                .getStepExecutions()
                .stream()
                .filter(stepExecution -> stepExecution.getStepName().equals("step1"))
                .findFirst()
                .orElse(null);

        return new BatchStatusDTO(
                step != null ? step.getStatus().toString() : null,
                step != null ? step.getReadCount() : 0,
                step != null ? step.getWriteCount() : 0,
                step != null ? step.getCommitCount() : 0,
                step != null ? step.getExitStatus().getExitCode() : null
        );
    }

    public List<UserResponseDTO> getUsers(UUID userId){

        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<User> users = userRepository.findAll();

        Role role = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("Papel de usuário não encontrado"));

        return users.stream()
                .filter(user -> user.getRoles().contains(role))
                .map(user -> new UserResponseDTO(user.getEmail()))
                .toList();

    }
}
