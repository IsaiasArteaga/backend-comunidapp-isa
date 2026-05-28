package com.pa.comunidapp_backend.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalificacionDetalleDTO {
    private Integer puntuacion;            // 1-5 estrellas
    private String comentario;             // Comentario opcional
    private LocalDateTime fecha;           // Fecha de la calificación
    private String usuarioQueCalificaNombre; // Nombre de quien califica
    private Long usuarioQueCalificaId;     // ID de quien califica
}
