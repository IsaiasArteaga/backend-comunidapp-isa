package com.pa.comunidapp_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

/**
 * Configuración de Cloudinary.
 *
 * Soporta dos formas de configuración:
 * 1. Variable de entorno CLOUDINARY_URL (recomendado)
 * 2. Propiedades individuales (cloudinary.cloud-name, api-key, api-secret)
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        // Si existe CLOUDINARY_URL como variable de entorno, Cloudinary la detectará
        // automáticamente
        // Si no, usamos las propiedades individuales con ObjectUtils.asMap()
        // (recomendado por Cloudinary)

        if (cloudName != null && !cloudName.isEmpty() &&
                apiKey != null && !apiKey.isEmpty() &&
                apiSecret != null && !apiSecret.isEmpty()) {

            // Usar ObjectUtils.asMap() como recomienda la documentación oficial
            return new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true // Recomendado por Cloudinary para URLs HTTPS
            ));
        }

        // Si no hay configuración, Cloudinary intentará usar CLOUDINARY_URL
        // automáticamente
        // o lanzará una excepción si no está configurado
        return new Cloudinary();
    }
}
