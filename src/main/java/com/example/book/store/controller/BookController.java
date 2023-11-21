package com.example.book.store.controller;

import com.example.book.store.dto.books.BookDto;
import com.example.book.store.dto.books.BookSearchParameters;
import com.example.book.store.dto.books.CreateBookRequestDto;
import com.example.book.store.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class BookController {
    private final BookService bookService;

    @GetMapping("/books")
    @Operation(summary = "Get all books", description = "Get a list of all available books")
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/books/{id}")
    @Operation(summary = "Get a book by id", description = "Get a book by id")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PostMapping("/books")
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.save(bookDto);
    }

    @PutMapping("/books/{id}")
    @Operation(summary = "Update book ", description = "Update the book by ID")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody CreateBookRequestDto requestDto) {
        return bookService.updateById(id, requestDto);
    }

    @DeleteMapping("/books/{id}")
    @Operation(summary = "Delete book", description = "Delete book by id")
    public void deleteBook(@PathVariable Long id){
    }

    @GetMapping("/books/search")
    @Operation(summary = "Search books", description = "Search for books in your library based "
                                                           + "on the specified parameters")
    public List<BookDto> searchBook(BookSearchParameters param) {
        return bookService.search(param);
    }
}
