package it.gioxi.statemachine.controller;


import it.gioxi.statemachine.model.BookRequest;
import it.gioxi.statemachine.model.BookRequestUpdate;
import it.gioxi.statemachine.model.BookResponse;
import it.gioxi.statemachine.model.enums.BookEvents;
import it.gioxi.statemachine.service.BookService;
import it.gioxi.statemachine.service.BookStatusChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final BookStatusChangeService bookStatusChangeService;

    @GetMapping
    public ResponseEntity<Collection<BookResponse>> findBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findBookResponseById(id));
    }

    @GetMapping("/by-title")
    public ResponseEntity<BookResponse> findBookByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.findBookResponseByTitle(title));
    }

    @PutMapping
    public ResponseEntity<String> updateBook(@RequestBody BookRequestUpdate requestUpdate) {
        bookService.update(requestUpdate);
        return ResponseEntity.ok("Book updated correctly");
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createBook(@RequestBody BookRequest newBook) {
        bookService.save(newBook);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.BORROW_BOOK);
        return ResponseEntity.ok("Book borrowed");
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.RETURN_BOOK);
        return ResponseEntity.ok("Book returned");
    }

    @PatchMapping("/{id}/mark-overdue")
    public ResponseEntity<String> markOverdue(@PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.MARK_OVERDUE);
        return ResponseEntity.ok("Book marked as overdue");
    }

    @PatchMapping("/{id}/mark-issued")
    public ResponseEntity<String> markIssued(@PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.ISSUE_BOOK);
        return ResponseEntity.ok("Book marked as issued");
    }
}
