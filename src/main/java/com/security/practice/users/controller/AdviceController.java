package com.security.practice.users.controller;

import com.security.practice.users.exception.ErrorResponse;
import com.security.practice.users.exception.RolNotFoundException;
import com.security.practice.users.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class AdviceController {

    private ErrorResponse buildError(HttpStatus httpStatus, String message){
        return new ErrorResponse(
                httpStatus.value(),
                message,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException userNotFoundException){
        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND,userNotFoundException.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RolNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRolNotFound(RolNotFoundException rolNotFoundException){
        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND,rolNotFoundException.getMessage()),
                HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex){
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado: " + ex.getMessage()));
    }



}
