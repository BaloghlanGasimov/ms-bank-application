package com.example.msbankapplication.controller;

import com.example.msbankapplication.exceptions.NotFound;
import com.example.msbankapplication.exceptions.SameCurrencyException;
import com.example.msbankapplication.model.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handler(NotFound e){
        log.error(e.getLogMessage());
        return new ExceptionDto(e.getErrorMessage());
    }

    @ExceptionHandler(SameCurrencyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handler(SameCurrencyException e){
        log.error(e.getLogMessage());
        return new ExceptionDto(e.getErrorMessage());
    }
}
