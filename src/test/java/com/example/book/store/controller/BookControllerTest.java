package com.example.book.store.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.book.store.dto.books.BookDto;
import com.example.book.store.dto.books.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                      .webAppContextSetup(webApplicationContext)
                      .apply(springSecurity())
                      .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/add_books_to_books_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/add_category_to_categories_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource(
                    "database/sql/add/add_book_and_category_to_books_categories_tables.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @WithMockUser(username = "user",
                  roles = "USER")
    @DisplayName("Get all books should return a list of books")
    void getAll_ShouldReturnListOfBooks() throws Exception {
        //Given
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L).setTitle("title1").setAuthor("author1")
                         .setIsbn("book1").setPrice(BigDecimal.valueOf(10.00)));
        expected.add(new BookDto().setId(2L).setTitle("title2").setAuthor("author2")
                         .setIsbn("book2").setPrice(BigDecimal.valueOf(20.00)));
        expected.add(new BookDto().setId(3L).setTitle("title3").setAuthor("author3")
                         .setIsbn("book3").setPrice(BigDecimal.valueOf(30.00)));
        expected.add(new BookDto().setId(4L).setTitle("title4").setAuthor("author4")
                         .setIsbn("book4").setPrice(BigDecimal.valueOf(40.00)));
        expected.add(new BookDto().setId(5L).setTitle("title5").setAuthor("author5")
                         .setIsbn("book5").setPrice(BigDecimal.valueOf(50.00)));
        //When
        MvcResult result =
                mockMvc.perform(
                    get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        //then
        BookDto[] actual = objectMapper.readValue(
            result.getResponse().getContentAsByteArray(), BookDto[].class
        );
        Assertions.assertEquals(5, actual.length);
        EqualsBuilder.reflectionEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "user",
                  roles = "USER")
    @DisplayName("Get a book by ID should return a book DTO")
    void getBookById_ShouldReturnBookDto() throws Exception {
        //Given
        BookDto expected = new BookDto()
                               .setId(1L)
                               .setTitle("title1")
                               .setAuthor("author1")
                               .setIsbn("book1")
                               .setPrice(BigDecimal.valueOf(10.00))
                               .setCategories(Set.of());
        long id = 1L;
        //When
        MvcResult result =
                mockMvc.perform(
                        get("/books/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsByteArray(), BookDto.class);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin",
                  roles = "ADMIN")
    @DisplayName("Create a new book with admin role should return a book DTO")
    @Sql(scripts = "classpath:database/sql/remove/remove_book_title_new_book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_AdminRole_ShouldReturnBookDto() throws Exception {
        //Given
        CreateBookRequestDto newBookDto = new CreateBookRequestDto()
                                              .setTitle("new book")
                                              .setAuthor("new Author")
                                              .setIsbn("newBook")
                                              .setPrice(BigDecimal.valueOf(111.00));
        BookDto expected = new BookDto()
                               .setTitle("new book")
                               .setAuthor("new Author")
                               .setIsbn("newBook")
                               .setPrice(BigDecimal.valueOf(111.00));
        String request = objectMapper.writeValueAsString(newBookDto);
        //When
        MvcResult result =
                mockMvc.perform(
                        post("/books")
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        //Then
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "admin",
                  roles = "ADMIN")
    @DisplayName("Update book with admin role should return updated book DTO")
    @Sql(scripts = "classpath:database/sql/update/update_book_by_id_1.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_AdminRole_ShouldReturnUpdatedBookDto() throws Exception {
        // Given
        long id = 1;

        BookDto expected = new BookDto()
                               .setTitle("new title")
                               .setAuthor("author1")
                               .setIsbn("book1")
                               .setPrice(BigDecimal.valueOf(10.00));
        CreateBookRequestDto updateDto = new CreateBookRequestDto()
                                             .setTitle("new title")
                                             .setAuthor("author1")
                                             .setIsbn("book1")
                                             .setPrice(BigDecimal.valueOf(10.0));
        String request = objectMapper.writeValueAsString(updateDto);
        //When
        MvcResult result =
                mockMvc.perform(
                        put("/books/{id}", id)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class
        );

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "user",
                  roles = "USER")
    @DisplayName("Search books should return a list of matching BookDto objects")
    void searchBook_ShouldReturnListOfBooks() throws Exception {
        // When
        MvcResult result =
                mockMvc.perform(
                        get("/books/search")
                            .param("author", "author1", "author4")
                            .param("price", "9.00", "20.00")
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        // Then
        List<BookDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class)
        );
        Assertions.assertEquals(1, actual.size());
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/remove/remove_category_to_category_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/remove/remove_books_to_books_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/remove/clear_books_category_table.sql"));
        }
    }
}
