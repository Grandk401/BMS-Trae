/**
 * 图书管理员 - 图书管理控制器
 * <p>
 * 提供图书的完整 CRUD 操作 REST API。
 * 仅图书管理员可访问。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.librarian;

import com.bms.common.Result;
import com.bms.common.validation.ValidationGroup;
import com.bms.entity.Book;
import com.bms.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书管理员 - 图书管理 REST API 控制器
 */
@RestController
@RequestMapping("/api/librarian/books")
@CrossOrigin
@Slf4j
public class LibrarianBookController {

    @Autowired
    private BookService bookService;

    /**
     * 查询所有图书
     *
     * @return 图书列表
     */
    @GetMapping
    public Result<List<Book>> getAllBooks() {
        log.info("图书管理员 - 查询所有图书列表");
        List<Book> books = bookService.getAllBooks();
        return Result.success("查询成功", books);
    }

    /**
     * 根据 ID 查询图书
     *
     * @param id 图书 ID
     * @return 图书详情
     */
    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Integer id) {
        log.info("图书管理员 - 查询图书: bookId={}", id);
        Book book = bookService.getBookById(id);
        return Result.success("查询成功", book);
    }

    /**
     * 添加新图书
     *
     * @param book 图书对象
     * @return 添加后的图书
     */
    @PostMapping
    public Result<Book> addBook(@Validated(ValidationGroup.Add.class) @RequestBody Book book) {
        log.info("图书管理员 - 添加图书: title={}", book.getTitle());
        Book savedBook = bookService.addBook(book);
        return Result.success("添加成功", savedBook);
    }

    /**
     * 更新图书信息
     *
     * @param id   图书 ID
     * @param book 更新后的图书对象
     * @return 更新后的图书
     */
    @PutMapping("/{id}")
    public Result<Book> updateBook(@PathVariable Integer id, @Validated(ValidationGroup.Update.class) @RequestBody Book book) {
        log.info("图书管理员 - 更新图书: bookId={}", id);
        book.setId(id);
        Book updatedBook = bookService.updateBook(book);
        return Result.success("更新成功", updatedBook);
    }

    /**
     * 删除图书
     *
     * @param id 图书 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteBook(@PathVariable Integer id) {
        log.info("图书管理员 - 删除图书: bookId={}", id);
        bookService.deleteBook(id);
        return Result.success("删除成功", null);
    }
}
