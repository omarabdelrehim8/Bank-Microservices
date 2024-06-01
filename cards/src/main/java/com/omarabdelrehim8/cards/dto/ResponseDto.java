package com.omarabdelrehim8.cards.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.omarabdelrehim8.cards.dto.constraints.CardView;
import lombok.*;

@Data
@AllArgsConstructor @RequiredArgsConstructor @NoArgsConstructor
public class ResponseDto {

    @JsonView({CardView.OnUpdate.class, CardView.OnDelete.class})
    @NonNull
    private int statusCode;

    @JsonView({CardView.OnUpdate.class, CardView.OnDelete.class})
    @NonNull
    private String statusMessage;

    private CardDto card;
}
