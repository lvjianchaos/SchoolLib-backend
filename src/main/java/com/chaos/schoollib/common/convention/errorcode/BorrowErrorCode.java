package com.chaos.schoollib.common.convention.errorcode;

/**
 * 借阅相关业务错误码
 * A - 客户端 - 0004xx (借阅)
 */
public enum BorrowErrorCode implements IErrorCode {

    STOCK_NOT_SUFFICIENT("A000401", "库存不足或图书不存在"),
    ALREADY_BORROWED("A000402", "你已经借阅了这本书，请勿重复借阅"),
    RECORD_NOT_FOUND("A000403", "借阅记录不存在"),
    INVALID_RETURN("A000404", "该书已归还或状态异常"),
    NO_PERMISSION_FOR_RECORD("A000405", "无权操作他人的借阅记录");

    private final String code;

    private final String message;

    BorrowErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}