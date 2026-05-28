package com.pa.comunidapp_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pa.comunidapp_backend.models.ArticuloComercio;

@Repository
public interface ArticuloComercioRepository extends JpaRepository<ArticuloComercio, Long> {
    List<ArticuloComercio> findByComercioIdAndEliminadoEnIsNull(Long comercioId);

    Optional<ArticuloComercio> findByIdAndEliminadoEnIsNull(Long id);

    List<ArticuloComercio> findByComercioIdAndCategoriaArticuloComercioIdAndEliminadoEnIsNull(Long comercioId,
            Long categoriaId);
}
