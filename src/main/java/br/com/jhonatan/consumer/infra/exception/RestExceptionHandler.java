package br.com.jhonatan.consumer.infra.exception;

import br.com.jhonatan.consumer.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;


@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> userNotFoundHandler(UserNotFoundException e) {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> subscriptionNotFoundException(SubscriptionNotFoundException e) {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> userAlreadyHasSubscription(UserAlreadyHasSubscription e) {
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        HttpStatus.CONFLICT
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> subscriptionAlreadyCanceled(SubscriptionAlreadyCanceled e) {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> subscriptionAlreadyBlocked(SubscriptionAlreadyBlocked e) {
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        HttpStatus.CONFLICT
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> subscriptionAlreadyUnblocked(SubscriptionAlreadyUnblocked e) {
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        HttpStatus.CONFLICT
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> genericExceptionHandler(Exception e) {
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new RestExceptionResponse(
                        "error",
                        message,
                        HttpStatus.BAD_REQUEST
                )
        );
    }
}
