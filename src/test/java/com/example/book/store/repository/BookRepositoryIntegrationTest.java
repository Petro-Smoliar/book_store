package com.example.book.store.repository;

import com.example.book.store.model.Book;
import com.example.book.store.repository.book.BookRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryIntegrationTest {
    @Autowired
    private BookRepository bookRepository;

    @Sql(scripts = {
        "classpath:database/sql/add/add_book_and_category_to_books_categories_tables.sql",
        "classpath:database/sql/add/add_category_to_categories_table.sql",
        "classpath:database/sql/add/add_books_to_books_table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
        "classpath:database/sql/remove/remove_books_to_books_table.sql",
        "classpath:database/sql/remove/remove_category_to_category_table.sql",
        "classpath:database/sql/remove/clear_books_category_table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findAllByCategories_Id_ShouldReturnListOfBooks() {
        // Given
        Long categoryId = 1L;
        // When
        List<Book> books = bookRepository.findAllByCategories_Id(categoryId);
        // Then
        Assertions.assertEquals(2, books.size());
    }
}
