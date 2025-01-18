package com.example.demo.types.Response;

import org.springframework.http.HttpStatus;

public class ErrorResponse extends Response {
    public static ErrorResponse httpStatus(HttpStatus status) {
        ErrorResponse res = new ErrorResponse();
        res.setStatus(status);
        return res;
    }

    public ErrorResponse textMessage(String message) {
        this.setMessage(message);
        return this;
    }

    public boolean successful() {
        return false;
    }
}
