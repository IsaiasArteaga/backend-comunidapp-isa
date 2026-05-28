package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExitoVentaPorCategoriaDTO {
    private Integer categoriaCodigo;
    private String categoriaNombre;
    private Double tasaExito;
    private Long cantidadArticulos;
    private Long cantidadVendidos;
}
