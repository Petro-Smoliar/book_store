package com.example.book.store;

import com.example.book.store.mapper.BookMapper;
import com.example.book.store.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookMapper bookMapper;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }
}
