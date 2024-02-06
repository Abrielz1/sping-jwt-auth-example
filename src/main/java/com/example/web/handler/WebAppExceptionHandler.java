package com.example.web.handler;

import com.example.spingjwtauthexample.exception.AlreadyExistsException;
import com.example.spingjwtauthexample.exception.EntityNotFoundException;
import com.example.spingjwtauthexample.exception.RefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class WebAppExceptionHandler {

    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResponseBody> refreshTokenExceptionHandler(RefreshTokenException e,
                                                                          WebRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, e, request);
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<ErrorResponseBody> alreadyExistsExceptionHandler(AlreadyExistsException e,
                                                                    WebRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY, e, request);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> entityNotFoundExceptionHandler(EntityNotFoundException e,
                                                                    WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, e, request);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(HttpStatus status,
                                                            Exception e,
                                                            WebRequest request) {
        return ResponseEntity.status(status)
                .body(ErrorResponseBody
                        .builder()
                        .message(e.getMessage())
                        .description(request.getDescription(false))
                        .build());
    }
}
