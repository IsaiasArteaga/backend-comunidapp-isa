package com.pa.comunidapp_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMensajeDTO {
    private String mensaje;
    private Long usuarioId;
}
