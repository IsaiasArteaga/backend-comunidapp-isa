package com.pa.comunidapp_backend.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

/**
 * Servicio para gestionar imágenes usando Cloudinary.
 * Todas las imágenes se suben a Cloudinary y se devuelven URLs públicas.
 */
@Service
public class FileStorageService {

    @Autowired
    private Cloudinary cloudinary;

    @Value("${cloudinary.folder:comunidapp}")
    private String cloudinaryFolder;

    /**
     * Sube múltiples imágenes a Cloudinary y devuelve sus URLs.
     *
     * @param files Array de archivos a subir
     * @return Lista de URLs de las imágenes subidas
     */
    public List<String> guardarImagenes(MultipartFile[] files) {
        List<String> urlsImagenes = new ArrayList<>();

        if (files == null || files.length == 0) {
            return urlsImagenes;
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String urlImagen = guardarArchivo(file);
            if (urlImagen != null) {
                urlsImagenes.add(urlImagen);
            }
        }

        return urlsImagenes;
    }

    /**
     * Sube una imagen a Cloudinary y devuelve su URL.
     *
     * @param file Archivo a subir
     * @return URL de la imagen subida
     */
    public String guardarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo no puede estar vacío");
        }

        String nombreOriginal = file.getOriginalFilename();
        if (nombreOriginal == null || nombreOriginal.isEmpty()) {
            throw new RuntimeException("El archivo no tiene un nombre válido");
        }

        try {
            // Generar un nombre único para el archivo
            String publicId = UUID.randomUUID().toString();

            // Configuración para subir a Cloudinary usando ObjectUtils.asMap() (recomendado
            // por la documentación)
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", cloudinaryFolder,
                    "public_id", publicId,
                    "resource_type", "image",
                    "overwrite", false // No sobrescribir por defecto
            );

            // Subir el archivo a Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            // Obtener la URL segura de la imagen
            String secureUrl = (String) uploadResult.get("secure_url");

            if (secureUrl == null) {
                throw new RuntimeException("No se pudo obtener la URL de la imagen subida");
            }

            return secureUrl;
        } catch (IOException ex) {
            throw new RuntimeException("Error al leer el archivo: " + nombreOriginal, ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error al subir la imagen a Cloudinary: " + ex.getMessage(), ex);
        }
    }

    /**
     * Elimina una imagen de Cloudinary usando su URL.
     * Extrae el public_id de la URL automáticamente.
     *
     * @param urlImagen URL completa de la imagen en Cloudinary
     */
    public void eliminarArchivo(String urlImagen) {
        if (urlImagen == null || urlImagen.isEmpty()) {
            return;
        }

        try {
            // Si no es una URL de Cloudinary, no hacer nada
            if (!urlImagen.contains("cloudinary.com")) {
                return;
            }

            // Extraer el public_id de la URL
            // Formato:
            // https://res.cloudinary.com/{cloud_name}/image/upload/{folder}/{public_id}.{extension}
            String publicId = extraerPublicIdDeUrl(urlImagen);

            if (publicId != null && !publicId.isEmpty()) {
                // Eliminar de Cloudinary
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception ex) {
            // Log el error pero no lanzar excepción para no interrumpir el flujo
            System.err.println("Error al eliminar imagen de Cloudinary: " + ex.getMessage());
        }
    }

    /**
     * Elimina múltiples imágenes de Cloudinary.
     *
     * @param imagenesString String con URLs separadas por comas
     */
    public void eliminarImagenes(String imagenesString) {
        if (imagenesString == null || imagenesString.isEmpty()) {
            return;
        }

        String[] imagenes = imagenesString.split(",");
        for (String imagen : imagenes) {
            if (!imagen.trim().isEmpty()) {
                eliminarArchivo(imagen.trim());
            }
        }
    }

    /**
     * Extrae el public_id de una URL de Cloudinary.
     * Maneja URLs con o sin transformaciones.
     *
     * @param url URL de Cloudinary
     * @return public_id extraído o null si no se puede extraer
     */
    private String extraerPublicIdDeUrl(String url) {
        try {
            // Buscar el patrón /image/upload/ o /video/upload/
            int uploadIndex = url.indexOf("/image/upload/");
            if (uploadIndex == -1) {
                uploadIndex = url.indexOf("/video/upload/");
                if (uploadIndex == -1) {
                    return null;
                }
            }

            // Extraer todo después de /upload/
            String afterUpload = url.substring(uploadIndex + "/image/upload/".length());

            // Remover parámetros de query si existen
            int queryIndex = afterUpload.indexOf('?');
            if (queryIndex > 0) {
                afterUpload = afterUpload.substring(0, queryIndex);
            }

            // Dividir por '/' para obtener las partes
            String[] parts = afterUpload.split("/");

            if (parts.length == 0) {
                return null;
            }

            // El último elemento es el archivo con extensión
            String fileName = parts[parts.length - 1];

            // Remover la extensión
            int puntoIndex = fileName.lastIndexOf('.');
            if (puntoIndex > 0) {
                fileName = fileName.substring(0, puntoIndex);
            }

            // Si hay más de una parte, reconstruir el public_id con el folder
            if (parts.length > 1) {
                StringBuilder publicId = new StringBuilder();
                for (int i = 0; i < parts.length - 1; i++) {
                    if (publicId.length() > 0) {
                        publicId.append("/");
                    }
                    publicId.append(parts[i]);
                }
                publicId.append("/").append(fileName);
                return publicId.toString();
            } else {
                // Si solo hay el nombre del archivo, usar el folder configurado
                return cloudinaryFolder + "/" + fileName;
            }
        } catch (Exception ex) {
            System.err.println("Error al extraer public_id de URL: " + ex.getMessage());
            return null;
        }
    }
}
