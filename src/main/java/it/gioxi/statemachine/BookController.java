package it.gioxi.statemachine;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final BookStatusChangeService bookStatusChangeService;

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findBookResponseById(id));
    }

    @GetMapping
    public ResponseEntity<BookResponse> findBookByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.findBookResponseByTitle(title));
    }

    @PutMapping
    public ResponseEntity<String> updateBook(@RequestBody BookRequestUpdate requestUpdate) {
        bookService.update(requestUpdate);
        return ResponseEntity.ok("Book updated correctly");
    }

    @PostMapping
    public ResponseEntity<HttpStatus> newBook(@RequestBody BookRequest newBook) {
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

    @PatchMapping("/{id}/markoverdue")
    public ResponseEntity<String> markOverdue(@PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.MARK_OVERDUE);
        return ResponseEntity.ok("Book marked as overdue");
    }
}
