package com.pa.comunidapp_backend.dto;

import lombok.Data;

@Data
public class RegistroDTO {
        private String nombreCompleto;
        private String nombreUsuario;
        private String email;
        private String contrasena;
        private String telefono;
        private String direccion;
}
