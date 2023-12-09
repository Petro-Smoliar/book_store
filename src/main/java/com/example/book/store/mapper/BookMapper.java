package com.example.book.store.mapper;

import com.example.book.store.config.MapperConfig;
import com.example.book.store.dto.books.BookDto;
import com.example.book.store.dto.books.BookDtoWithoutCategoryIds;
import com.example.book.store.dto.books.CreateBookRequestDto;
import com.example.book.store.model.Book;
import com.example.book.store.model.Category;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategories(book.getCategories().stream()
                                  .map(c -> new Category(c.getId()))
                                  .collect(Collectors.toSet()));
    }
}
