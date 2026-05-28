package com.pa.comunidapp_backend.response;

import java.util.List;

import com.pa.comunidapp_backend.dto.MenuDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private Long id;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String direccion;
    private String avatarUrl;
    private Float ratingPromedio;

    private String rol;
    private List<MenuDTO> menus;
    private List<String> permisos;
}

