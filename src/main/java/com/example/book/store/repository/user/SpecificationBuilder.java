package com.example.book.store.repository.user;

import com.example.book.store.dto.BookSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(BookSearchParameters searchParameters);
}
