package com.pa.comunidapp_backend.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "categoriaConfiabilidad", "confiabilidadScore", "cantidadArticulosActivos",
        "velocidadVentaPromedio", "calificacionPromedioVentas", "precioPromedioArticulos" })
public class DatosGraficoConfiabilidadDTO {
    private String categoriaConfiabilidad;
    private Integer confiabilidadScore;
    private Long cantidadArticulosActivos;

    @Schema(description = "Velocidad de venta promedio en días (días promedio para vender un artículo)")
    private Double velocidadVentaPromedio;

    private Double calificacionPromedioVentas;
    private Double precioPromedioArticulos;
}
