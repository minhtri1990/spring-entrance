package com.entrance.config;

import com.entrance.exception.BadRequestException;
import com.entrance.exception.ResourceNotFoundException;
import com.entrance.exception.ServerErrorException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiCallError<String>> handleBadRequestException(HttpServletRequest request,
                                                                          BadRequestException ex) {
        log.error("BadRequestException {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiCallError<>("Exception", Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<ApiCallError<String>> handleServerErrorException(HttpServletRequest request,
                                                                           ServerErrorException ex) {
        log.error("ServerErrorException {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiCallError<>("Exception", Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiCallError<String>> handleServerErrorException(HttpServletRequest request,
                                                                           ResourceNotFoundException ex) {
        log.error("ServerErrorException {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiCallError<>("Exception", Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiCallError<String>> handleValidationException(HttpServletRequest request,
                                                                          ValidationException ex) {
        log.error("ValidationException {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Validation exception", Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiCallError<String>> handleBindException(HttpServletRequest request,
                                                                    BindException ex) {
        log.error("BindException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Bind exception", ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList())));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiCallError<String>> handleMissingServletRequestParameterException(HttpServletRequest request,
                                                                                              MissingServletRequestParameterException ex) {
        log.error("handleMissingServletRequestParameterException {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .badRequest()
                .body(new ApiCallError<>("Missing request parameter", Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiCallError<String>> handleSqlException(SQLException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiCallError<>(exception.getMessage(), Collections.singletonList(exception.getMessage())));
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiCallError<T> {
        private String message;
        private List<T> details;

    }
}
