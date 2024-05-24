package com.example.cards.dto;

import com.example.cards.dto.constraints.CardView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor @RequiredArgsConstructor
public class ResponseDto {

    @JsonView({CardView.OnUpdate.class, CardView.OnDelete.class})
    @NonNull
    private int statusCode;

    @JsonView({CardView.OnUpdate.class, CardView.OnDelete.class})
    @NonNull
    private String statusMessage;

    private CardDto card;
}
