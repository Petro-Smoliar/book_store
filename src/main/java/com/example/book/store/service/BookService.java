package com.example.book.store.service;

import com.example.book.store.dto.BookDto;
import com.example.book.store.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    BookDto getById(Long id);

    List<BookDto> findAll();
}
