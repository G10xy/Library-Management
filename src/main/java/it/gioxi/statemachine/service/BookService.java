package it.gioxi.statemachine.service;

import it.gioxi.statemachine.repository.BookRepository;
import it.gioxi.statemachine.model.BookEntity;
import it.gioxi.statemachine.model.BookRequest;
import it.gioxi.statemachine.model.BookRequestUpdate;

import it.gioxi.statemachine.model.BookResponse;
import it.gioxi.statemachine.model.enums.BookStates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public Collection<BookResponse> findAll() { return bookRepository.findAll().stream().map(x -> new BookResponse(x.getId(), x.getTitle(), x.getState())).collect(Collectors.toList()); }

    public BookEntity findById(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }

    public Optional<BookEntity>  findByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public BookResponse findBookResponseById(Long id) {
        var book = findById(id);
        return new BookResponse(book.getId(), book.getTitle(), book.getState());
    }

    public BookResponse findBookResponseByTitle(String title) {
        return findByTitle(title).map(x -> new BookResponse(x.getId(), x.getTitle(), x.getState())).orElseThrow();
    }

    @Transactional
    public void update(BookRequestUpdate requestUpdate) {
        var book = findById(requestUpdate.getId());
        book.setTitle(requestUpdate.getTitle());
    }

    @Transactional
    public void updateStatus(Long id, BookStates state) {
        var book = findById(id);
        book.setState(state);
    }

    @Transactional
    public void save(BookRequest request) {
        var newBook = new BookEntity();
        newBook.setState(BookStates.AVAILABLE);
        newBook.setTitle(request.getTitle());
        bookRepository.save(newBook);
    }

}
