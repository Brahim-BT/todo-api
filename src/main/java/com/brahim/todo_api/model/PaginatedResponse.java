package com.brahim.todo_api.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> data;
    private PageMetadata page;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PageMetadata {
        private int size;
        private long totalElements;
        private int totalPages;
        private int number;
    }
}
