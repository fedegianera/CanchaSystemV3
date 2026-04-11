package com.example.CanchaSystem.service;

import com.example.CanchaSystem.exception.image.ImageNotFoundException;
import com.example.CanchaSystem.model.ImageData;
import com.example.CanchaSystem.model.ImageProviderType;
import com.example.CanchaSystem.repository.ImageDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


@Service
public class ImageService {
    @Autowired
    private ImageDataRepository repository;

    private static final Path imagePath = Paths.get("./images");
    private static final Set<String> validMimeTypes = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp"
    );

    private static final Random random = new Random();
    public ImageData uploadImage(MultipartFile file, ImageProviderType type, String uploadData) {
        validate(file);

        String storagePath = "";
        try(InputStream in = file.getInputStream()) {
            storagePath = storeFile(in, file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageData data = new ImageData(
                random.nextLong(),
                type,
                file.getOriginalFilename(),
                storagePath,
                file.getContentType(),
                file.getSize(),
                uploadData,
                true
        );

        return repository.save(data);
    }

    // La imagen vieja (Resource) sigue en el sistema: se añade una nueva y se relocaliza su ImageData correspondiente
    public ImageData updateImage(long imageDataId, MultipartFile file) {
        validate(file);

        String storagePath = "";
        try(InputStream in = file.getInputStream()) {
            storagePath = storeFile(in, file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageData data = getImageData(imageDataId);
        data.setFileName(file.getOriginalFilename());
        data.setStoredPath(storagePath);
        data.setMimeType(file.getContentType());
        data.setSize(file.getSize());

        return repository.save(data);
    }

    public ImageData deleteImage(long imageDataId) {
        ImageData data = getImageData(imageDataId);

        data.setActive(false);

        return repository.save(data);
    }

    public ImageData getProfilePictureByUsername(String username) {
        return repository.findByUploadDataAndImageProviderTypeAndActive(username, ImageProviderType.PROFILE_PICTURE, true)
                .stream().findFirst()
                .orElseThrow(() -> new ImageNotFoundException("Imagen no encontrada"));
    }

    public ImageData updateProfilePictureByUsername(String username, MultipartFile file) {
        return updateImage(getProfilePictureByUsername(username).getId(), file);
    }

    public ImageData deleteProfilePictureByUsername(String username) {
        return deleteImage(getProfilePictureByUsername(username).getId());
    }

    public List<ImageData> getEstablishmentImagesByEstablishmentId(Long establishmentId) {
        return repository.findByUploadDataAndImageProviderTypeAndActive(establishmentId.toString(), ImageProviderType.CANCHA, true);
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalArgumentException("La imagen no debe estar vacía");

        String mimeType = file.getContentType();
        if (mimeType == null || !validMimeTypes.contains(mimeType))
            throw new IllegalArgumentException("La imagen tiene un tipo de contenido inválido");
    }

    private String storeFile(InputStream in, String fileName) {
        LocalDate today = LocalDate.now();
        Path dir = imagePath.resolve(
                today.getYear() + File.separator +
                        String.format("%02d", today.getMonthValue()) + File.separator +
                        String.format("%02d", today.getDayOfMonth())
        );

        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String extension = getExtension(fileName);
        String storedName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
        Path filePath = dir.resolve(storedName);

        try (OutputStream out = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
            StreamUtils.copy(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imagePath.relativize(filePath).toString();
    }

    public ImageData getImageData(long imageDataId) {
        return repository.findByIdAndActive(imageDataId, true).orElseThrow(
                () -> new ImageNotFoundException("Imagen no encontrada")
        );
    }

    public Resource getImageResource(long imageDataId) {
        return getResource(getImageData(imageDataId).getStoredPath());
    }

    private Resource getResource(String storedPath) {
        Path filePath = imagePath.resolve(storedPath).normalize();
        Path normalizedRoot = imagePath.normalize().toAbsolutePath();
        filePath = normalizedRoot.getParent().resolve(filePath);

        if (!filePath.startsWith(normalizedRoot) || !Files.exists(filePath)) {
            throw new ImageNotFoundException("Imagen no encontrada");
        }

        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new ImageNotFoundException("URL malformada");
        }
    }

    private String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot == -1 ? "" : fileName.substring(lastDot + 1);
    }
}
