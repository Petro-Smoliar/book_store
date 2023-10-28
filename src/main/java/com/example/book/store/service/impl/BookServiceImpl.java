package com.example.book.store.service.impl;

import com.example.book.store.dto.BookDto;
import com.example.book.store.dto.CreateBookRequestDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.BookMapper;
import com.example.book.store.model.Book;
import com.example.book.store.repository.BookRepository;
import com.example.book.store.service.BookService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookRepository bookRepository;

    @Override
    public BookDto save(CreateBookRequestDto bookRequestDto) {
        Book newbook = bookRepository.save(bookMapper.toModel(bookRequestDto));
        return bookMapper.toDto(newbook);
    }

    @Override
    public BookDto getById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Book not found at index:" + id)));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                       .map(book -> bookMapper.toDto(book))
                       .collect(Collectors.toList());
    }
}
