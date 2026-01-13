package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads", e);
        }
    }

    public String saveFile(MultipartFile file, Long solicitudId) throws IOException {
        // Validar tipo de archivo
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + file.getContentType());
        }

        // Validar tamaño
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (10MB)");
        }

        // Generar nombre único
        String extension = getFileExtension(file.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID().toString() + extension;
        
        // Crear directorio para la solicitud
        Path solicitudDir = this.uploadDir.resolve(solicitudId.toString());
        Files.createDirectories(solicitudDir);
        
        // Guardar archivo
        Path targetLocation = solicitudDir.resolve(nombreArchivo);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return nombreArchivo;
    }

    public Path getFile(Long solicitudId, String nombreArchivo) {
        Path filePath = this.uploadDir.resolve(solicitudId.toString()).resolve(nombreArchivo).normalize();
        
        // Validar que el archivo esté dentro del directorio de uploads
        if (!filePath.startsWith(this.uploadDir)) {
            throw new SecurityException("Acceso no permitido al archivo");
        }
        
        if (!Files.exists(filePath)) {
            throw new RuntimeException("Archivo no encontrado: " + nombreArchivo);
        }
        
        return filePath;
    }

    public void deleteFile(Long solicitudId, String nombreArchivo) throws IOException {
        Path filePath = this.uploadDir.resolve(solicitudId.toString()).resolve(nombreArchivo).normalize();
        
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    public String sanitizeFilename(String filename) {
        if (filename == null) {
            return "";
        }
        // Remover caracteres peligrosos
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
