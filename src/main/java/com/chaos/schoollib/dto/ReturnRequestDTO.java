package com.chaos.schoollib.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 阶段四：还书请求 DTO
 * 接收 /api/return 接口的请求体
 */
@Data
public class ReturnRequestDTO {

    @NotNull(message = "借阅记录ID不能为空")
    private Integer recordId;
}