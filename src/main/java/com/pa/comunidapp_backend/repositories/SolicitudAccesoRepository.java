package com.pa.comunidapp_backend.repositories;

import com.pa.comunidapp_backend.models.SolicitudAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudAccesoRepository extends JpaRepository<SolicitudAcceso, Long> {
    
    List<SolicitudAcceso> findByEstadoCodigoOrderByCreadoEnDesc(Integer codigoEstado);
    
    List<SolicitudAcceso> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);
    
    List<SolicitudAcceso> findByUsuarioIdAndEstadoCodigo(Long usuarioId, Integer estadoCodigo);
}
