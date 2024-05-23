package com.omarabdelrehim8.accounts.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.omarabdelrehim8.accounts.dto.constraints.CustomerValidation;
import com.omarabdelrehim8.accounts.dto.constraints.CustomerView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(
        name = "Customer",
        description = "Schema to hold Customer Information"
)
public class CustomerDto {

    @Schema(
            description = "Customer's Id"
    )
    @JsonView(CustomerView.OnUpdate.class)
    @NotNull(message = "Customer ID is required", groups = CustomerValidation.OnUpdate.class)
    @Min(value = 1, message = "Customer ID must be bigger than 0", groups = CustomerValidation.OnUpdate.class)
    private Long customerId;

    @Schema(
            description = "Name of the customer", example = "John Doe"
    )
    @JsonView(CustomerView.OnCreate.class)
    @NotNull(message = "Name is required", groups = CustomerValidation.OnCreate.class)
    @Size(min = 5, max = 40, message = "The length of the customer name should be between 5 and 40", groups = CustomerValidation.OnCreate.class)
    private String name;

    @Schema(
            description = "Email of the customer", example = "johndoe@gmail.com"
    )
    @JsonView({CustomerView.OnUpdate.class, CustomerView.OnCreate.class})
    @NotBlank(message = "Email is required", groups = {CustomerValidation.OnCreate.class, CustomerValidation.OnUpdate.class})
    @Email(regexp = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$", message = "Email address should be a valid value", groups = {CustomerValidation.OnCreate.class, CustomerValidation.OnUpdate.class})
    private String email;

    @Schema(
            description = "Mobile Number of the customer", example = "3697746682"
    )
    @JsonView({CustomerView.OnUpdate.class, CustomerView.OnCreate.class})
    @NotBlank(message = "Mobile number is required", groups = {CustomerValidation.OnCreate.class, CustomerValidation.OnUpdate.class})
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits", groups = {CustomerValidation.OnCreate.class, CustomerValidation.OnUpdate.class})
    private String mobileNumber;
}
