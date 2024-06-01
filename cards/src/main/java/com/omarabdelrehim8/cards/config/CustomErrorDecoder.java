package com.omarabdelrehim8.cards.config;

import com.omarabdelrehim8.cards.exception.CustomResourceNotFoundException;
import com.omarabdelrehim8.cards.exception.ServerErrorException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodkey, Response response) {

        return switch (response.status()) {
            case 404 -> new CustomResourceNotFoundException("Customer", "customer id");
            case 500 -> new ServerErrorException(String.format("Something went wrong. Please try again later."));
            default -> defaultErrorDecoder.decode(methodkey, response);
        };
    }
}