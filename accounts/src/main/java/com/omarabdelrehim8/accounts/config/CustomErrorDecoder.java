package com.omarabdelrehim8.accounts.config;

import com.omarabdelrehim8.accounts.exception.ResourceNotFoundException;
import com.omarabdelrehim8.accounts.exception.ServerErrorException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodkey, Response response) {

        return switch (response.status()) {
            case 404 -> new ResourceNotFoundException("Card", "customer's mobile number. Please create a card first.");
            case 500 -> new ServerErrorException("Something went wrong. Please try again later.");
            default -> defaultErrorDecoder.decode(methodkey, response);
        };
    }
}