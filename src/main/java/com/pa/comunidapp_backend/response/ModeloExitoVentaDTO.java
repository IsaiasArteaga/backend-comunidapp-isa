package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeloExitoVentaDTO {
    private String nombre;
    private String descripcion;
    private Double tasaExitoGeneral;
    private String graficoTipo;
    private DatosGraficoExitoVentaDTO datosGrafico;
}
