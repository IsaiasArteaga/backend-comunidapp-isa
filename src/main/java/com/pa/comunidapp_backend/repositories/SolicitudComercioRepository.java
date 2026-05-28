package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.EstadoSolicitudComercio;
import com.pa.comunidapp_backend.models.SolicitudComercio;

@Repository
public interface SolicitudComercioRepository extends JpaRepository<SolicitudComercio, Long> {

    List<SolicitudComercio> findByEstado(EstadoSolicitudComercio estado);

    List<SolicitudComercio> findByUsuarioId(Long usuarioId);

    Optional<SolicitudComercio> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<SolicitudComercio> findByEstadoOrderByCreadoEnDesc(EstadoSolicitudComercio estado);
}
