package com.bookkeeper.AssetSummary.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetDTO {

    private String name;

    private LocalDate date;

    private Double credit;

    private Double debit;
}
