package com.chaos.schoollib.common.exception;

/**
 * 自定义业务异常
 * 用于处理如 "库存不足", "重复借阅" 等情况
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}