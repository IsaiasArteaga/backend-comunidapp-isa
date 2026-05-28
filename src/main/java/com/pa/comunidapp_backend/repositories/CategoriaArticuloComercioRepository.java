package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.CategoriaArticuloComercio;

@Repository
public interface CategoriaArticuloComercioRepository extends JpaRepository<CategoriaArticuloComercio, Long> {
    List<CategoriaArticuloComercio> findByComercioIdAndEliminadoEnIsNull(Long comercioId);

    Optional<CategoriaArticuloComercio> findByIdAndEliminadoEnIsNull(Long id);

    Optional<CategoriaArticuloComercio> findByComercioIdAndNombreAndEliminadoEnIsNull(Long comercioId, String nombre);
}
