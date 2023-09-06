package com.example.optimize.mongodb;

import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
public class PageableDTO<T> {

    private List<T> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Integer pageCount;
    private Long total;

    public PageableDTO(boolean isDefault) {
        if (isDefault) {
            this.pageNumber = 0;
            this.pageSize = 0;
            this.pageCount = 0;
            this.content = Collections.emptyList();
            this.total = 0L;
        }
    }

    public PageableDTO(Page<T> page, List<T> content) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.pageCount = page.getTotalPages();
        this.content = content;
        this.total = page.getTotalElements();
    }

    public PageableDTO(Integer number, Integer size, Integer totalPages, Long totalElements, List<T> content) {
        this.pageNumber = number;
        this.pageSize = size;
        this.pageCount = totalPages;
        this.content = content;
        this.total = totalElements;
    }

    public PageableDTO(Page<T> page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.pageCount = page.getTotalPages();
        this.content = page.getContent();
        this.total = page.getTotalElements();
    }
}
