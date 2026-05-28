package com.pa.comunidapp_backend.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "prediccion", "confianzaPrediccion", "diasDesdeUltimaActividad",
        "articulosPublicadosUltimoMes", "frecuenciaConexionSemanal", "tasaTransaccionesCompletadas" })
public class DatosGraficoActividadDTO {
    private String prediccion;
    private Double confianzaPrediccion;
    private Long diasDesdeUltimaActividad;
    private Long articulosPublicadosUltimoMes;
    private Long frecuenciaConexionSemanal;
    private Double tasaTransaccionesCompletadas;
}
