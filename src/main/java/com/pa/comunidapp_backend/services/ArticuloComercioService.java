package com.pa.comunidapp_backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pa.comunidapp_backend.dto.ArticuloComercioActualizarDTO;
import com.pa.comunidapp_backend.dto.ArticuloComercioCrearDTO;
import com.pa.comunidapp_backend.dto.ArticuloComercioResponseDTO;
import com.pa.comunidapp_backend.models.ArticuloComercio;
import com.pa.comunidapp_backend.models.Categoria;
import com.pa.comunidapp_backend.models.CategoriaArticuloComercio;
import com.pa.comunidapp_backend.models.Comercio;
import com.pa.comunidapp_backend.models.CondicionArticulo;
import com.pa.comunidapp_backend.models.EstadoArticulo;
import com.pa.comunidapp_backend.repositories.ArticuloComercioRepository;
import com.pa.comunidapp_backend.repositories.CategoriaArticuloComercioRepository;
import com.pa.comunidapp_backend.repositories.CategoriaRepository;
import com.pa.comunidapp_backend.repositories.ComercioRepository;
import com.pa.comunidapp_backend.repositories.CondicionArticuloRepository;
import com.pa.comunidapp_backend.repositories.EstadoArticuloRepository;

@Service
public class ArticuloComercioService {

    @Autowired
    private ArticuloComercioRepository articuloComercioRepository;

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private CategoriaArticuloComercioRepository categoriaArticuloComercioRepository;

    @Autowired
    private EstadoArticuloRepository estadoArticuloRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CondicionArticuloRepository condicionArticuloRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Crea un nuevo artículo para el comercio
     */
    public ArticuloComercio crearArticuloComercio(Long comercioId, ArticuloComercioCrearDTO dto) {
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));

        ArticuloComercio articulo = new ArticuloComercio();
        articulo.setComercio(comercio);
        articulo.setNombre(dto.getTitulo());
        articulo.setDescripcion(dto.getDescripcion());
        articulo.setCategoriaCodigo(dto.getCategoriaCodigo());
        articulo.setCondicionCodigo(dto.getCondicionCodigo());
        articulo.setPrecio(dto.getPrecio() != null ? dto.getPrecio() : java.math.BigDecimal.ZERO);

        // Asignar tipo de transacción automático (Venta - código 1)
        articulo.setTipoTransaccionCodigo(1);

        // Asignar estado por defecto (Activo - código 1)
        EstadoArticulo estado = estadoArticuloRepository.findByCodigo(1)
                .orElseThrow(() -> new RuntimeException("Estado de artículo por defecto no encontrado"));
        articulo.setEstado(estado);

        articulo.setCreadoEn(java.time.LocalDateTime.now());
        articulo.setActualizadoEn(java.time.LocalDateTime.now());

        // Si tiene categoría de comercio, asignarla
        if (dto.getCategoriaComercioId() != null) {
            CategoriaArticuloComercio categoria = categoriaArticuloComercioRepository
                    .findByIdAndEliminadoEnIsNull(dto.getCategoriaComercioId())
                    .orElseThrow(() -> new RuntimeException("Categoría de comercio no encontrada"));
            articulo.setCategoriaArticuloComercio(categoria);
        }

        // Guardar imágenes
        org.springframework.web.multipart.MultipartFile[] imagenes = dto.getImagenes();
        if (imagenes != null && imagenes.length > 0) {
            java.util.List<String> rutasImagenes = fileStorageService.guardarImagenes(imagenes);
            if (!rutasImagenes.isEmpty()) {
                articulo.setImagenes(rutasImagenes);
            }
        }

        return articuloComercioRepository.save(articulo);
    }

    /**
     * Obtiene todos los artículos de un comercio
     */
    public List<ArticuloComercioResponseDTO> obtenerArticulosComercio(Long comercioId) {
        return articuloComercioRepository.findByComercioIdAndEliminadoEnIsNull(comercioId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    /**
     * Obtiene un artículo específico
     */
    public Optional<ArticuloComercioResponseDTO> obtenerArticuloPorId(Long id) {
        return articuloComercioRepository.findByIdAndEliminadoEnIsNull(id)
                .map(this::mapToResponseDTO);
    }

    /**
     * Obtiene artículos de un comercio filtrados por categoría
     */
    public List<ArticuloComercioResponseDTO> obtenerArticulosPorCategoria(Long comercioId, Long categoriaId) {
        return articuloComercioRepository
                .findByComercioIdAndCategoriaArticuloComercioIdAndEliminadoEnIsNull(comercioId, categoriaId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private ArticuloComercioResponseDTO mapToResponseDTO(ArticuloComercio articulo) {
        // Obtener nombre de categoría
        String categoriaNombre = null;
        if (articulo.getCategoriaCodigo() != null) {
            Optional<Categoria> categoria = categoriaRepository.findByCodigo(articulo.getCategoriaCodigo());
            categoriaNombre = categoria.map(Categoria::getNombre).orElse(null);
        }

        // Obtener nombre de condición
        String condicionNombre = null;
        if (articulo.getCondicionCodigo() != null) {
            Optional<CondicionArticulo> condicion = condicionArticuloRepository
                    .findByCodigo(articulo.getCondicionCodigo());
            condicionNombre = condicion.map(CondicionArticulo::getNombre).orElse(null);
        }

        return new ArticuloComercioResponseDTO(
                articulo.getId(),
                articulo.getNombre(),
                articulo.getDescripcion(),
                articulo.getCategoriaCodigo(),
                categoriaNombre,
                articulo.getCondicionCodigo(),
                condicionNombre,
                articulo.getPrecio(),
                articulo.getImagenes(),
                articulo.getCategoriaArticuloComercio() != null ? articulo.getCategoriaArticuloComercio().getId()
                        : null,
                articulo.getCategoriaArticuloComercio() != null ? articulo.getCategoriaArticuloComercio().getNombre()
                        : null);
    }

    /**
     * Actualiza un artículo
     */
    public ArticuloComercio actualizarArticuloComercio(Long id, ArticuloComercioActualizarDTO dto) {
        ArticuloComercio articulo = articuloComercioRepository.findByIdAndEliminadoEnIsNull(id)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        articulo.setNombre(dto.getTitulo());
        articulo.setDescripcion(dto.getDescripcion());
        articulo.setCategoriaCodigo(dto.getCategoriaCodigo());
        articulo.setCondicionCodigo(dto.getCondicionCodigo());
        if (dto.getPrecio() != null) {
            articulo.setPrecio(dto.getPrecio());
        }

        // Si tiene categoría de comercio, asignarla
        if (dto.getCategoriaComercioId() != null) {
            CategoriaArticuloComercio categoria = categoriaArticuloComercioRepository
                    .findByIdAndEliminadoEnIsNull(dto.getCategoriaComercioId())
                    .orElseThrow(() -> new RuntimeException("Categoría de comercio no encontrada"));
            articulo.setCategoriaArticuloComercio(categoria);
        } else {
            articulo.setCategoriaArticuloComercio(null);
        }

        // Actualizar imágenes si se proporcionan
        org.springframework.web.multipart.MultipartFile[] imagenes = dto.getImagenes();
        if (imagenes != null && imagenes.length > 0) {
            java.util.List<String> rutasImagenes = fileStorageService.guardarImagenes(imagenes);
            if (!rutasImagenes.isEmpty()) {
                articulo.setImagenes(rutasImagenes);
            }
        }

        articulo.setActualizadoEn(LocalDateTime.now());
        return articuloComercioRepository.save(articulo);
    }

    /**
     * Elimina un artículo (soft delete)
     */
    public void eliminarArticuloComercio(Long id) {
        ArticuloComercio articulo = articuloComercioRepository.findByIdAndEliminadoEnIsNull(id)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
        articulo.setEliminadoEn(LocalDateTime.now());
        articuloComercioRepository.save(articulo);
    }
}
