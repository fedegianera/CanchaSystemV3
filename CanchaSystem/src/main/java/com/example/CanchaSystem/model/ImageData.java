package com.example.CanchaSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ImageProviderType imageProviderType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String storedPath;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long size;

    // uploadData is the user's username if ImageType is PROFILE_PICTURE, and a stringified CanchaId if it's CANCHA
    @Column(nullable = false)
    private String uploadData;

    @Column(nullable = false)
    private boolean active = true;
}
