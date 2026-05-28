package com.pa.comunidapp_backend.models;

import java.math.BigDecimal;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "articulos_comercios")
public class ArticuloComercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comercio_id", nullable = false)
    private Comercio comercio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_articulo_comercio_id")
    private CategoriaArticuloComercio categoriaArticuloComercio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoArticulo estado;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer categoriaCodigo;

    @Column(nullable = false)
    private Integer condicionCodigo;

    @Column(nullable = false)
    private Integer tipoTransaccionCodigo;

    @Column(nullable = false)
    private BigDecimal precio;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(length = 500)
    private List<String> imagenes;

    @Column(nullable = false)
    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;

    private LocalDateTime eliminadoEn;
}
