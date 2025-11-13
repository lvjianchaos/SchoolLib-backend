package com.chaos.schoollib.common.convention.errorcode;

/**
 * 图书相关业务错误码
 * A - 客户端 - 0003xx (图书)
 */
public enum BookErrorCode implements IErrorCode {

    BOOK_NOT_FOUND("A000301", "图书不存在"),
    BOOK_STOCK_UPDATE_ERROR("A000302", "图书库存更新失败, 总数不能小于已借出数");

    private final String code;

    private final String message;

    BookErrorCode(String code, String message) {
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