/**
 * 公告管理控制器
 * <p>
 * 提供公告的增删改查接口，仅供管理员和图书管理员使用。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.admin;

import com.bms.common.Result;
import com.bms.entity.Announcement;
import com.bms.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告管理控制器
 */
@RestController
@RequestMapping("/announcements")
@Slf4j
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 添加公告
     *
     * @param announcement 公告实体
     * @return 添加结果
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public Result<String> addAnnouncement(@RequestBody Announcement announcement) {
        log.info("添加公告请求: title={}", announcement.getTitle());
        announcementService.addAnnouncement(announcement);
        return Result.success("添加成功");
    }

    /**
     * 更新公告
     *
     * @param id           公告ID
     * @param announcement 公告实体
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public Result<String> updateAnnouncement(@PathVariable Integer id, @RequestBody Announcement announcement) {
        log.info("更新公告请求: id={}, title={}", id, announcement.getTitle());
        announcement.setId(id);
        announcementService.updateAnnouncement(announcement);
        return Result.success("更新成功");
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public Result<String> deleteAnnouncement(@PathVariable Integer id) {
        log.info("删除公告请求: id={}", id);
        announcementService.deleteAnnouncement(id);
        return Result.success("删除成功");
    }

    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告实体
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public Result<Announcement> getAnnouncementById(@PathVariable Integer id) {
        log.info("查询公告请求: id={}", id);
        Announcement announcement = announcementService.getAnnouncementById(id);
        return Result.success("查询成功", announcement);
    }

    /**
     * 查询所有公告
     *
     * @return 公告列表
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public Result<List<Announcement>> getAllAnnouncements() {
        log.info("查询所有公告请求");
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        return Result.success("查询成功", announcements);
    }
}
