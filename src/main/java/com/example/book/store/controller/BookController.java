package com.example.book.store.controller;

import com.example.book.store.dto.BookDto;
import com.example.book.store.dto.BookSearchParameters;
import com.example.book.store.dto.CreateBookRequestDto;
import com.example.book.store.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/books/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PostMapping("/books")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @PutMapping("/books/{id}")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody CreateBookRequestDto requestDto) {
        return bookService.updateById(id, requestDto);
    }

    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id){
    }

    @GetMapping("/books/search")
    public List<BookDto> searchBook(BookSearchParameters param) {
        return bookService.search(param);
    }
}
