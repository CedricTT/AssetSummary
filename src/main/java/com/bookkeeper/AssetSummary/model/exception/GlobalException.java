package com.bookkeeper.AssetSummary.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{

    private String code;
    private String message;
}
