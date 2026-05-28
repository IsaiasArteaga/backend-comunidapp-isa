package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.TipoTransaccion;

@Repository
public interface TipoTransaccionRepository extends JpaRepository<TipoTransaccion, Long> {
    Optional<TipoTransaccion> findByCodigo(Integer codigo);

    // Buscar tipos de transacción no eliminados
    List<TipoTransaccion> findByEliminadoEnIsNull();

    // Buscar un tipo de transacción específico no eliminado
    Optional<TipoTransaccion> findByIdAndEliminadoEnIsNull(Long id);

    // Buscar tipo por código (no eliminado)
    Optional<TipoTransaccion> findByCodigoAndEliminadoEnIsNull(Long codigo);

    // Buscar tipo por código (incluyendo eliminados)
    Optional<TipoTransaccion> findByCodigo(Long codigo);

    // Buscar tipos de transacción por nombre (no eliminados)
    List<TipoTransaccion> findByNombreContainingIgnoreCaseAndEliminadoEnIsNull(String nombre);
}
