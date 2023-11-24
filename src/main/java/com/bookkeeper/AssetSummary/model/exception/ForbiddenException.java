package com.bookkeeper.AssetSummary.model.exception;

public class ForbiddenException extends GlobalException {
    public ForbiddenException(String code, String message) {
        super(code, message);
    }
}
