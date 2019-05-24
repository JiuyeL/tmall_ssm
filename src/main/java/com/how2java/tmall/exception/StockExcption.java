package com.how2java.tmall.exception;


/**
 */
public class StockExcption extends Exception {
    private String message;

    public StockExcption(String message){
        this.message = message;
    }
    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
