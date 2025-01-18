package com.example.demo.types.Response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public abstract class Response {
    HttpStatus status;
    String message;
    HttpHeaders headers;
    Object body = null;

    public abstract boolean successful();
    public abstract Response textMessage(String message);

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getBody() {
        return body;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
