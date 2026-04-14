package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.exception.image.ImageNotFoundException;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageService service;

    @PostMapping("/insert/{uploadData}")
    public ResponseEntity<?> insertImages(@PathVariable String uploadData, @RequestParam("type") ImageProviderType type, @RequestParam("files") List<MultipartFile> files) {
        files.forEach((file) -> service.uploadImage(file, type, uploadData));
        return ResponseEntity.ok(Map.of("message", "Imágenes insertadas con éxito"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable long id) {
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
    public ResponseEntity<?> updateImage(@PathVariable long id, @RequestParam("file") MultipartFile file) {
        service.updateImage(id, file);

        return ResponseEntity.ok(Map.of("message", "Imagen actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable long id) {
        service.deleteImage(id);
        return ResponseEntity.ok(Map.of("message","Imagen eliminada"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteImages(@RequestBody long[] ids) {
        for (long id : ids) {
            service.deleteImage(id);
        }
        return ResponseEntity.ok(Map.of("message","Imágenes eliminadas"));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserProfilePicture(@PathVariable String username) {
        return ResponseEntity.ok(service.getProfilePictureByUsername(username));
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<?> updateUserProfilePicture(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(service.updateProfilePictureByUsername(username, file));
        } catch (ImageNotFoundException e) {
            return ResponseEntity.ok(service.uploadImage(file, ImageProviderType.PROFILE_PICTURE, username));
        }
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUserProfilePicture(@PathVariable String username) {
        return ResponseEntity.ok(service.deleteProfilePictureByUsername(username));
    }

    @GetMapping("/establishment/{establishmentId}")
    public ResponseEntity<?> getEstablishmentImages(@PathVariable Long establishmentId) {
        return ResponseEntity.ok(service.getEstablishmentImagesByEstablishmentId(establishmentId));
    }
}
