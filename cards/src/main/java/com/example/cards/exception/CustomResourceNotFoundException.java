package com.example.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomResourceNotFoundException extends RuntimeException {

    public CustomResourceNotFoundException(String resourceName, String fieldName) {

        // this super() only accepts one string as parameter, so we use String.format
        // parameters will be replaced wherever %s is mentioned
        super(String.format("%s not found with the given %s", resourceName, fieldName));

    }
}
