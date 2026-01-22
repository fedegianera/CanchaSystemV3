package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.ImageData;
import com.example.CanchaSystem.model.ImageProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageDataRepository extends JpaRepository<ImageData, UUID> {
    List<ImageData> findAllByActive(boolean active);
    boolean existsByIdAndActive(UUID id, boolean active);
    Optional<ImageData> findByIdAndActive(UUID id, boolean active);
    List<ImageData> findByUsernameAndActive(String username, boolean active);
    List<ImageData> findByUsernameAndTypeAndActive(String username, ImageProviderType type, boolean active);
}
