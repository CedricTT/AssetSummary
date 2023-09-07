package com.bookkeeper.AssetSummary.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asset_generator")
    @SequenceGenerator(name = "asset_generator", sequenceName = "asset_seq", allocationSize = 1)
    private Long id;

    private String name;

    private LocalDate date;

    private Double credit;

    private Double debit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Asset asset = (Asset) o;
        return getId() != null && Objects.equals(getId(), asset.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
