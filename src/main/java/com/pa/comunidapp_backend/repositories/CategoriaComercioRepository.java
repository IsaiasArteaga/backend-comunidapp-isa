package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.CategoriaComercio;

@Repository
public interface CategoriaComercioRepository extends JpaRepository<CategoriaComercio, Long> {
    Optional<CategoriaComercio> findByNombre(String nombre);

    List<CategoriaComercio> findByEliminadoEnIsNull();

    Optional<CategoriaComercio> findByCodigoAndEliminadoEnIsNull(Integer codigo);

    Optional<CategoriaComercio> findByIdAndEliminadoEnIsNull(Long id);
}
