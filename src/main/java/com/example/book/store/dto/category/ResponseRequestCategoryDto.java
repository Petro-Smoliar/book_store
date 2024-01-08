package com.example.book.store.dto.category;

import lombok.Data;

@Data
public class ResponseRequestCategoryDto {
    private Long id;

    public ResponseRequestCategoryDto(Long id) {
        this.id = id;
    }

    public ResponseRequestCategoryDto() {
    }
}
