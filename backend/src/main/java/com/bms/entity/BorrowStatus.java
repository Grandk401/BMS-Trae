/**
 * 借阅状态枚举
 */
package com.bms.entity;

public enum BorrowStatus {
    
    PENDING("待审核"),
    BORROWING("借阅中"),
    RETURNED("已归还"),
    OVERDUE("已逾期"),
    OVERDUE_RETURNED("已逾期但已归还"),
    REJECTED("已拒绝");
    
    private final String description;
    
    BorrowStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}