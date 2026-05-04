/**
 * 普通读者 - 图书控制器
 * <p>
 * 提供图书查询操作 REST API。
 * 仅普通读者可访问。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.reader;

import com.bms.common.Result;
import com.bms.dto.BookSearchDTO;
import com.bms.entity.Book;
import com.bms.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 普通读者 - 图书 REST API 控制器
 */
@RestController
@RequestMapping("/reader/books")
@CrossOrigin
@Slf4j
public class ReaderBookController {

    @Autowired
    private BookService bookService;

    /**
     * 查询所有图书
     *
     * @return 图书列表
     */
    @GetMapping
    public Result<List<Book>> getAllBooks() {
        log.info("普通读者 - 查询所有图书列表");
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
        log.info("普通读者 - 查询图书: bookId={}", id);
        Book book = bookService.getBookById(id);
        return Result.success("查询成功", book);
    }

    /**
     * 模糊搜索图书（多条件组合）
     *
     * @param dto 搜索条件 DTO
     * @return 符合条件的图书列表
     */
    @GetMapping("/search")
    public Result<List<Book>> searchBooks(BookSearchDTO dto) {
        log.info("普通读者 - 模糊搜索图书: title={}, author={}, isbn={}, category={}, publisher={}",
                dto.getTitle(), dto.getAuthor(), dto.getIsbn(), dto.getCategory(), dto.getPublisher());
        List<Book> books = bookService.searchBooks(dto);
        return Result.success("搜索成功", books);
    }

    /**
     * 获取所有图书分类
     *
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        log.info("普通读者 - 查询所有图书分类");
        List<String> categories = bookService.getAllCategories();
        return Result.success("查询成功", categories);
    }
}
