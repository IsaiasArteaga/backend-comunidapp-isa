package com.pa.comunidapp_backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComponentesScoreDTO {
    private Integer ratingContribucion;
    private Integer transaccionesContribucion;
    private Integer cumplimientoContribucion;
    private Integer antiguedadContribucion;
    private Integer calificacionesContribucion;
}
