package com.pa.comunidapp_backend.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudResponseDTO {

    private Long id;
    private Long articuloId;
    private String articuloTitulo;
    private UsuarioBasicoDTO solicitante;
    private UsuarioBasicoDTO propietario;
    private String mensaje;
    private Integer estadoCodigo;
    private Integer tipoCodigo;
    private LocalDateTime creadoEn;
    private LocalDateTime respondidoEn;
}

