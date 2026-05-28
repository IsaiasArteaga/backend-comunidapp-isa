package com.pa.comunidapp_backend.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloComercioActualizarDTO {
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "El código de categoría es obligatorio")
    private Integer categoriaCodigo;

    @NotNull(message = "El código de condición es obligatorio")
    private Integer condicionCodigo;

    private java.math.BigDecimal precio;

    private Long categoriaComercioId;

    private MultipartFile[] imagenes;
}
