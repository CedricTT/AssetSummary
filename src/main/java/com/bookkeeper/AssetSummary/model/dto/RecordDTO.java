package com.bookkeeper.AssetSummary.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDTO {

    private String description;

    private String category;

    private String paymentMethod;

    private LocalDate date;

    private double amount;
}
