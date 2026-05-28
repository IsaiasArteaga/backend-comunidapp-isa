package com.pa.comunidapp_backend.models;

import java.math.BigDecimal;
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
@Table(name = "articulos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Long comercioId;
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private Integer categoriaCodigo;
    private Integer condicionCodigo;
    private Integer estadoArticuloCodigo;
    private Integer tipoTransaccionCodigo;

    private BigDecimal precio;

    @Column(columnDefinition = "TEXT")
    private String imagenes;

    @Column(columnDefinition = "JSON")
    private String propietario;

    @Column(columnDefinition = "JSON")
    private String solicitante;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime eliminadoEn;
}
