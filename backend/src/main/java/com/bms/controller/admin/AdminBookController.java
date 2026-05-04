/**
 * 系统管理员 - 图书管理控制器
 * <p>
 * 提供图书的完整 CRUD 操作 REST API。
 * 仅系统管理员可访问。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.admin;

import com.bms.annotation.OperationLogging;
import com.bms.common.Result;
import com.bms.common.validation.ValidationGroup;
import com.bms.dto.BookSearchDTO;
import com.bms.entity.Book;
import com.bms.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理员 - 图书管理 REST API 控制器
 */
@RestController
@RequestMapping("/admin/books")
@CrossOrigin
@Slf4j
public class AdminBookController {

    @Autowired
    private BookService bookService;

    /**
     * 查询所有图书
     *
     * @return 图书列表
     */
    @GetMapping
    public Result<List<Book>> getAllBooks() {
        log.info("系统管理员 - 查询所有图书列表");
        List<Book> books = bookService.getAllBooks();
        return Result.success("查询成功", books);
    }

    /**
     * 模糊搜索图书（多条件组合）
     *
     * @param dto 搜索条件 DTO
     * @return 符合条件的图书列表
     */
    @GetMapping("/search")
    public Result<List<Book>> searchBooks(BookSearchDTO dto) {
        log.info("系统管理员 - 模糊搜索图书: title={}, author={}, isbn={}, category={}, publisher={}",
                dto.getTitle(), dto.getAuthor(), dto.getIsbn(), dto.getCategory(), dto.getPublisher());
        List<Book> books = bookService.searchBooks(dto);
        return Result.success("搜索成功", books);
    }

    /**
     * 根据 ID 查询图书
     *
     * @param id 图书 ID
     * @return 图书详情
     */
    @GetMapping("/{id}")
    public Result<Book> getBookById(@PathVariable Integer id) {
        log.info("系统管理员 - 查询图书: bookId={}", id);
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
    @OperationLogging(module = "BOOK", type = "ADD", description = "添加图书")
    public Result<Book> addBook(@Validated(ValidationGroup.Add.class) @RequestBody Book book) {
        log.info("系统管理员 - 添加图书: title={}", book.getTitle());
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
    @OperationLogging(module = "BOOK", type = "UPDATE", description = "更新图书")
    public Result<Book> updateBook(@PathVariable Integer id, @Validated(ValidationGroup.Update.class) @RequestBody Book book) {
        log.info("系统管理员 - 更新图书: bookId={}", id);
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
    @OperationLogging(module = "BOOK", type = "DELETE", description = "删除图书")
    public Result<String> deleteBook(@PathVariable Integer id) {
        log.info("系统管理员 - 删除图书: bookId={}", id);
        bookService.deleteBook(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取所有图书分类
     *
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        log.info("系统管理员 - 查询所有图书分类");
        List<String> categories = bookService.getAllCategories();
        return Result.success("查询成功", categories);
    }
}
