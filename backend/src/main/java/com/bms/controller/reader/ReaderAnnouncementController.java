/**
 * 读者公告控制器
 * <p>
 * 提供读者查看公告的接口。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.reader;

import com.bms.common.Result;
import com.bms.entity.Announcement;
import com.bms.service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 读者公告控制器
 */
@RestController
@RequestMapping("/api/reader/announcements")
@Slf4j
public class ReaderAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 查询启用的公告（读者可见）
     *
     * @return 启用的公告列表
     */
    @GetMapping
    @PreAuthorize("hasRole('READER')")
    public Result<List<Announcement>> getEnabledAnnouncements() {
        log.info("读者查询公告请求");
        List<Announcement> announcements = announcementService.getEnabledAnnouncements();
        return Result.success("查询成功", announcements);
    }
}
