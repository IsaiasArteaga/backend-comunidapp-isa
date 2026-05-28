package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetallesConfiabilidadDTO {
    private Double ratingPromedio;
    private Long transaccionesTotales;
    private Long transaccionesCompletadas;
    private Double tasaCumplimiento;
    private Long prestamosATiempo;
    private Long transaccionesRetrasadas;
    private Long diasAntiguedad;
    private Double calificacionesPromedio;
    private String tendencia;
}
