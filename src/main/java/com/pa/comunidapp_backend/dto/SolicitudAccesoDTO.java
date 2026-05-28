package com.pa.comunidapp_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAccesoDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private String mensaje;
    private Integer estadoCodigo;
    private String estadoNombre;
    private LocalDateTime creadoEn;
    private LocalDateTime revisadoEn;
    private String motivoRechazo;
}
