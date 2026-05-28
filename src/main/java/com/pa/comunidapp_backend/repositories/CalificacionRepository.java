package com.pa.comunidapp_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pa.comunidapp_backend.models.Calificacion;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    List<Calificacion> findByUsuarioCalificadoIdAndEliminadoEnIsNull(Long usuarioId);
    List<Calificacion> findByTransaccionIdAndEliminadoEnIsNull(Long transaccionId);
}
