package com.bookkeeper.AssetSummary.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDTO {

    private String name;

    private LocalDate date;

    private String type;

    private Double credit;

    private Double debit;

    private Double balance;

    private LocalDateTime created_date;

    private LocalDateTime updated_date;
}
