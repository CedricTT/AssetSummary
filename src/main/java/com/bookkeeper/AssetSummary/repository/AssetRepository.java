package com.bookkeeper.AssetSummary.repository;

import com.bookkeeper.AssetSummary.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

//    Optional<Asset> findTopByNameOrderByDate(String assetName);

    Optional<Asset> findByName(String name);
}
