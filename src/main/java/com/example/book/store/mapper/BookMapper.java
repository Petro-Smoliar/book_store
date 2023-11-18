package com.example.book.store.mapper;

import com.example.book.store.config.MapperConfig;
import com.example.book.store.dto.books.BookDto;
import com.example.book.store.dto.books.CreateBookRequestDto;
import com.example.book.store.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
