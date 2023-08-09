package com.bookkeeper.AssetSummary.model.dto;

import lombok.Data;

@Data
public class AssetDTO {

    private String name;

    private Double credit;

    private Double debit;
}
