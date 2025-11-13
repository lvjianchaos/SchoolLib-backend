package com.chaos.schoollib.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 借书请求 DTO
 * 接收 /api/borrow 接口的请求体
 */
@Data
public class BorrowRequestDTO {

    @NotNull(message = "图书ID不能为空")
    private Integer bookId;

    // 注意：UserID 将从 JWT Token 中获取，
    // 而不是由前端在 DTO 中传递，这样更安全。
}