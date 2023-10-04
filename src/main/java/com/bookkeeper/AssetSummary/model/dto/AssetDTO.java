package com.bookkeeper.AssetSummary.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDTO {

    @NotBlank
    private String name;

    private LocalDate date;

    @NotBlank
    private String type;

    @NotNull
    private Double credit;

    @NotNull
    private Double debit;

    @NotNull
    private Double balance;

    private LocalDateTime created_date;

    private LocalDateTime updated_date;
}
