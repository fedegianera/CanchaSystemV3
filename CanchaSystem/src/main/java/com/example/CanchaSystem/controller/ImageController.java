package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.model.ImageData;
import com.example.CanchaSystem.model.ImageProviderType;
import com.example.CanchaSystem.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageService service;

    @PostMapping("/insert/{username}")
    public ResponseEntity<?> insertImage(@PathVariable String uploadData, @RequestParam("type") ImageProviderType type, @RequestParam("file") MultipartFile file) {
        ImageData data = service.uploadImage(file, type, uploadData);
        return ResponseEntity.ok(Map.of("message", "Imagen insertada con éxito: " + data.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable UUID id) {
        ImageData data = service.getImageData(id);
        Resource resource = service.getImageResource(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + data.getFileName() + '"'
                )
                .contentType(MediaType.parseMediaType(data.getMimeType()))
                .contentLength(data.getSize())
                .body(resource);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateImage(@PathVariable("id") UUID id, @RequestParam("file") MultipartFile file) {
        service.updateImage(id, file);

        return ResponseEntity.ok(Map.of("message", "Imagen actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID id) {
        service.deleteImage(id);
        return ResponseEntity.ok(Map.of("message","Imagen eliminada"));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserProfilePicture(@PathVariable("username") String username) {
        ImageData data = service.getProfilePictureByUsername(username);
        Resource resource = service.getImageResource(data.getId());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + data.getFileName() + '"'
                )
                .contentType(MediaType.parseMediaType(data.getMimeType()))
                .contentLength(data.getSize())
                .body(resource);
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<?> updateUserProfilePicture(@PathVariable("username") String username, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.updateProfilePictureByUsername(username, file));
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUserProfilePicture(@PathVariable("username") String username) {
        return ResponseEntity.ok(service.deleteProfilePictureByUsername(username));
    }

    @GetMapping("/cancha/{id}")
    public ResponseEntity<?> getCanchaImages(@PathVariable Long canchaId) {
        List<ImageData> images = service.getCanchaImagesByCanchaId(canchaId);
        List<Resource> resources = new ArrayList<>();
        for (ImageData data : images) {
            resources.add(service.getImageResource(data.getId()));
        }

        return ResponseEntity.ok(resources);

        /*return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + images.getFileName() + '"'
                )
                .contentType(MediaType.parseMediaType(images.getMimeType()))
                .contentLength(images.getSize())
                .body(resource);*/
    }
}
