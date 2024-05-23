package com.example.cards.dto;

import com.example.cards.dto.constraints.CardView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {

    @JsonView({CardView.OnUpdate.class, CardView.OnDelete.class})
    private int statusCode;

    @JsonView({CardView.OnUpdate.class, CardView.OnDelete.class})
    private String statusMessage;

    private CardDto card;

    public ResponseDto(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
