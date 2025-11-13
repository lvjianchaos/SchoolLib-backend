package com.chaos.schoollib.service.impl;

import com.chaos.schoollib.common.exception.BusinessException;
import com.chaos.schoollib.entity.BorrowRecord;
import com.chaos.schoollib.mapper.BookMapper;
import com.chaos.schoollib.mapper.BorrowRecordMapper;
import com.chaos.schoollib.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 阶段四：借阅业务实现
 * (本项目的核心)
 */
@Service
public class BorrowServiceImpl implements BorrowService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Autowired
    public BorrowServiceImpl(BookMapper bookMapper, BorrowRecordMapper borrowRecordMapper) {
        this.bookMapper = bookMapper;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    /**
     * 借书
     * * @Transactional 确保 "减库存" 和 "创建借阅记录"
     * 是一个原子操作 (要么都成功, 要么都回滚)
     */
    @Transactional
    @Override
    public BorrowRecord borrowBook(Integer userId, Integer bookId) {

        // 1. (可选) 检查是否已借阅且未归还
        BorrowRecord activeRecord = borrowRecordMapper.findActiveRecordByUserAndBook(userId, bookId, "borrowed");
        if (activeRecord != null) {
            throw new BusinessException("你已经借阅了这本书，请勿重复借阅");
        }

        // 2. (核心) 尝试原子化减库存
        int affectedRows = bookMapper.decreaseStock(bookId);

        // 3. 检查减库存是否成功
        if (affectedRows == 0) {
            // 如果 = 0, 说明 WHERE BookID = ? AND Stock > 0 未匹配到
            // (即库存不足或图书不存在)
            throw new BusinessException("库存不足或图书不存在");
        }

        // 4. 减库存成功，创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserID(userId);
        record.setBookID(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30)); // 默认30天
        record.setStatus("borrowed");

        borrowRecordMapper.insert(record);

        return record;
    }

    /**
     * 还书
     */
    @Transactional
    @Override
    public BorrowRecord returnBook(Integer userId, Integer recordId) {
        // 1. 查找借阅记录
        BorrowRecord record = borrowRecordMapper.findById(recordId);

        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }

        // 2. 验证：确保是本人还书
        if (!record.getUserID().equals(userId)) {
            throw new BusinessException("无权操作他人的借阅记录");
        }

        // 3. 验证：确保是"已借出"状态 (防止重复还书)
        if (!record.getStatus().equals("borrowed")) {
            throw new BusinessException("该书已归还或状态异常");
        }

        // 4. 更新记录状态
        record.setStatus("returned");
        record.setReturnDate(LocalDateTime.now());
        borrowRecordMapper.update(record);

        // 5. (核心) 原子化加库存
        bookMapper.increaseStock(record.getBookID());

        return record;
    }

    /**
     * 获取我的借阅记录
     */
    @Override
    public List<BorrowRecord> getMyRecords(Integer userId) {
        return borrowRecordMapper.findByUserId(userId);
    }
}