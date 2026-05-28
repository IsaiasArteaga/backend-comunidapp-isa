package com.pa.comunidapp_backend.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloCrearDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String titulo;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El código de categoría es obligatorio")
    private Integer categoriaCodigo;

    @NotNull(message = "El código de condición es obligatorio")
    private Integer condicionCodigo;

    @NotNull(message = "El código de tipo de transacción es obligatorio")
    private Integer tipoTransaccionCodigo;

    @PositiveOrZero(message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precio;

    private MultipartFile[] imagenes;
}
