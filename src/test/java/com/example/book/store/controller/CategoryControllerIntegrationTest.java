package com.example.book.store.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.book.store.dto.books.BookDtoWithoutCategoryIds;
import com.example.book.store.dto.category.CategoryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class CategoryControllerIntegrationTest {
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
                    "database/sql/add/add_book_and_category_to_books_categories_tables.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @Sql(
            scripts = "classpath:database/sql/remove/"
                          + "remove_category_name_category3_to_categories_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "admin",
                    roles = "ADMIN")
    @DisplayName("Create a new category with admin role should return a category DTO")
    void createCategory_AdminRole_ShouldReturnCategoryDto() throws Exception {
        //Given
        CategoryDto newCategoryDto = new CategoryDto().setName("category3");
        CategoryDto expected = new CategoryDto().setName("category3");

        String request = objectMapper.writeValueAsString(newCategoryDto);
        //When
        MvcResult result =
                mockMvc.perform(
                        post("/categories")
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        //Then
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user",
                  roles = "USER")
    @DisplayName("Get all category should return a list of categories")
    void getAll_ShouldReturnListOfCategories() throws Exception {
        //Given
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto().setName("category1"));
        expected.add(new CategoryDto().setName("category2"));
        //When
        MvcResult result =
                mockMvc.perform(
                        get("/categories").contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        //then
        CategoryDto[] actual = objectMapper.readValue(
            result.getResponse().getContentAsByteArray(), CategoryDto[].class
        );
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @WithMockUser(username = "user",
                  roles = "USER")
    @DisplayName("Get a category by ID should return a category DTO")
    void getCategoryById_ShouldReturnCategoryDto() throws Exception {
        //Given
        CategoryDto expected = new CategoryDto().setName("category1");
        long id = 1L;
        //When
        MvcResult result =
                mockMvc.perform(
                        get("/categories/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        CategoryDto actual =
                objectMapper.readValue(
                    result.getResponse().getContentAsByteArray(), CategoryDto.class
                );

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin",
                  roles = "ADMIN")
    @DisplayName("Update category with admin role should return updated category DTO")
    @Sql(scripts = "classpath:database/sql/add/add_category_name_category3_to_categories_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/sql/remove/remove_category_id_3.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_AdminRole_ShouldReturnUpdatedCategoryDto() throws Exception {
        // Given
        long id = 3;

        CategoryDto expected = new CategoryDto().setName("new name").setDescription("description");
        CategoryDto updateDto = new CategoryDto().setName("new name").setDescription("description");
        String request = objectMapper.writeValueAsString(updateDto);
        //When
        MvcResult result =
                mockMvc.perform(
                        put("/categories/{id}", id)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class
        );

        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "");
    }

    @Test
    @WithMockUser(username = "admin",
                  roles = "ADMIN")
    @DisplayName("Delete category should return 204 No Content")
    @Sql(scripts = "classpath:database/sql/add/add_category_name_category3_to_categories_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteCategory_AdminRole_ShouldReturnNoContent() throws Exception {
        // Given
        long id = 3L;

        // When
        MvcResult result =
                mockMvc.perform(
                        delete("/categories/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseBody.isEmpty());
    }

    @Test
    @WithMockUser(username = "user",
                  roles = "USER")
    @DisplayName("Retrieve books by a specific category "
                     + "should return a list of BookDtoWithoutCategoryIds")
    void getBooksByCategoryId_UserRole_ShouldReturnListOfBooks() throws Exception {
        // Given
        long categoryId = 1L;

        // When
        MvcResult result =
                mockMvc.perform(
                        get("/{id}/books", categoryId)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

        // Then
        List<BookDtoWithoutCategoryIds> actual =
                Arrays.asList(objectMapper.readValue(
                    result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class
                ));
        Assertions.assertEquals(2, actual.size());
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
