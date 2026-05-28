package com.pa.comunidapp_backend.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menus")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String ruta;

    private String icono;

    private Integer orden = 0;

    private LocalDateTime eliminadoEn;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt = LocalDateTime.now();

    @jakarta.persistence.Column(name = "por_defecto", nullable = false, columnDefinition = "boolean default false")
    private Boolean porDefecto = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_padre_id")
    private Menu menuPadre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permiso_id")
    private Permiso permiso;

    @OneToMany(mappedBy = "menuPadre", fetch = FetchType.EAGER)
    private List<Menu> hijos = new ArrayList<>();
}
