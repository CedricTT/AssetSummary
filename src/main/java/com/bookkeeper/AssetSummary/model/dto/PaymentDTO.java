package com.bookkeeper.AssetSummary.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private String description;

    private String category;

    private String paymentMethod;

    private LocalDate date;

    private double amount;

    private String paymentFrom;

    private String paymentTo;
}
