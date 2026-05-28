package com.pa.comunidapp_backend.config.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ImagenService {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Convierte una lista de URLs de imágenes a JSON string
     *
     * @param imagenes Lista de URLs de imágenes
     * @return JSON string con las URLs
     */
    public String imagenesToJson(List<String> imagenes) {
        if (imagenes == null || imagenes.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(imagenes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir imágenes a JSON", e);
        }
    }

    /**
     * Convierte un JSON string a lista de URLs de imágenes
     *
     * @param imagenesJson JSON string con las URLs
     * @return Lista de URLs de imágenes
     */
    public List<String> jsonToImagenes(String imagenesJson) {
        if (imagenesJson == null || imagenesJson.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(imagenesJson, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            // Si hay error, retornar null en lugar de lanzar excepción
            return null;
        }
    }

    /**
     * Valida si una lista de imágenes es válida
     *
     * @param imagenes Lista de URLs de imágenes
     * @return true si es válida, false si no
     */
    public boolean validarImagenes(List<String> imagenes) {
        if (imagenes == null) {
            return true; // null es válido (sin imágenes)
        }

        // Validar que no esté vacía si se proporciona
        if (imagenes.isEmpty()) {
            return false;
        }

        // Validar que todas las URLs no estén vacías
        return imagenes.stream().allMatch(url -> url != null && !url.trim().isEmpty());
    }

    /**
     * Obtiene la primera imagen de una lista
     *
     * @param imagenes Lista de URLs de imágenes
     * @return Primera URL o null si no hay imágenes
     */
    public String obtenerPrimeraImagen(List<String> imagenes) {
        if (imagenes == null || imagenes.isEmpty()) {
            return null;
        }
        return imagenes.get(0);
    }

    /**
     * Obtiene el número de imágenes en una lista
     *
     * @param imagenes Lista de URLs de imágenes
     * @return Número de imágenes
     */
    public int contarImagenes(List<String> imagenes) {
        if (imagenes == null) {
            return 0;
        }
        return imagenes.size();
    }
}
