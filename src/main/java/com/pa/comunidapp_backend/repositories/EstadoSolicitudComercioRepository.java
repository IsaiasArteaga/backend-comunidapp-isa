package com.pa.comunidapp_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.EstadoSolicitudComercio;

@Repository
public interface EstadoSolicitudComercioRepository extends JpaRepository<EstadoSolicitudComercio, Long> {

    Optional<EstadoSolicitudComercio> findByCodigo(Integer codigo);

    Optional<EstadoSolicitudComercio> findByNombre(String nombre);
}
