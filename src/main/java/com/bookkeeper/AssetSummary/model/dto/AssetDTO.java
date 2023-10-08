package com.bookkeeper.AssetSummary.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    private Double balance;

}
