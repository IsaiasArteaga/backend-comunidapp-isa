package com.pa.comunidapp_backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "calificaciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long transaccionId; // Referencia a la transacción
    private Long usuarioQueCalificaId; // Quien da la calificación
    private Long usuarioCalificadoId; // Quien recibe la calificación
    private Integer puntuacion; // 1-5 estrellas

    @Column(columnDefinition = "TEXT")
    private String comentario;

    private LocalDateTime creadoEn;
    private LocalDateTime eliminadoEn;
}
