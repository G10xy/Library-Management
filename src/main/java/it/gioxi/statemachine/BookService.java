package it.gioxi.statemachine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }

    public Optional<Book>  findByTitle(String title) {
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
        var newBook = new Book();
        newBook.setState(BookStates.AVAILABLE);
        newBook.setTitle(request.getTitle());
        bookRepository.save(newBook);
    }

}
