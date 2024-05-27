package com.omarabdelrehim8.accounts.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "accounts")
@Getter @Setter
public class AccountsContactInfoDto {

    private String message;
    private Map<String, String> contactDetails;
    private String onCallSupport;
}
