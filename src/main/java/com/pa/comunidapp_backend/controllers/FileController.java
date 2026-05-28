package com.pa.comunidapp_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para archivos.
 *
 * NOTA: Con Cloudinary, las imágenes se sirven directamente desde sus URLs.
 * Este controlador se mantiene por compatibilidad pero ya no es necesario.
 * Las URLs de Cloudinary son públicas y se pueden usar directamente en el
 * frontend.
 */
@RestController
@RequestMapping("/api/archivos")
@Tag(name = "Archivos")
public class FileController {

    /**
     * Endpoint obsoleto: Las imágenes ahora se sirven directamente desde
     * Cloudinary.
     *
     * @param nombreArchivo Nombre del archivo (ya no se usa)
     * @return Respuesta indicando que se debe usar la URL de Cloudinary
     */
    @GetMapping("/imagen/{nombreArchivo:.+}")
    public ResponseEntity<String> obtenerImagen(@PathVariable String nombreArchivo) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("Este endpoint ya no está disponible. Las imágenes se sirven directamente desde Cloudinary usando sus URLs.");
    }
}
