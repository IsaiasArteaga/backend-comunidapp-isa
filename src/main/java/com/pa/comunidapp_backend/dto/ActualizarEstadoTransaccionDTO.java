package com.pa.comunidapp_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarEstadoTransaccionDTO {
    private Integer estadoCodigo;
    private String mensajeRespuesta;
}
