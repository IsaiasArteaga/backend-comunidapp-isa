package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByCodigo(Integer codigo);

    // Buscar categorías no eliminadas
    List<Categoria> findByEliminadoEnIsNull();

    // Buscar una categoría específica no eliminada
    Optional<Categoria> findByIdAndEliminadoEnIsNull(Long id);

    // Buscar categoría por código (no eliminada)
    Optional<Categoria> findByCodigoAndEliminadoEnIsNull(Long codigo);

    // Buscar categoría por código (incluyendo eliminadas)
    Optional<Categoria> findByCodigo(Long codigo);

    // Buscar categorías por nombre (no eliminadas)
    List<Categoria> findByNombreContainingIgnoreCaseAndEliminadoEnIsNull(String nombre);
}
