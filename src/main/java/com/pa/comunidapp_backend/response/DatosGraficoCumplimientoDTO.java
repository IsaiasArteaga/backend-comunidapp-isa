package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatosGraficoCumplimientoDTO {
    private Double tasaCumplimientoPorcentaje;
    private Double tasaRetrasoPorcentaje;
    private Long prestamosTotales;
    private Long prestamosCumplidos;
    private Long prestamosRetrasados;
    private Double retrasoPromedioDias;
    private String color;
}
