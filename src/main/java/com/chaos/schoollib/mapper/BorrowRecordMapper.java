package com.chaos.schoollib.mapper;

import com.chaos.schoollib.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 借阅记录 Mapper
 */
@Mapper
public interface BorrowRecordMapper {

    /**
     * 插入一条新的借阅记录
     * @param record 记录
     * @return 受影响行数
     */
    int insert(BorrowRecord record);

    /**
     * 根据 ID 查找记录
     * @param recordId 记录ID
     * @return 借阅记录
     */
    BorrowRecord findById(@Param("recordId") Integer recordId);

    /**
     * 更新记录 (例如：还书时)
     * @param record 记录
     * @return 受影响行数
     */
    int update(BorrowRecord record);

    /**
     * 根据用户 ID 查找所有借阅记录
     * @param userId 用户ID
     * @return 记录列表
     */
    List<BorrowRecord> findByUserId(@Param("userId") Integer userId);

    /**
     * 查找某用户对某本书的未还记录
     * @param userId 用户ID
     * @param bookId 图书ID
     * @param status 状态 (例如 'borrowed')
     * @return 记录
     */
    BorrowRecord findActiveRecordByUserAndBook(
            @Param("userId") Integer userId,
            @Param("bookId") Integer bookId,
            @Param("status") String status
    );
}