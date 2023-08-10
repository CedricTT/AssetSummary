package com.bookkeeper.AssetSummary.repository;

import com.bookkeeper.AssetSummary.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
}
