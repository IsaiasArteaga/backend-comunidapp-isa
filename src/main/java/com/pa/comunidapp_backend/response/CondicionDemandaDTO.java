package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CondicionDemandaDTO {
    private Integer condicionCodigo;
    private String condicionNombre;
    private String nivelDemanda;
    private Double tasaVenta;
    private Double precioPromedio;
    private Double diasVentaPromedio;
}
