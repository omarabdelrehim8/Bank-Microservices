package com.omarabdelrehim8.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(
        name = "Successful_Response",
        description = "Schema to hold successful Response Information"
)
public class ResponseDto {

    @Schema(
            description = "Status Code in the response"
    )
    private int statusCode;

    @Schema(
            description = "Status Message in the response"
    )
    private String statusMessage;
}
