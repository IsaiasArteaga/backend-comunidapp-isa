package com.pa.comunidapp_backend.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloComercioResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private Integer categoriaCodigo;
    private String categoriaNombre;
    private Integer condicionCodigo;
    private String condicionNombre;
    private BigDecimal precio;
    private List<String> imagenes;
    private Long categoriaArticuloComercioId;
    private String categoriaArticuloComercioNombre;
}
