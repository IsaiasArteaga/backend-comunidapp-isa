package com.pa.comunidapp_backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "solicitudes_acceso")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudAcceso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoSolicitudComercio estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Usuario adminRevisor;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;
    
    @UpdateTimestamp
    private LocalDateTime actualizadoEn;
    
    private LocalDateTime revisadoEn;
}
