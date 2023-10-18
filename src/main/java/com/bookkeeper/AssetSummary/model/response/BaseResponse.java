package com.bookkeeper.AssetSummary.model.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Data
@SuperBuilder
@NoArgsConstructor
public class BaseResponse {

    String status;

    LocalDateTime requestTime;
}
