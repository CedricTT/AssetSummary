package com.bookkeeper.AssetSummary.model.exception;

public class HttpException extends GlobalException {
    public HttpException(String code, String message) {
        super(code, message);
    }
}
