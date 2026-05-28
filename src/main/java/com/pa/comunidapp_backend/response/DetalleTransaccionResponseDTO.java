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
public class DetalleTransaccionResponseDTO {
    private Long id;
    private String nombreArticulo;
    private List<String> imagenes;
    private Long propietarioId;
    private Integer tipoCodigo;
    private String tipoNombre;
    private LocalDateTime fechaSolicitud;
    private String mensaje;
    private String mensajeRespuesta;
    private String categoriaNombre;
    private Integer estadoCodigo;
    private String estadoNombre;
    private Integer condicionCodigo;
    private String condicionNombre;
    private UsuarioBasicoDTO solicitante;
    private UsuarioBasicoDTO propietario;
    private BigDecimal precio;
    private LocalDateTime fechaEstimadaDevolucion;
}
