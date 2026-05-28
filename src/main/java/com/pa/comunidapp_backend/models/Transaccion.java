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
@Table(name = "transacciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long articuloId;
    private Long usuarioPropietarioId;
    private Long usuarioSolicitanteId;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(columnDefinition = "TEXT")
    private String mensajeRespuesta;

    private Integer estadoCodigo; // 1=PENDIENTE, 2=ACEPTADA, 3=RECHAZADA, 4=DEVUELTO
    private Integer tipoCodigo; // 1=SOLICITUD, 2=PRESTAMO

    private LocalDateTime creadoEn;
    private LocalDateTime respondidoEn;
    private LocalDateTime eliminadoEn;
    private LocalDateTime fechaEstimadaDevolucion;
}
