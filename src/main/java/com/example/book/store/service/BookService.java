package com.example.book.store.service;

import com.example.book.store.dto.books.BookDto;
import com.example.book.store.dto.books.BookSearchParameters;
import com.example.book.store.dto.books.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    BookDto getById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    List<BookDto> search(BookSearchParameters param);
}
