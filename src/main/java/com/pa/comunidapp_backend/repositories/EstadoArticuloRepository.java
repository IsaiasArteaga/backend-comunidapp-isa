package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.EstadoArticulo;

@Repository
public interface EstadoArticuloRepository extends JpaRepository<EstadoArticulo, Long> {
    Optional<EstadoArticulo> findByCodigo(Integer codigo);

    // Buscar estados de artículo no eliminados
    List<EstadoArticulo> findByEliminadoEnIsNull();

    // Buscar un estado de artículo específico no eliminado
    Optional<EstadoArticulo> findByIdAndEliminadoEnIsNull(Long id);

    // Buscar estado por código (no eliminado)
    Optional<EstadoArticulo> findByCodigoAndEliminadoEnIsNull(Long codigo);

    // Buscar estado por código (incluyendo eliminados)
    Optional<EstadoArticulo> findByCodigo(Long codigo);

    // Buscar estados de artículo por nombre (no eliminados)
    List<EstadoArticulo> findByNombreContainingIgnoreCaseAndEliminadoEnIsNull(String nombre);
}
