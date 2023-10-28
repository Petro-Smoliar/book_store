package com.example.book.store.repository.spec;

import com.example.book.store.model.Book;
import com.example.book.store.repository.user.SpecificationProvider;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecification implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "price";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        BigDecimal minPrice = new BigDecimal(params[0]);
        BigDecimal maxPrice = new BigDecimal(params[1]);
        return (root, query, criteriaBuilder) ->
                   criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
    }
}
