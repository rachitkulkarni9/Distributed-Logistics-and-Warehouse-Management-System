package com.logistics.common.util;

import com.logistics.common.response.PagedResponse;
import org.springframework.data.domain.Page;

/**
 * Maps a Spring Data {@link Page} to the API's {@link PagedResponse} wrapper.
 */
public final class PageUtils {

    private PageUtils() {}

    public static <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return PagedResponse.<T>builder()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .first(page.isFirst())
            .build();
    }
}
