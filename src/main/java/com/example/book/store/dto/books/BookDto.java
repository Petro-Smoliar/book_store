package com.example.book.store.dto.books;

import com.example.book.store.dto.category.ResponseRequestCategoryDto;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private String isbn;
    private String description;
    private String coverImage;
    private Set<ResponseRequestCategoryDto> categories;
}
