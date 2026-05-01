package com.tomazbr9.buildprice.integration.repository;

import com.tomazbr9.buildprice.entity.Project;
import com.tomazbr9.buildprice.entity.ProjectItem;
import com.tomazbr9.buildprice.entity.SinapiItem;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.ProjectItemRepository;
import com.tomazbr9.buildprice.repository.ProjectRepository;
import com.tomazbr9.buildprice.repository.SinapiItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProjectItemRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SinapiItemRepository sinapiItemRepository;

    @Autowired
    private ProjectItemRepository projectItemRepository;

    @Test
    @DisplayName("Should save ProjectItem successfully")
    void shouldSaveProjectItem() {

        Project project = new Project();
        project.setNameWork("Projeto Integração");
        project = projectRepository.save(project);

        SinapiItem sinapiItem = new SinapiItem();
        sinapiItem.setDescription("Item Teste");
        sinapiItem = sinapiItemRepository.save(sinapiItem);

        ProjectItem item = new ProjectItem();
        item.setQuantity(5);
        item.setPrice(new BigDecimal("100.50"));

        item.setSinapiItem(sinapiItem);

        ProjectItem saved = projectItemRepository.save(item);

        assertNotNull(saved.getId());
        assertEquals(5, saved.getQuantity());
        assertEquals(new BigDecimal("100.50"), saved.getPrice());
    }

    @Test
    @DisplayName("Should not save without project")
    void shouldNotSaveWithoutProject() {

        SinapiItem sinapiItem = new SinapiItem();
        sinapiItem.setDescription("Item Teste");
        sinapiItem = sinapiItemRepository.save(sinapiItem);

        ProjectItem item = new ProjectItem();
        item.setQuantity(1);
        item.setPrice(new BigDecimal("10.00"));
        item.setSinapiItem(sinapiItem);


        assertThrows(DataIntegrityViolationException.class, () -> {
            projectItemRepository.saveAndFlush(item);
        });
    }

    @Test
    @DisplayName("Should not save without sinapiItem")
    void shouldNotSaveWithoutSinapiItem() {

        Project project = new Project();
        project.setNameWork("Projeto FK");
        project = projectRepository.save(project);

        ProjectItem item = new ProjectItem();
        item.setQuantity(1);
        item.setPrice(new BigDecimal("10.00"));
        item.setSinapiItem(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            projectItemRepository.saveAndFlush(item);
        });
    }
}
