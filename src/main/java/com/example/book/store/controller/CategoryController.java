package com.example.book.store.controller;

import com.example.book.store.dto.books.BookDtoWithoutCategoryIds;
import com.example.book.store.dto.category.CategoryDto;
import com.example.book.store.service.BookService;
import com.example.book.store.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for managing category")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @Operation(summary = "Create a new category", description = "Requires admin role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/categories")
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @Operation(summary = "Retrieve all categories", description = "Requires user role")
    @GetMapping("/categories")
    public List<CategoryDto> getAll() {
        return categoryService.findAll();
    }

    @Operation(summary = "Retrieve a specific category by its ID",
               description = "Requires user role")
    @GetMapping("/categories/{id}")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "Update a specific category", description = "Requires admin role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/categories/{id}")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.update(id, categoryDto);
    }

    @Operation(summary = "Delete a specific category", description = "Requires admin role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @Operation(summary = "Retrieve books by a specific category",
               description = "Requires user role")
    @GetMapping("/{id}/books")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(@PathVariable Long id) {
        return bookService.getBooksByCategoryId(id);
    }
}
