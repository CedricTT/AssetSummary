package com.bookkeeper.AssetSummary.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionRecord {

    @NotBlank
    String assetName;

    @NotNull
    Double amount;

    @NotNull
    LocalDateTime requestTime;
}
