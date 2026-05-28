package com.pa.comunidapp_backend.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrediccionesResponseDTO {
    private LocalDateTime timestamp;
    private ModelosGlobalesDTO modelosGlobales;
    private ModelosUsuarioDTO modelosUsuario;
}
