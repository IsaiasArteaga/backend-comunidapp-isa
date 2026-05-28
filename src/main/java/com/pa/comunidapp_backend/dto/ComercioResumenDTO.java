package com.pa.comunidapp_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComercioResumenDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private String telefono;
    private String email;
    private List<String> imagenes;
    private String sitioWeb;
    private Boolean tieneEnvio;
    private String categoriaNombre;
}
