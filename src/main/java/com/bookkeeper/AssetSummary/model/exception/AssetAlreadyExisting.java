package com.bookkeeper.AssetSummary.model.exception;

public class AssetAlreadyExisting extends GlobalException{
    public AssetAlreadyExisting(String code, String message) {
        super(code, message);
    }
}
