package com.example.book.store.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.example.book.store.dto.books.BookDto;
import com.example.book.store.dto.books.BookDtoWithoutCategoryIds;
import com.example.book.store.dto.books.BookSearchParameters;
import com.example.book.store.dto.books.CreateBookRequestDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.BookMapper;
import com.example.book.store.model.Book;
import com.example.book.store.repository.book.BookRepository;
import com.example.book.store.repository.book.BookSpecificationBuilder;
import com.example.book.store.service.impl.BookServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Save Book with Valid Request should return saved BookDto")
    void saveBook_WithValidBookRequest_ShouldReturnSavedBookDto() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Title");
        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle(requestDto.getTitle());
        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);

        // When
        BookDto actual = bookService.save(requestDto);

        //Then
        assertEquals(expected, actual);
        Mockito.verify(bookMapper, Mockito.times(1)).toModel(requestDto);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
        Mockito.verify(bookMapper, Mockito.times(1)).toDto(book);
    }

    @Test
    @DisplayName("Get Book by Valid Id should return BookDto")
    void getById_WithValidBookId_ShouldReturnBookDto() {
        //Given
        Long bookId = 1L;
        Book book = new Book();
        BookDto expected = new BookDto();

        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);

        // When
        BookDto actual = bookService.getById(bookId);

        // Then
        assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(bookId);
        Mockito.verify(bookMapper, Mockito.times(1)).toDto(book);
    }

    @Test
    @DisplayName("Get Book by Invalid Id should throw EntityNotFoundException")
    void getById_WithInvalidBookId_ShouldThrowEntityNotFoundException() {
        // Given
        Long bookId = 1L;
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        // when
        assertThrows(EntityNotFoundException.class, () -> bookService.getById(bookId));
        Mockito.verify(bookRepository, Mockito.times(1)).findById(bookId);
        Mockito.verify(bookMapper, Mockito.never()).toDto(any());
    }

    @Test
    @DisplayName("Find All with Pageable should return list of BookDtos")
    void findAll_WithPageable_ShouldReturnListOfBookDtos() {
        // Given
        Book book1 = new Book();
        book1.setTitle("1775");
        Book book2 = new Book();
        book2.setTitle("1773");
        BookDto bookDto1 = new BookDto();
        bookDto1.setTitle("1775");
        BookDto bookDto2 = new BookDto();
        bookDto2.setTitle("1773");
        List<Book> books = Arrays.asList(book1, book2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        Mockito.when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        Mockito.when(bookMapper.toDto(book2)).thenReturn(bookDto2);
        // when
        List<BookDto> actual = bookService.findAll(pageable);
        // Then
        assertEquals(2, actual.size());
        assertEquals(bookDto1, actual.get(0));
        assertEquals(bookDto2, actual.get(1));
        Mockito.verify(bookRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(bookMapper, Mockito.times(1)).toDto(book1);
        Mockito.verify(bookMapper, Mockito.times(1)).toDto(book2);
    }

    @Test
    @DisplayName("Update by Valid Id should update Book and return BookDto")
    void updateById_WithValidId_ShouldUpdateBookAndReturnBookDto() {
        // Given
        Long existingBookId = 1L;
        CreateBookRequestDto updatedBookDto = new CreateBookRequestDto();
        updatedBookDto.setTitle("Updated Title");
        updatedBookDto.setAuthor("Updated Author");

        Book existingBook = new Book();
        existingBook.setId(existingBookId);
        existingBook.setTitle("Original Title");
        existingBook.setAuthor("Original Author");

        Book updatedBook = new Book();

        updatedBook.setTitle("Updated Title");
        updatedBook.setAuthor("Updated Author");

        BookDto updatedBookDtoResponse = new BookDto();
        updatedBookDtoResponse.setId(existingBookId);
        updatedBookDtoResponse.setTitle("Updated Title");
        updatedBookDtoResponse.setAuthor("Updated Author");

        Mockito.when(
                bookRepository.findById(existingBookId)).thenReturn(Optional.of(existingBook)
        );
        Mockito.when(bookMapper.toModel(updatedBookDto)).thenReturn(updatedBook);
        Mockito.when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        Mockito.when(bookMapper.toDto(updatedBook)).thenReturn(updatedBookDtoResponse);
        // When
        BookDto actual = bookService.updateById(existingBookId, updatedBookDto);
        BookDto expected = updatedBookDtoResponse;
        // Then
        assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(existingBookId);
        Mockito.verify(bookRepository, Mockito.times(1))
                .save(updatedBook);
        Mockito.verify(bookMapper, Mockito.times(1))
                .toModel(updatedBookDto);
        Mockito.verify(bookMapper, Mockito.times(1))
                .toDto(updatedBook);
        Mockito.verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update by Invalid Id should throw EntityNotFoundException")
    void updateById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        // Given
        Long nonExistingBookId = 99L;
        CreateBookRequestDto updatedBookDto = new CreateBookRequestDto();
        updatedBookDto.setTitle("Updated Title");
        updatedBookDto.setAuthor("Updated Author");
        Mockito.when(bookRepository.findById(nonExistingBookId)).thenReturn(Optional.empty());
        // When/Then
        String actual = assertThrows(EntityNotFoundException.class,
                () -> bookService.updateById(nonExistingBookId, updatedBookDto)).getMessage();
        String expected = "Not found book by id: " + nonExistingBookId;

        assertEquals(expected, actual);
        Mockito.verify(bookRepository, Mockito.times(1))
                .findById(nonExistingBookId);
        Mockito.verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Search should return list of BookDtos")
    void search_ShouldReturnListOfBookDtos() {
        // Given
        Book book1 = new Book();
        book1.setTitle("Title");
        Book book2 = new Book();
        book2.setTitle("Title2");
        BookDto bookDto1 = new BookDto();
        bookDto1.setTitle("Title");
        BookDto bookDto2 = new BookDto();
        bookDto2.setTitle("Title2");
        List<Book> books = Arrays.asList(book1, book2);
        Specification<Book> bookSpecification = Specification.where(null);
        BookSearchParameters searchParameters = new BookSearchParameters(null, null);

        Mockito.when(bookSpecificationBuilder.build(searchParameters))
                .thenReturn(bookSpecification);
        Mockito.when(bookRepository.findAll(bookSpecification)).thenReturn(books);
        Mockito.when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        Mockito.when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        // When
        List<BookDto> actual = bookService.search(searchParameters);

        // Then
        assertEquals(2, actual.size());
        assertEquals(bookDto1, actual.get(0));
        assertEquals(bookDto2, actual.get(1));

        Mockito.verify(bookSpecificationBuilder, Mockito.times(1))
                .build(searchParameters);
        Mockito.verify(bookRepository, Mockito.times(1))
                .findAll(bookSpecification);
        Mockito.verify(bookMapper, Mockito.times(1))
                .toDto(book1);
        Mockito.verify(bookMapper, Mockito.times(1))
                .toDto(book2);
    }

    @Test
    @DisplayName("Get Books by Category Id with valid category id "
                     + "should return list of BookDtoWithoutCategoryIds")
    void getBooksByCategoryId_WithValidCategoryId_ShouldReturnListOfBookDtoWithoutCategoryIds() {
        // Given
        Long categoryId = 1L;
        Book book1 = new Book();
        Book book2 = new Book();
        List<Book> books = Arrays.asList(book1, book2);

        BookDtoWithoutCategoryIds bookDto1 = new BookDtoWithoutCategoryIds();
        BookDtoWithoutCategoryIds bookDto2 = new BookDtoWithoutCategoryIds();
        Mockito.when(bookRepository.findAllByCategories_Id(categoryId)).thenReturn(books);
        Mockito.when(bookMapper.toDtoWithoutCategories(book1)).thenReturn(bookDto1);
        Mockito.when(bookMapper.toDtoWithoutCategories(book2)).thenReturn(bookDto2);
        List<BookDtoWithoutCategoryIds> expectedBookDtos = Arrays.asList(bookDto1, bookDto2);

        // When
        List<BookDtoWithoutCategoryIds> actual = bookService.getBooksByCategoryId(categoryId);

        // Then
        assertEquals(expectedBookDtos, actual);
        Mockito.verify(bookRepository, Mockito.times(2))
                .findAllByCategories_Id(categoryId);
        Mockito.verify(bookMapper, Mockito.times(2))
                .toDtoWithoutCategories(any());
    }

    @Test
    @DisplayName("Get Books by Category Id with invalid category id should "
                     + "throw EntityNotFoundException")
    void getBooksByCategoryId_WithInvalidCategoryId_ShouldThrowEntityNotFoundException() {
        // Given
        Long categoryId = 1L;
        Mockito.when(bookRepository.findAllByCategories_Id(categoryId))
                .thenReturn(Collections.emptyList());

        // When and Then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.getBooksByCategoryId(categoryId));
        Mockito.verify(bookRepository, Mockito.times(1))
                .findAllByCategories_Id(categoryId);
    }
}
