package com.tomazbr9.buildprice.batch.step1;

import com.tomazbr9.buildprice.dto.sinapi.SinapiItemDTO;
import com.tomazbr9.buildprice.entity.SinapiItem;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@StepScope
public class SinapiItemProcessor
        implements ItemProcessor<SinapiItemDTO, List<SinapiItem>> {

    @Override
    public List<SinapiItem> process(SinapiItemDTO item) {

        List<SinapiItem> items = new ArrayList<>();

        item.pricesForUf().forEach((uf, price) -> {
            items.add(new SinapiItem(
                    null,
                    item.codSinapi(),
                    item.description(),
                    item.classification(),
                    item.unit(),
                    uf,
                    price,
                    item.taxRelief(),
                    LocalDate.now()
            ));
        });

        return items;
    }
}


