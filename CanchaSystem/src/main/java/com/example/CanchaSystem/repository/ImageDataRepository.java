package com.example.CanchaSystem.repository;

import com.example.CanchaSystem.model.ImageData;
import com.example.CanchaSystem.model.ImageProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImageDataRepository extends JpaRepository<ImageData, UUID> {
    List<ImageData> findAllByActive(boolean active);
    boolean existsByIdAndActive(long id, boolean active);
    Optional<ImageData> findByIdAndActive(long id, boolean active);
    List<ImageData> findByUploadDataAndActive(String uploadData, boolean active);
    List<ImageData> findByUploadDataAndImageProviderTypeAndActive(String uploadData, ImageProviderType imageProviderType, boolean active);
}
