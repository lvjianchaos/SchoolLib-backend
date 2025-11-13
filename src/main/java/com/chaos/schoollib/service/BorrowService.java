package com.chaos.schoollib.service;

import com.chaos.schoollib.entity.BorrowRecord;
import java.util.List;

/**
 * 借阅业务接口
 */
public interface BorrowService {

    /**
     * 借书
     * @param userId 借书的用户 ID (来自 JWT)
     * @param bookId 要借的图书 ID
     * @return 创建的借阅记录
     */
    BorrowRecord borrowBook(Integer userId, Integer bookId);

    /**
     * 还书
     * @param userId 还书的用户 ID (来自 JWT, 用于验证)
     * @param recordId 要还的借阅记录 ID
     * @return 更新后的借阅记录
     */
    BorrowRecord returnBook(Integer userId, Integer recordId);

    /**
     * 获取当前登录用户的借阅记录
     * @param userId 用户 ID (来自 JWT)
     * @return 记录列表
     */
    List<BorrowRecord> getMyRecords(Integer userId);
}