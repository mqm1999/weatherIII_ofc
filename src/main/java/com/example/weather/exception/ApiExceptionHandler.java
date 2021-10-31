package com.example.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String invalidURLException(Exception e, WebRequest request) {
        return "Invalid URL";
    }
}
