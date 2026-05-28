package com.pa.comunidapp_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pa.comunidapp_backend.dto.CategoriaArticuloComercioDTO;
import com.pa.comunidapp_backend.dto.CategoriaArticuloComercioResponseDTO;
import com.pa.comunidapp_backend.models.CategoriaArticuloComercio;
import com.pa.comunidapp_backend.models.CategoriaArticuloComercioFiltro;
import com.pa.comunidapp_backend.models.Comercio;
import com.pa.comunidapp_backend.repositories.CategoriaArticuloComercioRepository;
import com.pa.comunidapp_backend.repositories.ComercioRepository;

@Service
public class CategoriaArticuloComercioService {

    @Autowired
    private CategoriaArticuloComercioRepository categoriaRepository;

    @Autowired
    private ComercioRepository comercioRepository;

    /**
     * Crea una nueva categoría para artículos de un comercio
     */
    public CategoriaArticuloComercio crearCategoria(Long comercioId, CategoriaArticuloComercioDTO dto) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));

        // Verificar que no exista ya una categoría con ese nombre en ese comercio
        Optional<CategoriaArticuloComercio> existente = categoriaRepository
                .findByComercioIdAndNombreAndEliminadoEnIsNull(comercioId, dto.getNombre());
        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe una categoría con ese nombre en este comercio");
        }

        CategoriaArticuloComercio categoria = new CategoriaArticuloComercio();
        categoria.setComercio(comercio);
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setCreadoEn(LocalDateTime.now());

        return categoriaRepository.save(categoria);
    }

    /**
     * Obtiene todas las categorías de un comercio
     */
    public List<CategoriaArticuloComercioResponseDTO> obtenerCategoriasComercio(Long comercioId) {
        return categoriaRepository.findByComercioIdAndEliminadoEnIsNull(comercioId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Obtiene todas las categorías de un comercio en formato filtro
     */
    public List<CategoriaArticuloComercioFiltro> obtenerCategoriasComercioFiltro(Long comercioId) {
        return categoriaRepository.findByComercioIdAndEliminadoEnIsNull(comercioId).stream()
                .map(this::mapToFiltro)
                .toList();
    }

    /**
     * Obtiene una categoría específica
     */
    public Optional<CategoriaArticuloComercioResponseDTO> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findByIdAndEliminadoEnIsNull(id)
                .map(this::mapToResponseDTO);
    }

    /**
     * Actualiza una categoría
     */
    public CategoriaArticuloComercio actualizarCategoria(Long id, CategoriaArticuloComercioDTO dto) {
        CategoriaArticuloComercio categoria = categoriaRepository.findByIdAndEliminadoEnIsNull(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        return categoriaRepository.save(categoria);
    }

    /**
     * Elimina una categoría (soft delete)
     */
    public void eliminarCategoria(Long id) {
        CategoriaArticuloComercio categoria = categoriaRepository.findByIdAndEliminadoEnIsNull(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        categoria.setEliminadoEn(LocalDateTime.now());
        categoriaRepository.save(categoria);
    }

    private CategoriaArticuloComercioResponseDTO mapToResponseDTO(CategoriaArticuloComercio categoria) {
        return new CategoriaArticuloComercioResponseDTO(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion());
    }

    private CategoriaArticuloComercioFiltro mapToFiltro(CategoriaArticuloComercio categoria) {
        return new CategoriaArticuloComercioFiltro(
                categoria.getId().intValue(),
                categoria.getNombre());
    }
}
