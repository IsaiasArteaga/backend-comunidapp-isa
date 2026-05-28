package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.Comercio;

@Repository
public interface ComercioRepository extends JpaRepository<Comercio, Long> {

    List<Comercio> findByEliminadoEnIsNull();

    List<Comercio> findByEliminadoEnIsNullAndActivoTrue();

    List<Comercio> findByUsuarioIdAndEliminadoEnIsNull(Long usuarioId);

    Optional<Comercio> findByIdAndEliminadoEnIsNull(Long id);

    List<Comercio> findByNombreContainingIgnoreCaseAndEliminadoEnIsNull(String nombre);

    // Filtrados por categoría
    List<Comercio> findByEliminadoEnIsNullAndActivoTrueAndCategoriaId(Long categoriaId);

    // Búsqueda por nombre
    List<Comercio> findByEliminadoEnIsNullAndActivoTrueAndNombreContainingIgnoreCase(String nombre);

    // Combinado: categoría y nombre
    List<Comercio> findByEliminadoEnIsNullAndActivoTrueAndCategoriaIdAndNombreContainingIgnoreCase(Long categoriaId,
            String nombre);
}
