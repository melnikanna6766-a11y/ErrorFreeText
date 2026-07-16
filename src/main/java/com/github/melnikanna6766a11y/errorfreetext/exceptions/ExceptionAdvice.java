package com.github.melnikanna6766a11y.errorfreetext.exceptions;

import com.github.melnikanna6766a11y.errorfreetext.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(NoSuchIdException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchIdException exception) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        LocalDateTime.now(),
                        ServletUriComponentsBuilder.fromCurrentRequest().build().getPath()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse>  handleException(BindException exception) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
                        HttpStatus.BAD_REQUEST,
                        LocalDateTime.now(),
                        ServletUriComponentsBuilder.fromCurrentRequest().build().getPath()),
                HttpStatus.BAD_REQUEST);
    }
}
