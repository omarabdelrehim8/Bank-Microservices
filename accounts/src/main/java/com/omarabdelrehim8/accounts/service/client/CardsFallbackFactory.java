package com.omarabdelrehim8.accounts.service.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class CardsFallbackFactory implements FallbackFactory<CardsFeignClient> {

    @Override
    public CardsFeignClient create(Throwable cause) {
        return customerId -> null;
    }
}

