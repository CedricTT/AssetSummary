package com.bookkeeper.AssetSummary.model.exception;

public class AssetNotFound extends GlobalException{
    public AssetNotFound(String code, String message) {
        super(code, message);
    }
}
