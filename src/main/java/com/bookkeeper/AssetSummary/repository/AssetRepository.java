package com.bookkeeper.AssetSummary.repository;

import com.bookkeeper.AssetSummary.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByNameAndDate(String name, LocalDate date);

    Optional<Asset> findTopByNameOrderByDate(String assetName);

    Optional<List<Asset>> findByName(String name);
}
