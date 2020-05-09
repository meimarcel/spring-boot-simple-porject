package com.example.marcel.DevDojo.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        ResourceNotFoundDetails resourceNotFoundDetails = ResourceNotFoundDetails.Builder
                .newBuilder()
                .title("Resource Not Found")
                .timestamp(LocalDateTime.now())
                .detail(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(resourceNotFoundDetails);
    }

    @ResponseBody
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> fieldsValidations = ex.getBindingResult().getFieldErrors()
                .stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (oldValue, newValue) -> oldValue + ", " + newValue));
        ValidationErrorDetails validationErrorDetails = ValidationErrorDetails.Builder
                .newBuilder()
                .title("Fiels Validation Error")
                .timestamp(LocalDateTime.now())
                .detail(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .fields(fieldsValidations)
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(validationErrorDetails);
    }

    @ResponseBody
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        MasterErrorDetails masterErrorDetails = MasterErrorDetails.Builder
                .newBuilder()
                .title("Internal Error")
                .timestamp(LocalDateTime.now())
                .detail(ex.getMessage())
                .developerMessage("Houve um erro inesperado, tente novamente mais tarde.")
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(masterErrorDetails);
    }
}
