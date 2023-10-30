package com.example.book.store.repository.spec;

import com.example.book.store.model.Book;
import com.example.book.store.repository.user.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecification implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "author";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) ->
                   root.get("author").in(Arrays.stream(params).toArray());
    }
}
