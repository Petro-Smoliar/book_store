package com.example.book.store.repository.book;

import com.example.book.store.dto.BookSearchParameters;
import com.example.book.store.model.Book;
import com.example.book.store.repository.user.SpecificationBuilder;
import com.example.book.store.repository.user.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.price() != null && searchParameters.price().length == 2) {
            spec =
                spec.and(bookSpecificationProviderManager.getSpecificationProvider("price")
                             .getSpecification(searchParameters.price()));
        }
        if (searchParameters.author() != null && searchParameters.author().length > 0) {
            spec =
                spec.and(bookSpecificationProviderManager.getSpecificationProvider("author")
                             .getSpecification(searchParameters.author()));
        }
        return spec;
    }
}
