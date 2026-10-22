package com.ecoalerta.backend.services;

import com.ecoalerta.backend.exceptions.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

@Service
public class StorageService {

    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path raiz = Paths.get("subidas").toAbsolutePath().normalize();

    public StorageService() {
        try {
            Files.createDirectories(raiz);
        } catch (IOException e) {
            throw new RuntimeException("Error al crear carpeta de subidas", e);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        // El nombre que manda el navegador no es de fiar: nos quedamos solo con el nombre, sin carpetas
        String soloNombre = original.substring(Math.max(original.lastIndexOf('/'), original.lastIndexOf('\\')) + 1);
        String limpio = soloNombre.replaceAll("[^A-Za-z0-9._-]", "_");

        int punto = limpio.lastIndexOf('.');
        String extension = punto < 0 ? "" : limpio.substring(punto + 1).toLowerCase(Locale.ROOT);
        String tipo = file.getContentType() == null ? "" : file.getContentType();
        if (!EXTENSIONES_PERMITIDAS.contains(extension) || !tipo.startsWith("image/")) {
            throw new ReglaNegocioException("Solo se permiten imágenes JPG, PNG, GIF o WEBP");
        }

        String nombreFinal = System.currentTimeMillis() + "_" + limpio;
        Path destino = raiz.resolve(nombreFinal).normalize();
        if (!destino.startsWith(raiz)) {
            throw new ReglaNegocioException("Ruta de archivo no permitida");
        }
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo", e);
        }
        return nombreFinal;
    }

    public void eliminar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        Path archivo = raiz.resolve(nombre).normalize();
        if (!archivo.startsWith(raiz)) {
            return;
        }
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            // No es grave: el registro ya se borró, solo queda un archivo suelto
        }
    }
}
