package com.tomazbr9.buildprice.integration.repository;

import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProjectRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository repository;

    @Test
    @DisplayName("Should save a project successfully")
    void shouldSaveProject() {

        Project project = new Project();
        project.setNameWork("Projeto Teste");

        Project saved = repository.save(project);

        assertNotNull(saved.getId());
        assertEquals("Projeto Teste", saved.getNameWork());
    }

    @Test
    @DisplayName("Should find project by id")
    void shouldFindProjectById() {

        Project project = new Project();
        project.setNameWork("Projeto Busca");

        Project saved = repository.save(project);

        Project found = repository.findById(saved.getId()).orElseThrow();

        assertEquals("Projeto Busca", found.getNameWork());
    }

    @Test
    @DisplayName("Should delete project")
    void shouldDeleteProject() {

        Project project = new Project();
        project.setNameWork("Projeto Delete");

        Project saved = repository.save(project);

        repository.deleteById(saved.getId());

        assertFalse(repository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("Should not save project when name is null")
    void shouldThrowExceptionWhenNameIsNull() {

        Project project = new Project();
        project.setNameWork(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(project);
        });
    }
}
