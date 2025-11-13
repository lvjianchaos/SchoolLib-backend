package com.chaos.schoollib.mapper;

import com.chaos.schoollib.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {

    // --- 阶段二方法 ---
    List<Book> findAll();

    Book findById(@Param("bookId") Integer bookId);

    // (useGeneratedKeys 在 XML 中配置)
    int insert(Book book);

    int update(Book book);

    int deleteById(@Param("bookId") Integer bookId);


    // ===================================
    // == 阶段四：新增并发控制方法 ==
    // ===================================

    /**
     * 原子化减库存
     * 对应 BookMapper.xml 中的 'decreaseStock'
     * @param bookId 图书ID
     * @return 受影响的行数 (1 表示成功, 0 表示失败/库存不足)
     */
    int decreaseStock(@Param("bookId") Integer bookId);

    /**
     * 原子化加库存 (还书时)
     * 对应 BookMapper.xml 中的 'increaseStock'
     * @param bookId 图书ID
     * @return 受影响的行数
     */
    int increaseStock(@Param("bookId") Integer bookId);
}