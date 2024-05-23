package com.omarabdelrehim8.accounts;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Accounts Microservice REST API documentation",
                description = "Microservice demo built to manage a bank's account life cycle",
                version = "v1",
                contact = @Contact(
                        name = "Omar Abdel Rehim",
                        email = "omarabdelrehim8@gmail.com"
                )
        )
)
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }
}
