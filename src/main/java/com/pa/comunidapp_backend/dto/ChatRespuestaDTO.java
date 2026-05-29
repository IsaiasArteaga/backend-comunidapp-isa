package com.pa.comunidapp_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRespuestaDTO {
    private String respuesta;
    private String modelo;
    private Integer tokensUsados;
}
