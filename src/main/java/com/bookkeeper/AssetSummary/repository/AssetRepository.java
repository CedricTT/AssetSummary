package com.bookkeeper.AssetSummary.repository;

import com.bookkeeper.AssetSummary.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

//    Optional<Asset> findTopByNameOrderByDate(String assetName);

    Optional<Asset> findByName(String name);

    Optional<Asset> findByNameAndUID(String name, String uid);

    Optional<List<Asset>> findByEmailAndUID(String email, String uid);
}
