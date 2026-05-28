package com.pa.comunidapp_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioActualizarDTO {
    private String nombreCompleto;
    private String email;
    private String direccion;
    private String telefono;
}
