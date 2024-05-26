package com.omarabdelrehim8.cards.dto;

import com.omarabdelrehim8.cards.dto.constraints.CardView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonView({CardView.OnUpdate.class, CardView.OnDelete.class})
public class ErrorResponseDto {

    private String apiPath;

    private HttpStatus errorCode;

    private String errorMessage;

    private LocalDateTime errorTime;
}
