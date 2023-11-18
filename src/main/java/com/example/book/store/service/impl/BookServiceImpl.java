package com.example.book.store.service.impl;

import com.example.book.store.dto.books.BookDto;
import com.example.book.store.dto.books.BookSearchParameters;
import com.example.book.store.dto.books.CreateBookRequestDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.BookMapper;
import com.example.book.store.model.Book;
import com.example.book.store.repository.book.BookRepository;
import com.example.book.store.repository.book.BookSpecificationBuilder;
import com.example.book.store.service.BookService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookSpecificationBuilder bookSpecificationBuilder;

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
    public BookDto updateById(Long id, CreateBookRequestDto requestDto) {
        Book bookById = bookRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Not found book by id: "
                                                                               + id));
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                       .map(bookMapper::toDto)
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookDto> search(BookSearchParameters param) {
        return bookRepository.findAll(bookSpecificationBuilder.build(param)).stream()
                   .map(bookMapper::toDto)
                   .toList();
    }
}
