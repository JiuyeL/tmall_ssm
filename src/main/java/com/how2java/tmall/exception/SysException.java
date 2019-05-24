package com.how2java.tmall.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 */

public class SysException extends Exception{
    private static final long serialVersionUID = 4055945147128016300L;
    // 异常提示信息
    private String message;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public SysException(String message) {
        this.message = message;
    }
}
