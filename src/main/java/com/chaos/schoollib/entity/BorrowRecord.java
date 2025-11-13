package com.chaos.schoollib.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录实体 (POJO)
 * 对应 'BorrowRecord' 表
 */
@Data
public class BorrowRecord {

    private Integer recordID;
    private Integer userID;
    private Integer bookID;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status; // 'borrowed', 'returned', 'overdue'

    // (为简单起见，暂不包含 User 和 Book 的
    // 嵌套对象，以避免复杂的 MyBatis
    // 'association' 映射，我们可以在 Service
    // 层需要时单独查询它们)
}