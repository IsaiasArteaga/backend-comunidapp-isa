package com.pa.comunidapp_backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pa.comunidapp_backend.dto.ArticuloComercioActualizarDTO;
import com.pa.comunidapp_backend.dto.ArticuloComercioCrearDTO;
import com.pa.comunidapp_backend.dto.ArticuloComercioResponseDTO;
import com.pa.comunidapp_backend.dto.CategoriaArticuloComercioDTO;
import com.pa.comunidapp_backend.dto.CategoriaArticuloComercioResponseDTO;
import com.pa.comunidapp_backend.dto.ComercioCrearDTO;
import com.pa.comunidapp_backend.dto.ComercioDetalleDTO;
import com.pa.comunidapp_backend.dto.ComercioResumenDTO;
import com.pa.comunidapp_backend.services.ArticuloComercioService;
import com.pa.comunidapp_backend.services.CategoriaArticuloComercioService;
import com.pa.comunidapp_backend.services.ComercioService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Comercios")
@RequestMapping("/api/comercios")
public class ComercioController {

    @Autowired
    private ComercioService comercioService;

    @Autowired
    private ArticuloComercioService articuloComercioService;

    @Autowired
    private CategoriaArticuloComercioService categoriaArticuloComercioService;

    @GetMapping
    public ResponseEntity<List<ComercioResumenDTO>> obtenerTodosComercios() {
        List<ComercioResumenDTO> comercios = comercioService.obtenerTodosComercios();
        return ResponseEntity.ok(comercios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComercioDetalleDTO> obtenerComercioById(@PathVariable Long id) {
        Optional<ComercioDetalleDTO> comercio = comercioService.obtenerComercioByIdConArticulos(id);
        return comercio.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ComercioResumenDTO>> obtenerComerciosPorUsuario(@PathVariable Long usuarioId) {
        List<ComercioResumenDTO> comercios = comercioService.obtenerComerciosPorUsuarioDTO(usuarioId);
        return ResponseEntity.ok(comercios);
    }

    @PutMapping(value = "/{comercioId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> actualizarComercio(
            @PathVariable Long comercioId,
            @RequestParam Long usuarioId,
            @Valid @ModelAttribute ComercioCrearDTO comercioDTO) {
        comercioService.actualizarComercio(comercioId, usuarioId, comercioDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comercioId}")
    public ResponseEntity<Void> eliminarComercio(
            @PathVariable Long comercioId,
            @RequestParam Long usuarioId) {
        comercioService.desactivarComercio(comercioId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> crearComercio(
            @RequestParam Long usuarioId,
            @Valid @ModelAttribute ComercioCrearDTO comercioDTO) {
        comercioService.crearComercio(usuarioId, comercioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/{comercioId}/articulos/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> crearArticuloComercio(
            @PathVariable Long comercioId,
            @Valid @ModelAttribute ArticuloComercioCrearDTO dto) {
        articuloComercioService.crearArticuloComercio(comercioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{comercioId}/categorias")
    public ResponseEntity<List<CategoriaArticuloComercioResponseDTO>> obtenerCategoriasComercio(
            @PathVariable Long comercioId) {
        List<CategoriaArticuloComercioResponseDTO> categorias = categoriaArticuloComercioService
                .obtenerCategoriasComercio(comercioId);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{comercioId}/articulos")
    public ResponseEntity<List<ArticuloComercioResponseDTO>> obtenerArticulosComercio(
            @PathVariable Long comercioId,
            @RequestParam(required = false) Long categoriaId) {
        List<ArticuloComercioResponseDTO> articulos;
        if (categoriaId != null) {
            articulos = articuloComercioService.obtenerArticulosPorCategoria(comercioId, categoriaId);
        } else {
            articulos = articuloComercioService.obtenerArticulosComercio(comercioId);
        }
        return ResponseEntity.ok(articulos);
    }

    @GetMapping("/{comercioId}/articulos/{articuloId}")
    public ResponseEntity<ArticuloComercioResponseDTO> obtenerArticuloComercioById(
            @PathVariable Long comercioId,
            @PathVariable Long articuloId) {
        Optional<ArticuloComercioResponseDTO> articulo = articuloComercioService.obtenerArticuloPorId(articuloId);
        return articulo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{comercioId}/categorias/crear", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> crearCategoriaArticulo(
            @PathVariable Long comercioId,
            @Valid @RequestBody CategoriaArticuloComercioDTO dto) {
        categoriaArticuloComercioService.crearCategoria(comercioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{comercioId}/categorias/{categoriaId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> actualizarCategoriaArticulo(
            @PathVariable Long comercioId,
            @PathVariable Long categoriaId,
            @Valid @RequestBody CategoriaArticuloComercioDTO dto) {
        categoriaArticuloComercioService.actualizarCategoria(categoriaId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comercioId}/categorias/{categoriaId}")
    public ResponseEntity<Void> eliminarCategoriaArticulo(
            @PathVariable Long comercioId,
            @PathVariable Long categoriaId) {
        categoriaArticuloComercioService.eliminarCategoria(categoriaId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{comercioId}/articulos/{articuloId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> actualizarArticuloComercio(
            @PathVariable Long comercioId,
            @PathVariable Long articuloId,
            @Valid @ModelAttribute ArticuloComercioActualizarDTO dto) {
        articuloComercioService.actualizarArticuloComercio(articuloId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{comercioId}/articulos/{articuloId}")
    public ResponseEntity<Void> eliminarArticuloComercio(
            @PathVariable Long comercioId,
            @PathVariable Long articuloId) {
        articuloComercioService.eliminarArticuloComercio(articuloId);
        return ResponseEntity.noContent().build();
    }
}
