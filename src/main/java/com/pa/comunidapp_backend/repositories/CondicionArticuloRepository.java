package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.CondicionArticulo;

@Repository
public interface CondicionArticuloRepository extends JpaRepository<CondicionArticulo, Long> {
    Optional<CondicionArticulo> findByCodigo(Integer codigo);

    // Buscar condiciones de artículo no eliminadas
    List<CondicionArticulo> findByEliminadoEnIsNull();

    // Buscar una condición de artículo específica no eliminada
    Optional<CondicionArticulo> findByIdAndEliminadoEnIsNull(Long id);

    // Buscar condición por código (no eliminada)
    Optional<CondicionArticulo> findByCodigoAndEliminadoEnIsNull(Long codigo);

    // Buscar condición por código (incluyendo eliminadas)
    Optional<CondicionArticulo> findByCodigo(Long codigo);

    // Buscar condiciones de artículo por nombre (no eliminadas)
    List<CondicionArticulo> findByNombreContainingIgnoreCaseAndEliminadoEnIsNull(String nombre);
}
