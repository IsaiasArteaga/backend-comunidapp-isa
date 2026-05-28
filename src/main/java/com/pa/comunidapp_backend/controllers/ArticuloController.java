package com.pa.comunidapp_backend.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pa.comunidapp_backend.dto.ArticuloActualizarDTO;
import com.pa.comunidapp_backend.dto.ArticuloCrearDTO;
import com.pa.comunidapp_backend.response.ArticuloResponseDTO;
import com.pa.comunidapp_backend.response.ArticuloUsuarioResponseDTO;
import com.pa.comunidapp_backend.services.ArticuloService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/articulo")
@Tag(name = "Articulo")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @PostMapping(value = "/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> crearArticulo(
            @Valid @ModelAttribute ArticuloCrearDTO articuloCrearDTO,
            @RequestParam Long usuarioId) {

        articuloService.crearArticulo(articuloCrearDTO, usuarioId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ArticuloResponseDTO>> obtenerTodosLosArticulos() {
        List<ArticuloResponseDTO> articulos = articuloService.obtenerTodosLosArticulos();
        return new ResponseEntity<>(articulos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticuloResponseDTO> obtenerArticuloById(@PathVariable Long id) {
        ArticuloResponseDTO articulo = articuloService.obtenerArticuloPorId(id);
        return new ResponseEntity<>(articulo, HttpStatus.OK);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ArticuloUsuarioResponseDTO>> obtenerArticulosPorUsuario(@PathVariable Long usuarioId) {
        List<ArticuloUsuarioResponseDTO> articulos = articuloService.obtenerArticulosPorUsuario(usuarioId);
        return new ResponseEntity<>(articulos, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> actualizarArticulo(
            @PathVariable Long id,
            @Valid @ModelAttribute ArticuloActualizarDTO articuloActualizarDTO,
            @RequestParam Long usuarioId) {

        articuloService.actualizarArticulo(id, articuloActualizarDTO, usuarioId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarArticulo(@PathVariable Long id, @RequestParam Long usuarioId) {

        articuloService.eliminarArticulo(id, usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}
