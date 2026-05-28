package com.pa.comunidapp_backend.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloActualizarDTO {

    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String titulo;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

        private Integer categoriaCodigo;

        private Integer condicionCodigo;

        private Integer estadoArticuloCodigo;

        private Integer tipoTransaccionCodigo;

    @PositiveOrZero(message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precio;

    private MultipartFile[] imagenes;

}
