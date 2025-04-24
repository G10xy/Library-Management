package it.gioxi.statemachine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Book Management", description = "APIs for managing books and their states")
public class BookController {

    private final BookService bookService;
    private final BookStatusChangeService bookStatusChangeService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieves a list of all books in the library")
    @ApiResponse(responseCode = "200", description = "Books successfully retrieved",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BookResponse.class)))
    public ResponseEntity<Collection<BookResponse>> findBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a specific book by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<BookResponse> findBookById(
            @Parameter(description = "ID of the book to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(bookService.findBookResponseById(id));
    }

    @GetMapping("/by-title")
    @Operation(summary = "Get book by title", description = "Retrieves a specific book by its title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<BookResponse> findBookByTitle(
            @Parameter(description = "Title of the book to retrieve") @RequestParam String title) {
        return ResponseEntity.ok(bookService.findBookResponseByTitle(title));
    }

    @PutMapping
    @Operation(summary = "Update book details", description = "Updates the details of an existing book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<String> updateBook(
            @Parameter(description = "Updated book details") @RequestBody BookRequestUpdate requestUpdate) {
        bookService.update(requestUpdate);
        return ResponseEntity.ok("Book updated correctly");
    }

    @PostMapping
    @Operation(summary = "Create new book", description = "Adds a new book to the library")
    @ApiResponse(responseCode = "200", description = "Book created successfully",
            content = @Content(schema = @Schema(implementation = HttpStatus.class)))
    public ResponseEntity<HttpStatus> createBook(
            @Parameter(description = "New book details") @RequestBody BookRequest newBook) {
        bookService.save(newBook);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/borrow")
    @Operation(summary = "Borrow a book", description = "Changes the state of a book from AVAILABLE to BORROWED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "400", description = "Book cannot be borrowed",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<String> borrowBook(
            @Parameter(description = "ID of the book to borrow") @PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.BORROW_BOOK);
        return ResponseEntity.ok("Book borrowed");
    }

    @PatchMapping("/{id}/return")
    @Operation(summary = "Return a book", description = "Changes the state of a book from BORROWED or OVERDUE to AVAILABLE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "400", description = "Book cannot be returned",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<String> returnBook(
            @Parameter(description = "ID of the book to return") @PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.RETURN_BOOK);
        return ResponseEntity.ok("Book returned");
    }

    @PatchMapping("/{id}/mark-overdue")
    @Operation(summary = "Mark book as overdue", description = "Changes the state of a book from BORROWED to OVERDUE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book marked as overdue successfully"),
            @ApiResponse(responseCode = "400", description = "Book cannot be marked as overdue",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<String> markOverdue(
            @Parameter(description = "ID of the book to mark as overdue") @PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.MARK_OVERDUE);
        return ResponseEntity.ok("Book marked as overdue");
    }

    @PatchMapping("/{id}/mark-issued")
    @Operation(summary = "Mark book as issued", description = "Changes the state of a book from BORROWED to ISSUED (permanent assignment)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book marked as issued successfully"),
            @ApiResponse(responseCode = "400", description = "Book cannot be marked as issued",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<String> markIssued(
            @Parameter(description = "ID of the book to mark as issued") @PathVariable Long id) throws Exception {
        bookStatusChangeService.doAction(id, BookEvents.ISSUE_BOOK);
        return ResponseEntity.ok("Book marked as issued");
    }
}
