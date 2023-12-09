package com.example.book.store.service.impl;

import com.example.book.store.dto.category.CategoryDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.CategoryMapper;
import com.example.book.store.model.Category;
import com.example.book.store.repository.category.CategoryRepository;
import com.example.book.store.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll() {
        if (!categoryRepository.findAll().isEmpty()) {
            return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
        } else {
            throw new EntityNotFoundException("Not found list categories");
        }
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(
            categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Not found category by id: " + id)
            )
        );
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.toModel(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category updateCategory = categoryMapper.toModel(categoryDto);
        updateCategory.setId(id);
        return categoryMapper.toDto(categoryRepository.save(updateCategory));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
