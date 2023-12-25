package com.example.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.example.book.store.dto.category.CategoryDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.CategoryMapper;
import com.example.book.store.model.Category;
import com.example.book.store.repository.category.CategoryRepository;
import com.example.book.store.service.impl.CategoryServiceImpl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("findAll should return list of CategoryDto")
    void findAll_ShouldReturnListOfCategoryDto() {
        // Given
        Category category1 = new Category();
        Category category2 = new Category();
        List<Category> categories = Arrays.asList(category1, category2);

        CategoryDto categoryDto1 = new CategoryDto();
        CategoryDto categoryDto2 = new CategoryDto();
        Mockito.when(categoryRepository.findAll()).thenReturn(categories);
        Mockito.when(categoryMapper.toDto(category1)).thenReturn(categoryDto1);
        Mockito.when(categoryMapper.toDto(category2)).thenReturn(categoryDto2);
        List<CategoryDto> expected = Arrays.asList(categoryDto1, categoryDto2);
        // When
        List<CategoryDto> actual = categoryService.findAll();
        // Then
        assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(2)).findAll();
        Mockito.verify(categoryMapper, Mockito.times(2)).toDto(any());
    }

    @Test
    @DisplayName("findAll should throw EntityNotFoundException when no categories found")
    void findAll_ShouldThrowEntityNotFoundExceptionWhenNoCategoriesFound() {
        // Given
        Mockito.when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // When and Then
        assertThrows(EntityNotFoundException.class, () -> categoryService.findAll());
        Mockito.verify(categoryRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("getById should return CategoryDto")
    void getById_ShouldReturnCategoryDto() {
        // Given
        Long categoryId = 1L;
        Category category = new Category();
        CategoryDto expected = new CategoryDto();
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        CategoryDto actual = categoryService.getById(categoryId);

        // Then
        assertEquals(expected, actual);
        Mockito.verify(categoryRepository, Mockito.times(1))
                .findById(categoryId);
        Mockito.verify(categoryMapper, Mockito.times(1))
                .toDto(category);
    }

    @Test
    @DisplayName("getById should throw EntityNotFoundException when category not found")
    void getById_ShouldThrowEntityNotFoundExceptionWhenCategoryNotFound() {
        // Given
        Long categoryId = 1L;
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(categoryId));
        Mockito.verify(categoryRepository, Mockito.times(1))
                .findById(categoryId);
    }

    @Test
    @DisplayName("save should return saved CategoryDto")
    void save_ShouldReturnSavedCategoryDto() {
        // Given
        CategoryDto categoryDto = new CategoryDto();
        Category category = new Category();
        Category savedCategory = new Category();
        CategoryDto expected = new CategoryDto();
        Mockito.when(categoryMapper.toModel(categoryDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(savedCategory);
        Mockito.when(categoryMapper.toDto(savedCategory)).thenReturn(expected);

        // When
        CategoryDto actual = categoryService.save(categoryDto);

        // Then
        assertEquals(expected, actual);
        Mockito.verify(categoryMapper, Mockito.times(1))
                .toModel(categoryDto);
        Mockito.verify(categoryRepository, Mockito.times(1))
                .save(category);
        Mockito.verify(categoryMapper, Mockito.times(1))
                .toDto(savedCategory);
    }

    @Test
    @DisplayName("update should return updated CategoryDto")
    void update_ShouldReturnUpdatedCategoryDto() {
        // Given
        Long categoryId = 1L;
        CategoryDto updatedCategoryDto = new CategoryDto();
        Category updatedCategory = new Category();
        Mockito.when(categoryMapper.toModel(updatedCategoryDto)).thenReturn(updatedCategory);
        Mockito.when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        Mockito.when(categoryMapper.toDto(updatedCategory)).thenReturn(updatedCategoryDto);

        // When
        CategoryDto result = categoryService.update(categoryId, updatedCategoryDto);

        // Then
        assertEquals(updatedCategoryDto, result);
        Mockito.verify(categoryMapper, Mockito.times(1))
                .toModel(any(CategoryDto.class));
        Mockito.verify(categoryRepository, Mockito.times(1))
                .save(any(Category.class));
        Mockito.verify(categoryMapper, Mockito.times(1))
                .toDto(updatedCategory);
    }

    @Test
    @DisplayName("deleteById should call repository's deleteById method with correct argument")
    void deleteById_ShouldCallRepositoryDeleteByIdWithCorrectArgument() {
        // Given
        Long categoryId = 1L;

        // When
        categoryService.deleteById(categoryId);

        // Then
        Mockito.verify(categoryRepository, Mockito.times(1))
                .deleteById(categoryId);
    }
}
