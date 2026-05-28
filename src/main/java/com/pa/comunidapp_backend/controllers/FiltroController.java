package com.pa.comunidapp_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pa.comunidapp_backend.models.CategoriaArticuloComercioFiltro;
import com.pa.comunidapp_backend.models.CategoriaComercioFiltro;
import com.pa.comunidapp_backend.models.CategoriaFiltro;
import com.pa.comunidapp_backend.models.CondicionesFiltro;
import com.pa.comunidapp_backend.models.RolFiltro;
import com.pa.comunidapp_backend.models.TipoFiltro;
import com.pa.comunidapp_backend.response.ArticuloResponseDTO;
import com.pa.comunidapp_backend.services.CategoriaArticuloComercioService;
import com.pa.comunidapp_backend.services.CategoriaComercioService;
import com.pa.comunidapp_backend.services.FiltroService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/filtros")
@Tag(name = "Filtros")
public class FiltroController {

    @Autowired
    private FiltroService filtroService;

    @Autowired
    private CategoriaComercioService categoriaComercioService;

    @Autowired
    private CategoriaArticuloComercioService categoriaArticuloComercioService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaFiltro>> obtenerCategorias() {
        List<CategoriaFiltro> categorias = filtroService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoFiltro>> obtenerTipos() {
        List<TipoFiltro> tipos = filtroService.obtenerTipos();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/condiciones")
    public ResponseEntity<List<CondicionesFiltro>> obtenerCondiciones() {
        List<CondicionesFiltro> condiciones = filtroService.obtenerCondiciones();
        return ResponseEntity.ok(condiciones);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RolFiltro>> obtenerRoles() {
        List<RolFiltro> roles = filtroService.obtenerRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/categorias-comercios")
    public ResponseEntity<List<CategoriaComercioFiltro>> obtenerCategoriasComercios() {
        List<CategoriaComercioFiltro> categorias = categoriaComercioService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/comercio/{comercioId}/categorias-articulos")
    public ResponseEntity<List<CategoriaArticuloComercioFiltro>> obtenerCategoriasArticulosPorComercio(
            @PathVariable Long comercioId) {
        List<CategoriaArticuloComercioFiltro> categorias = categoriaArticuloComercioService
                .obtenerCategoriasComercioFiltro(comercioId);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ArticuloResponseDTO>> buscarArticulos(
            @RequestParam(required = false) String nombreArticulo,
            @RequestParam(required = false) Integer categoriaCodigo,
            @RequestParam(required = false) Integer tipoTransaccionCodigo,
            @RequestParam(required = false) Integer estadoArticuloCodigo,
            @RequestParam(required = false) String nombreUsuario) {

        List<ArticuloResponseDTO> articulos = filtroService.buscarArticulos(nombreArticulo, categoriaCodigo,
                tipoTransaccionCodigo,
                estadoArticuloCodigo,
                nombreUsuario);
        return ResponseEntity.ok(articulos);
    }
}
