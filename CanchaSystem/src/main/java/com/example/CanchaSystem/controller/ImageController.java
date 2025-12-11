package com.example.CanchaSystem.controller;

import com.example.CanchaSystem.dto.request.OwnerRequestDTO;
import com.example.CanchaSystem.model.ImageData;
import com.example.CanchaSystem.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {
    @Autowired
    private ImageService service;

    @PostMapping("insert/{username}")
    public ResponseEntity<?> insertImage(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        ImageData data = service.uploadImage(file, username);
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
    public ResponseEntity<?> updateOwner(@PathVariable("id") UUID id, @RequestParam("file") MultipartFile file) {
        service.updateImage(id, file);

        return ResponseEntity.ok(Map.of("message", "Imagen actualizada"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOwner(@PathVariable UUID id) {
        service.deleteImage(id);
        return ResponseEntity.ok(Map.of("message","Imagen eliminada"));
    }
}
