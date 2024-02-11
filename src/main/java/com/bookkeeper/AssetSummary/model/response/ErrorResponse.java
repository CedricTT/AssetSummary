package com.bookkeeper.AssetSummary.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class ErrorResponse extends BaseResponse{
    private final int HttpStatus;
    private final String message;
    private String code;
    private String stackTrace;
}
