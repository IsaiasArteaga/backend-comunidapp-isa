package com.pa.comunidapp_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {

    private Long id;
    private String nombre;
    private String ruta;
    private String icono;
    private Integer orden;
    private Boolean porDefecto;
    private List<MenuDTO> hijos;
}
