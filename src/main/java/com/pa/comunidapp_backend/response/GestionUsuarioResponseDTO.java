package com.pa.comunidapp_backend.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GestionUsuarioResponseDTO {
    private Long id;
    private String nombreArticulo;
    private String imagenArticulo;
    private Long propietarioId;
    private Integer tipoCodigo;
    private String tipoNombre;
    private LocalDateTime fechaSolicitud;
    private String mensaje;
    private String mensajeRespuesta;
    private String categoriaNombre;
    private Integer estadoCodigo;
    private String estadoNombre;
    private UsuarioBasicoDTO solicitante;
    private UsuarioBasicoDTO propietario;
    private BigDecimal precio;
    private java.time.LocalDateTime fechaEstimadaDevolucion;
}
