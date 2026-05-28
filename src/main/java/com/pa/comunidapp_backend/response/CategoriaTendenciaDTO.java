package com.pa.comunidapp_backend.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaTendenciaDTO {
    private Integer categoriaCodigo;
    private String categoriaNombre;
    private String tendencia;
    private Double variacionPorcentaje;
    private List<DatoMensualDTO> datosMensual;
}
