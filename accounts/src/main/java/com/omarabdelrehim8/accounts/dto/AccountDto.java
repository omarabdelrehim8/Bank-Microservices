package com.omarabdelrehim8.accounts.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(
        name = "Account",
        description = "Schema to hold Account Information"
)
public class AccountDto {

    @Schema(
            description = "Account Number of an Account", example = "1034562573"
    )
    private Long accountNumber;

    @Schema(
            description = "Account Type of an Account", example = "Savings"
    )
    private String accountType;

    @Schema(
            description = "Bank's Branch Address", example = "123 Main Street, New York"
    )
    private String branchAddress;
}
