package com.github.melnikanna6766a11y.errorfreetext.exceptions;

import com.github.melnikanna6766a11y.errorfreetext.dto.CorrectedTextResponse;
import com.github.melnikanna6766a11y.errorfreetext.dto.ErrorResponse;
import com.github.melnikanna6766a11y.errorfreetext.services.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@ControllerAdvice
@Log4j2
@AllArgsConstructor
public class ExceptionAdvice {
    private TaskService taskService;

    @ExceptionHandler({NoSuchIdException.class})
    public ResponseEntity<ErrorResponse> handleException(NoSuchIdException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        LocalDateTime.now(),
                        ServletUriComponentsBuilder.fromCurrentRequest().build().getPath()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse>  handleException(BindException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
                        HttpStatus.BAD_REQUEST,
                        LocalDateTime.now(),
                        ServletUriComponentsBuilder.fromCurrentRequest().build().getPath()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleException(RestClientResponseException exception) {
        log.error(exception.getMessage(), exception.getResponseBodyAsString());
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        LocalDateTime.now(),
                        ServletUriComponentsBuilder.fromCurrentRequest().build().getPath()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CounterOverflowException.class)
    public ResponseEntity<ErrorResponse> handleException(CounterOverflowException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        LocalDateTime.now(),
                        ServletUriComponentsBuilder.fromCurrentRequest().build().getPath()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleException(ClientAbortException exception) {
        log.error(exception.getMessage());
    }
}
