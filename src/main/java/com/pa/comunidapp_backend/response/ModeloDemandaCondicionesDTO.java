package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeloDemandaCondicionesDTO {
    private String nombre;
    private String descripcion;
    private String graficoTipo;
    private DatosGraficoDemandaDTO datosGrafico;
}
