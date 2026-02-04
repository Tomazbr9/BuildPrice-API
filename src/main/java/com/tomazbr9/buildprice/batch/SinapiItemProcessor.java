package com.tomazbr9.buildprice.batch;

import com.tomazbr9.buildprice.dto.sinapi.SinapiItemDTO;
import com.tomazbr9.buildprice.entity.SinapiItem;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@StepScope
public class SinapiItemProcessor implements ItemProcessor<SinapiItemDTO, SinapiItem> {

    private Iterator<SinapiItem> currentIterator;

    @Override
    public SinapiItem process(SinapiItemDTO item) {

        if (currentIterator == null || !currentIterator.hasNext()) {
            List<SinapiItem> items = new ArrayList<>();

            item.pricesForUf().forEach((uf, price) -> {
                if (price != null) {
                    items.add(new SinapiItem(
                            item.codSinapi(),
                            item.description(),
                            item.classification(),
                            item.unit(),
                            uf,
                            price,
                            item.taxRelief()
                    ));
                }
            });

            currentIterator = items.iterator();
        }

        return currentIterator.hasNext() ? currentIterator.next() : null;
    }
}

