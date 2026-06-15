package com.sourashis.quizapp.core.response;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableUtil {

    private PageableUtil() {}

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_PAGE_NUMBER = 10000;

    public static Pageable safe(Pageable pageable, Set<String> allowedSortProperties) {
        int page = Math.min(pageable.getPageNumber(), MAX_PAGE_NUMBER);
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);

        if (!pageable.getSort().isUnsorted()) {
            Sort safeSort = Sort.unsorted();
            for (Sort.Order order : pageable.getSort()) {
                if (allowedSortProperties.contains(order.getProperty())) {
                    safeSort = safeSort.and(Sort.by(order.getDirection(), order.getProperty()));
                }
            }
            return PageRequest.of(page, size, safeSort);
        }
        return PageRequest.of(page, size, pageable.getSort());
    }
}
