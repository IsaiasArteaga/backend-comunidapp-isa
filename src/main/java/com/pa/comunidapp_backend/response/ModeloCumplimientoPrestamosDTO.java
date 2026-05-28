package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeloCumplimientoPrestamosDTO {
    private String nombre;
    private String descripcion;
    private Double tasaCumplimiento;
    private String graficoTipo;
    private DatosGraficoCumplimientoDTO datosGrafico;
}
