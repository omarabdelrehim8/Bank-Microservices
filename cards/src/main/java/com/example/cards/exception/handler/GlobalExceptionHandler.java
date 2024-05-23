package com.example.cards.exception.handler;

import com.example.cards.dto.ErrorResponseDto;
import com.example.cards.exception.CardCreationLimitException;
import com.example.cards.exception.CustomResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // for @RequestBody input
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String endpoint = ((ServletWebRequest)request).getRequest().getRequestURI();
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldNameUnfiltered = ((FieldError)error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldNameUnfiltered, validationMsg);
        });

        String errorString = validationErrors.entrySet().stream()
                .map(error -> String.format("[%s: %s]", error.getKey(), error.getValue()))
                .collect(Collectors.joining(" "));

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                endpoint,
                HttpStatus.BAD_REQUEST,
                errorString,
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    // for @RequestParam input
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {

        String endpoint = request.getRequestURI();
        Map<String, String> validationErrors = new HashMap<>();

        ex.getConstraintViolations().forEach(error -> {
            String propertyPath = error.getPropertyPath().toString();
            System.out.println(propertyPath);
            String validationMsg = error.getMessage();
            validationErrors.put(propertyPath.substring(propertyPath.indexOf(".") + 1), validationMsg);
        });

        String errorString = validationErrors.entrySet().stream()
                .map(error -> String.format("[%s: %s]", error.getKey(), error.getValue()))
                .collect(Collectors.joining(" "));

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                endpoint,
                HttpStatus.BAD_REQUEST,
                errorString,
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CardCreationLimitException.class)
    public ResponseEntity<ErrorResponseDto> handleCardCreationLimitException(CardCreationLimitException exception, HttpServletRequest request) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomResourceNotFoundException(CustomResourceNotFoundException exception, HttpServletRequest request) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                request.getRequestURI(),
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String endpoint = ((ServletWebRequest)request).getRequest().getRequestURI();

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                endpoint,
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception, HttpServletRequest request) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}