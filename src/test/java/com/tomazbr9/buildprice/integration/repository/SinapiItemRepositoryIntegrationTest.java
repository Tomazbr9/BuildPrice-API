package com.tomazbr9.buildprice.integration.repository;

import com.tomazbr9.buildprice.entity.SinapiItem;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.SinapiItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class SinapiItemRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SinapiItemRepository repository;

    @Test
    void shouldSaveSinapiItem() {

        SinapiItem item = new SinapiItem(
                null,
                "12345",
                "Descrição teste",
                "Material",
                "UN",
                "SP",
                new BigDecimal("150.50"),
                "N",
                LocalDate.of(2024, 1, 1)
        );

        SinapiItem saved = repository.save(item);

        assertNotNull(saved.getId());
        assertEquals("12345", saved.getCodSinapi());
        assertEquals(new BigDecimal("150.50"), saved.getPrice());
    }

    @Test
    void shouldFindByCodSinapi() {

        SinapiItem item = repository.save(
                new SinapiItem(
                        null,
                        "99999",
                        "Item Busca",
                        "Serviço",
                        "M2",
                        "RJ",
                        new BigDecimal("300.00"),
                        "S",
                        LocalDate.of(2024, 2, 1)
                )
        );

        Optional<SinapiItem> found = repository.findByCodSinapi("99999");

        assertTrue(found.isPresent());
        assertEquals("RJ", found.get().getUf());
    }

}

