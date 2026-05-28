package com.pa.comunidapp_backend.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloUsuarioResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private Integer categoriaCodigo;
    private String categoriaNombre;
    private Integer condicionCodigo;
    private String condicionNombre;
    private Integer estadoArticuloCodigo;
    private String estadoArticuloNombre;
    private Integer tipoTransaccionCodigo;
    private String tipoTransaccionNombre;
    private BigDecimal precio;
    private List<String> imagenes;
    private LocalDateTime creadoEn;
}
