package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Transaccion;

@Repository
public interface MisGestionesRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByUsuarioPropietarioIdAndEliminadoEnIsNull(Long usuarioPropietarioId);

    List<Transaccion> findByUsuarioSolicitanteIdAndEliminadoEnIsNull(Long usuarioSolicitanteId);

    List<Transaccion> findByArticuloIdAndEliminadoEnIsNull(Long articuloId);

    List<Transaccion> findByArticuloIdAndEstadoCodigoAndEliminadoEnIsNull(Long articuloId, Integer estadoCodigo);

    Optional<Transaccion> findByIdAndEliminadoEnIsNull(Long id);

    Optional<Transaccion> findByArticuloIdAndUsuarioSolicitanteIdAndEliminadoEnIsNull(Long articuloId, Long usuarioSolicitanteId);

    List<Transaccion> findByUsuarioPropietarioIdAndTipoCodigoAndEliminadoEnIsNull(Long usuarioPropietarioId, Integer tipoCodigo);

    List<Transaccion> findByUsuarioSolicitanteIdAndTipoCodigoAndEliminadoEnIsNull(Long usuarioSolicitanteId, Integer tipoCodigo);
}
