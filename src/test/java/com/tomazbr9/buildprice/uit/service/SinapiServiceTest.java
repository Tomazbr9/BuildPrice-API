package com.tomazbr9.buildprice.uit.service;

import com.tomazbr9.buildprice.entity.SinapiItem;
import com.tomazbr9.buildprice.repository.SinapiItemRepository;
import com.tomazbr9.buildprice.service.SinapiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SinapiServiceTest {

    @Mock
    private SinapiItemRepository sinapiItemRepository;

    @InjectMocks
    private SinapiService sinapiService;

    @Test
    void shouldFilterByUfAndTaxReliefTogether() {
        SinapiItem item = new SinapiItem(
                null,
                "12345",
                "Item ICD",
                "Material",
                "UN",
                "SP",
                new BigDecimal("10.00"),
                "ICD",
                LocalDate.of(2026, 1, 1)
        );
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(sinapiItemRepository.findByDescriptionContainingIgnoreCaseAndUfAndTaxRelief(
                eq(""),
                eq("SP"),
                eq("ICD"),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(item)));

        var response = sinapiService.searchItems("", "sp", "icd", 0, 20);

        assertEquals(1, response.content().size());
        assertEquals("ICD", response.content().getFirst().taxRelief());

        verify(sinapiItemRepository).findByDescriptionContainingIgnoreCaseAndUfAndTaxRelief(
                eq(""),
                eq("SP"),
                eq("ICD"),
                pageableCaptor.capture()
        );
        verify(sinapiItemRepository, never()).findByDescriptionContainingIgnoreCaseAndUf(
                any(),
                any(),
                any()
        );
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
    }
}
