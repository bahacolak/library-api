package com.bahadircolak.library.service;

import com.bahadircolak.library.model.Book;
import com.bahadircolak.library.repository.BookRepository;
import com.bahadircolak.library.web.advice.BookNotFoundException;
import com.bahadircolak.library.web.dto.BookDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookDto createBook(BookDto bookDto) {

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPublicationYear(bookDto.getPublicationYear());

        Book savedBook = bookRepository.save(book);

        BookDto savedBookDTO = new BookDto();
        savedBookDTO.setId(savedBook.getId());
        savedBookDTO.setTitle(savedBook.getTitle());
        savedBookDTO.setAuthor(savedBook.getAuthor());
        savedBookDTO.setPublicationYear(savedBook.getPublicationYear());

        return savedBookDTO;
    }

    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookDto> bookDtoList = new ArrayList<>();

        for (Book book : books) {
            BookDto bookDto = new BookDto();
            bookDto.setId(book.getId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setPublicationYear(book.getPublicationYear());
            bookDtoList.add(bookDto);
        }

        return bookDtoList;
    }

    public BookDto getBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            BookDto bookDto = new BookDto();
            bookDto.setId(book.getId());
            bookDto.setTitle(book.getTitle());
            bookDto.setAuthor(book.getAuthor());
            bookDto.setPublicationYear(book.getPublicationYear());
            return bookDto;
        } else {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
    }


    public BookDto updateBook(Long id, BookDto bookDto) {

        Optional<Book> existingBookOptional = bookRepository.findById(id);

        if (existingBookOptional.isPresent()) {

            Book existingBook = existingBookOptional.get();

            existingBook.setTitle(bookDto.getTitle());
            existingBook.setAuthor(bookDto.getAuthor());
            existingBook.setPublicationYear(bookDto.getPublicationYear());

            existingBook  = bookRepository.save(existingBook);

            BookDto updatedBookDto = new BookDto(
                    existingBook.getId(),
                    existingBook.getTitle(),
                    existingBook.getAuthor(),
                    existingBook.getPublicationYear());

            return updatedBookDto;
        } else {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
    }

    public boolean deleteBook(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            bookRepository.deleteById(id);
            return true;
        } else {
            throw new BookNotFoundException("Book not found with id: " + id);
        }
    }

}
