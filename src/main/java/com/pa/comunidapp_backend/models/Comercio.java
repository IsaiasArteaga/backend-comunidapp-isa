package com.pa.comunidapp_backend.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
@Table(name = "comercios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaComercio categoria;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 500)
    private String direccion;

    private String telefono;

    private String email;

    @Column(length = 500)
    private String sitioWeb;

    @Column(nullable = false)
    private Boolean tieneEnvio = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(length = 500)
    private List<String> imagenes;

    @Column(nullable = false)
    private Float ratingPromedio = 5.0f;

    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime eliminadoEn;
}
