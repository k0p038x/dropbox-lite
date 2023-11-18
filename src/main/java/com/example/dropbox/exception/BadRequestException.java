package com.example.dropbox.exception;

public class BadRequestException extends BaseException {

    public BadRequestException(String description) {
        super("bad_request_error", description);
    }
}
