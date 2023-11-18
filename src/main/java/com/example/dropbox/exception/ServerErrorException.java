package com.example.dropbox.exception;

public class ServerErrorException extends BaseException {

    public ServerErrorException(String description) {
        super("server_error", description);
    }
}
