/**
 * 定时任务调度类
 * <p>
 * 包含图书管理系统的定时任务，如自动逾期检测、罚款计算等。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.scheduler;

import com.bms.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 借阅定时任务
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BorrowScheduler {

    private final BorrowRecordService borrowRecordService;

    /**
     * 每日凌晨0:01自动扫描并标记逾期借阅记录
     * <p>
     * 扫描条件：
     * - 状态为借阅中（BORROWING）
     * - 归还日期（dueDate）为前一天
     * </p>
     * <p>
     * cron表达式：秒 分 时 日 月 周
     * "0 1 0 * * ?" 表示每天凌晨0点0分1秒执行
     * </p>
     */
    @Scheduled(cron = "0 1 0 * * ?")
    public void autoMarkOverdueRecords() {
        log.info("========== 开始执行每日自动逾期检测任务 ==========");
        try {
            int count = borrowRecordService.autoMarkOverdue();
            log.info("自动逾期检测任务完成，共标记 {} 条逾期记录", count);
        } catch (Exception e) {
            log.error("自动逾期检测任务执行失败", e);
        }
        log.info("========== 每日自动逾期检测任务结束 ==========");
    }

    /**
     * 每日凌晨0:02计算逾期罚款
     * <p>
     * 计算规则：
     * - 从被标记为逾期当天开始计算
     * - 每天罚款0.2元
     * - 单本书封顶10元
     * - 一旦到达封顶则自动禁用该用户账户
     * </p>
     * <p>
     * cron表达式：秒 分 时 日 月 周
     * "0 2 0 * * ?" 表示每天凌晨0点0分2秒执行
     * </p>
     */
    @Scheduled(cron = "0 2 0 * * ?")
    public void autoCalculateOverdueFines() {
        log.info("========== 开始执行每日逾期罚款计算任务 ==========");
        try {
            int[] result = borrowRecordService.autoCalculateOverdueFines();
            log.info("逾期罚款计算任务完成，共处理 {} 条记录，其中 {} 条触发账户禁用", result[0], result[1]);
        } catch (Exception e) {
            log.error("逾期罚款计算任务执行失败", e);
        }
        log.info("========== 每日逾期罚款计算任务结束 ==========");
    }
}
