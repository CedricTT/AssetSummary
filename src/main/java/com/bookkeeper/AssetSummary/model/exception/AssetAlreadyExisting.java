package com.bookkeeper.AssetSummary.model.exception;

public class AssetAlreadyExisting extends GlobalException{
    public AssetAlreadyExisting(String message, String code) {
        super(message, code);
    }
}
