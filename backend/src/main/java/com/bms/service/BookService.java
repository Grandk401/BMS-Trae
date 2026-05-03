/**
 * 图书服务层
 * <p>
 * 提供图书相关的业务逻辑处理，包括图书的增删改查功能。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.service;

import com.bms.dto.BookSearchDTO;
import com.bms.entity.Book;
import com.bms.exception.BusinessException;
import com.bms.mapper.BookMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图书业务服务类
 */
@Service
@Slf4j
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    /**
     * 获取所有图书
     *
     * @return 图书列表
     */
    public List<Book> getAllBooks() {
        log.info("查询所有图书");
        List<Book> books = bookMapper.findAll();
        log.info("查询到图书数量: {}", books.size());
        return books;
    }

    /**
     * 根据 ID 获取图书
     *
     * @param id 图书 ID
     * @return 图书详情
     */
    public Book getBookById(Integer id) {
        log.info("查询图书详情: bookId={}", id);
        Book book = bookMapper.findById(id);
        if (book == null) {
            log.warn("查询图书失败: 图书不存在, bookId={}", id);
            throw BusinessException.bookNotFound();
        }
        log.info("查询图书成功: bookId={}, title={}", id, book.getTitle());
        return book;
    }

    /**
     * 添加新图书
     *
     * @param book 图书对象
     * @return 添加后的图书
     */
    public Book addBook(Book book) {
        log.info("添加图书: title={}, isbn={}", book.getTitle(), book.getIsbn());
        
        Book existingBook = bookMapper.findByIsbn(book.getIsbn());
        if (existingBook != null) {
            log.warn("添加图书失败: 图书已存在, isbn={}", book.getIsbn());
            throw BusinessException.bookExists();
        }
        
        int rows = bookMapper.insert(book);
        if (rows <= 0) {
            log.warn("添加图书失败: title={}", book.getTitle());
            throw BusinessException.dbOperationFailure();
        }
        log.info("添加图书成功: bookId={}, title={}", book.getId(), book.getTitle());
        return book;
    }

    /**
     * 更新图书信息
     *
     * @param book 更新后的图书对象
     * @return 更新后的图书
     */
    public Book updateBook(Book book) {
        log.info("更新图书: bookId={}, title={}", book.getId(), book.getTitle());
        
        Book existingBook = bookMapper.findById(book.getId());
        if (existingBook == null) {
            log.warn("更新图书失败: 图书不存在, bookId={}", book.getId());
            throw BusinessException.bookNotFound();
        }
        
        int rows = bookMapper.update(book);
        if (rows <= 0) {
            log.warn("更新图书失败: bookId={}", book.getId());
            throw BusinessException.dbOperationFailure();
        }
        log.info("更新图书成功: bookId={}", book.getId());
        return book;
    }

    /**
     * 删除图书
     *
     * @param id 图书 ID
     */
    public void deleteBook(Integer id) {
        log.info("删除图书: bookId={}", id);

        Book existingBook = bookMapper.findById(id);
        if (existingBook == null) {
            log.warn("删除图书失败: 图书不存在, bookId={}", id);
            throw BusinessException.bookNotFound();
        }

        int rows = bookMapper.deleteById(id);
        if (rows <= 0) {
            log.warn("删除图书失败: bookId={}", id);
            throw BusinessException.dbOperationFailure();
        }
        log.info("删除图书成功: bookId={}", id);
    }

    /**
     * 模糊搜索图书
     *
     * 支持多条件组合搜索，所有条件都是可选的，满足的条件会进行模糊匹配
     *
     * @param dto 搜索条件 DTO
     * @return 符合条件的图书列表
     */
    public List<Book> searchBooks(BookSearchDTO dto) {
        log.info("模糊搜索图书: title={}, author={}, isbn={}, category={}, publisher={}",
                dto.getTitle(), dto.getAuthor(), dto.getIsbn(), dto.getCategory(), dto.getPublisher());

        List<Book> books = bookMapper.searchBooks(dto);
        log.info("搜索到图书数量: {}", books.size());
        return books;
    }
}
