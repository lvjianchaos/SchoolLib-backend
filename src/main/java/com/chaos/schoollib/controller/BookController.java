package com.chaos.schoollib.controller;

import com.chaos.schoollib.dto.BookDTO;
import com.chaos.schoollib.entity.Book;
import com.chaos.schoollib.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书管理 RESTful API 控制器
 */
@RestController
@RequestMapping("/api/books") // 所有请求都以 /api/books 为前缀
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 1. 创建新书 (POST /api/books)
     * (限制此接口仅对 ADMIN 开放)
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookDTO bookDTO) {
        Book createdBook = bookService.createBook(bookDTO);
        // 返回 201 Created 状态，并在 body 中包含创建的资源
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    /**
     * 2. 获取所有图书 (GET /api/books)
     * (此接口将对所有人开放)
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(books); // 返回 200 OK
    }

    /**
     * 3. 根据 ID 获取单本图书 (GET /api/books/{id})
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Integer bookId) {
        Book book = bookService.getBookById(bookId);
        return ResponseEntity.ok(book); // getBookById 内部会处理找不到的情况
    }

    /**
     * 4. 更新图书 (PUT /api/books/{id})
     * (限制此接口仅对 ADMIN 开放)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Integer bookId,
                                           @Valid @RequestBody BookDTO bookDTO) {
        Book updatedBook = bookService.updateBook(bookId, bookDTO);
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * 5. 删除图书 (DELETE /api/books/{id})
     * (限制此接口仅对 ADMIN 开放)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Integer bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }
}