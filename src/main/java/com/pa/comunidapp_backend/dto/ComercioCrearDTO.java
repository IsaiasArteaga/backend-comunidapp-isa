package com.pa.comunidapp_backend.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComercioCrearDTO {

    @NotBlank(message = "El nombre del comercio es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @Email(message = "El email debe ser válido")
    private String email;

    @Size(max = 500, message = "El sitio web no puede exceder 500 caracteres")
    private String sitioWeb;

    private Boolean tieneEnvio = false;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private MultipartFile[] imagenes;
}
