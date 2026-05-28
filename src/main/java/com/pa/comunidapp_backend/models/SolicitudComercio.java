package com.pa.comunidapp_backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitudes_comercio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudComercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String nombreNegocio;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 500)
    private String razonSocial;

    private String telefonoContacto;

    private String emailContacto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoSolicitudComercio estado;

    @Column(length = 1000)
    private String motivoRechazo;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime revisadoEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Usuario adminRevisor;
}
