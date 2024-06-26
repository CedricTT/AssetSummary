package com.bookkeeper.AssetSummary.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private UUID uuid;

    @NotBlank
    private String description;

    @NotBlank
    private String category;

    @NotBlank
    private String paymentMethod;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull
    private double amount;

    private Double estimateValue;

    @NotBlank
    private String currency;

    @NotBlank
    private String paymentFrom;

    @NotBlank
    private String paymentTo;
}
