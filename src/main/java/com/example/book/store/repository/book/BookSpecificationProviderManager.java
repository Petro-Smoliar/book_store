package com.example.book.store.repository.book;

import com.example.book.store.model.Book;
import com.example.book.store.repository.user.SpecificationProvider;
import com.example.book.store.repository.user.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                   .filter(p -> p.getKey().equals(key))
                   .findFirst()
                   .orElseThrow(() -> new RuntimeException("Specification provider not found for "
                                                               + "key:" + key));
    }
}
