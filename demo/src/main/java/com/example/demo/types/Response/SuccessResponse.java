package com.example.demo.types.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class SuccessResponse<T> extends Response {
    private T body;

    public static <E> SuccessResponse<E> httpStatus(HttpStatus status) {
        SuccessResponse<E> res = new SuccessResponse<>();
        res.setStatus(status);
        return res;
    }

    public SuccessResponse<T> textMessage(String message) {
        this.setMessage(message);
        return this;
    }

    public SuccessResponse<T> setHttpHeaders(HttpHeaders headers) {
        this.setHeaders(headers);
        return this;
    }

    public SuccessResponse<T> build(T body) {
        this.setBody(body);
        return this;
    }

    public boolean successful() {
        return true;
    }

    @Override
    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
