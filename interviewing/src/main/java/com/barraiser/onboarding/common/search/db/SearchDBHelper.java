package com.barraiser.onboarding.common.search.db;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class SearchDBHelper {
    public Pageable getPageRequest(final SearchQuery searchQuery) {
        final Integer pageNumber = searchQuery.getPageNumber();
        final Integer pageSize = searchQuery.getPageSize();
        final Sort sortRequest = this.getSortRequest(searchQuery.getSortBy());

        return PageRequest.of(pageNumber, pageSize, sortRequest);
    }

    private Sort getSortRequest(final List<SearchOrder> searchOrders) {
        List<Sort.Order> orders = new ArrayList<>();
        for(final SearchOrder searchOrder : searchOrders) {
            if(searchOrder.getSortByAscending()) {
                orders.add(Sort.Order.asc(searchOrder.getField()));
            }
            else {
                orders.add(Sort.Order.desc(searchOrder.getField()));
            }
        }
        return Sort.by(orders);
    }
}
