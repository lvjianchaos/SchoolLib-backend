package com.chaos.schoollib.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 阶段四：全局异常处理器
 * (提前引入阶段五)
 * 职责：捕获 Service 层抛出的异常，并返回统一的 JSON 错误格式
 */
@RestControllerAdvice // 声明这是一个全局异常处理切面
public class GlobalExceptionHandler {

    /**
     * 捕获我们自定义的 BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        // 返回 400 Bad Request
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * 捕获 RuntimeException (例如 "Book not found")
     * (这是一个备用处理器，更推荐为 "Not Found" 创建专门的异常)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        // 捕获所有其他运行时异常，防止服务器 500 错误暴露
        // (在生产环境中，这里应该记录日志)
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred: " + ex.getMessage()));
    }
}