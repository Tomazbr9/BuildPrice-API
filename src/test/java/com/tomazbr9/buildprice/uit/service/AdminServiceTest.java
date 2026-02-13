package com.tomazbr9.buildprice.uit.service;

import com.tomazbr9.buildprice.dto.sinapi.BatchStatusDTO;
import com.tomazbr9.buildprice.dto.sinapi.ImportResponseDTO;
import com.tomazbr9.buildprice.dto.user.UserResponseDTO;
import com.tomazbr9.buildprice.entity.Role;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.exception.*;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job sinapiJob;

    @Mock
    private JobExplorer jobExplorer;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JobOperator jobOperator;

    @InjectMocks
    private AdminService adminService;

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(EmptyFileSentException.class,
                () -> adminService.importSinapi(file, "ISD"));
    }

    @Test
    void shouldThrowExceptionWhenTabIsInvalid() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        assertThrows(TabNotFound.class,
                () -> adminService.importSinapi(file, "INVALID"));
    }

    @Test
    void shouldRunJobSuccessfully() throws Exception {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        Path fakePath = Files.createTempFile("test", ".xlsx");

        AdminService spyService = spy(adminService);
        doReturn(fakePath).when(spyService).createTemporaryFile(file);

        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getJobId()).thenReturn(1L);
        when(jobExecution.getId()).thenReturn(100L);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTED);

        when(jobLauncher.run(any(), any()))
                .thenReturn(jobExecution);

        ImportResponseDTO response = spyService.importSinapi(file, "ISD");

        assertEquals(1L, response.jobId());
        assertEquals(100L, response.jobExecutionId());
        assertEquals("STARTED", response.status());
    }

    @Test
    void shouldThrowRunningJobExceptionWhenJobFails() throws Exception {

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        Path fakePath = Files.createTempFile("test", ".xlsx");

        AdminService spyService = spy(adminService);
        doReturn(fakePath).when(spyService).createTemporaryFile(file);

        when(jobLauncher.run(any(), any()))
                .thenThrow(new RuntimeException());

        assertThrows(RunningJobException.class,
                () -> spyService.importSinapi(file, "ISD"));
    }

    @Test
    void shouldReturnBatchStatus() {

        Long jobId = 1L;

        JobExecution jobExecution = mock(JobExecution.class);
        StepExecution stepExecution = mock(StepExecution.class);

        when(stepExecution.getStepName()).thenReturn("step1");
        when(stepExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);
        when(stepExecution.getReadCount()).thenReturn(Long.valueOf(10));
        when(stepExecution.getWriteCount()).thenReturn(Long.valueOf(10));
        when(stepExecution.getCommitCount()).thenReturn(Long.valueOf(5));
        when(stepExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);

        when(jobExecution.getStepExecutions())
                .thenReturn(Set.of(stepExecution));

        when(jobExplorer.getJobExecution(jobId))
                .thenReturn(jobExecution);

        BatchStatusDTO response = adminService.getImportProcessStatus(jobId);

        assertEquals("COMPLETED", response.status());
        assertEquals(10, response.readCount());
        assertEquals(10, response.writeCount());
        assertEquals(5, response.commitCount());
        assertEquals("COMPLETED", response.exitCode());
    }

    @Test
    void shouldThrowWhenJobExecutionNotFound() {

        when(jobExplorer.getJobExecution(1L))
                .thenReturn(null);

        assertThrows(JobExecutionNotFoundException.class,
                () -> adminService.getImportProcessStatus(1L));
    }

    @Test
    void shouldReturnOnlyUsersWithRoleUser() {

        UUID adminId = UUID.randomUUID();

        User admin = new User();
        when(userRepository.findById(adminId))
                .thenReturn(Optional.of(admin));

        Role roleUser = new Role();
        roleUser.setName(RoleName.ROLE_USER);

        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.of(roleUser));

        User user1 = new User();
        user1.setEmail("user1@email.com");
        user1.setRoles(Set.of(roleUser));

        User user2 = new User();
        user2.setEmail("admin@email.com");
        user2.setRoles(Set.of());

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserResponseDTO> response = adminService.getUsers(adminId);

        assertEquals(1, response.size());
        assertEquals("user1@email.com", response.get(0).email());
    }

    @Test
    void shouldStopJobSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();
        Long jobId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));

        JobExecution jobExecution = mock(JobExecution.class);
        when(jobExecution.getId()).thenReturn(100L);

        when(jobExplorer.getJobExecution(jobId))
                .thenReturn(jobExecution);

        adminService.stopJob(jobId, userId);

        verify(jobOperator).stop(100L);
    }

    @Test
    void shouldThrowWhenStoppingNonExistingJob() {

        UUID userId = UUID.randomUUID();
        Long jobId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));

        when(jobExplorer.getJobExecution(jobId))
                .thenReturn(null);

        assertThrows(JobExecutionNotFoundException.class,
                () -> adminService.stopJob(jobId, userId));
    }

    @Test
    void shouldThrowWhenTempFileCreationFails() throws IOException {

        MultipartFile file = mock(MultipartFile.class);

        doThrow(new IOException())
                .when(file).transferTo(any(File.class));

        assertThrows(TempFileCreationFailedException.class,
                () -> adminService.createTemporaryFile(file));
    }

}

