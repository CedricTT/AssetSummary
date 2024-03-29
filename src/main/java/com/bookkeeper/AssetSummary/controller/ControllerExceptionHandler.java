package com.bookkeeper.AssetSummary.controller;

import com.bookkeeper.AssetSummary.model.exception.ForbiddenException;
import com.bookkeeper.AssetSummary.model.response.ErrorResponse;
import com.bookkeeper.AssetSummary.model.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(GlobalException exception, WebRequest request) {
        log.error("Server error occurred", exception);
        return buildErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleForbiddenException(GlobalException exception, WebRequest request) {
        log.error("Authentication error occurred", exception);
        return buildErrorResponse(exception, HttpStatus.FORBIDDEN, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(GlobalException exception, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .HttpStatus(httpStatus.value())
                .message(exception.getMessage())
                .code(exception.getCode())
                .status("FAILED")
                .requestTime(LocalDateTime.now().withNano(0))
                .build();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
