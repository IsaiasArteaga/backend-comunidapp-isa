package com.pa.comunidapp_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudArticuloCrearDTO {

    @NotNull(message = "El ID del artículo es requerido")
    private Long articuloId;

    private String mensaje;
    private java.time.LocalDateTime fechaEstimadaDevolucion;
}
