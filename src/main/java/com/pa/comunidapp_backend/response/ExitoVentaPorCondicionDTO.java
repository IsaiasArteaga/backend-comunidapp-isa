package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExitoVentaPorCondicionDTO {
    private Integer condicionCodigo;
    private String condicionNombre;
    private Double tasaExito;
    private Long cantidadArticulos;
    private Long cantidadVendidos;
}
