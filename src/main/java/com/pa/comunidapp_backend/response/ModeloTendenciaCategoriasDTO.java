package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeloTendenciaCategoriasDTO {
    private String nombre;
    private String descripcion;
    private String graficoTipo;
    private DatosGraficoTendenciaDTO datosGrafico;
}
