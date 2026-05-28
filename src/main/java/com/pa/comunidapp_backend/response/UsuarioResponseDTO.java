package com.pa.comunidapp_backend.response;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
        private Long id;
        private String nombre;
        private String email;
        private String telefono;
        private String direccion;
        private String zona;
        private String avatarUrl;
        private Float ratingPromedio;
}
