package com.example.demo.controller;

import com.example.demo.dto.APIError;
import com.example.demo.exceptions.TooHeavyLoadException;
import com.example.demo.exceptions.UnknownDroneException;
import com.example.demo.exceptions.UnknownMedicationException;
import com.example.demo.exceptions.UnloadableDroneException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UnknownDroneException.class)
    protected ResponseEntity<APIError> onUnknownProductException(UnknownDroneException ex, WebRequest request) {
        final APIError apiError = new APIError(LocalDateTime.now(), 404, "Drone is unknown",
                null, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }
    @ExceptionHandler(UnknownMedicationException.class)
    protected ResponseEntity<APIError> onUnknownMedicationException(UnknownMedicationException ex, WebRequest request) {
        final APIError apiError = new APIError(LocalDateTime.now(), 404, "Medication is unknown",
                null, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(UnloadableDroneException.class)
    protected ResponseEntity<APIError> onUnloadableDroneException(UnloadableDroneException ex, WebRequest request) {
        final APIError apiError = new APIError(LocalDateTime.now(), 409, "Drone is not loadable right now",
                null, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(TooHeavyLoadException.class)
    protected ResponseEntity<APIError> onTooHeavyLoadException(TooHeavyLoadException ex, WebRequest request) {
        final APIError apiError = new APIError(LocalDateTime.now(), 409, "The requested load is too heavy for this drone",
                null, request.getDescription(false));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<APIError> onMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        final Map<String, List<String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField, Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
        final APIError apiError = new APIError(LocalDateTime.now(), 400, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors, request.getDescription(false));
        return ResponseEntity.badRequest().body(apiError);
    }
}
