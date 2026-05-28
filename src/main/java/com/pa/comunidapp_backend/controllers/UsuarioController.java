package com.pa.comunidapp_backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pa.comunidapp_backend.dto.CambiarPasswordDTO;
import com.pa.comunidapp_backend.dto.UsuarioActualizarDTO;
import com.pa.comunidapp_backend.models.Usuario;
import com.pa.comunidapp_backend.services.UsuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Usuario")
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getUsuarios();
    }

    @GetMapping("/admin/todos")
    public List<Usuario> getAllUsuariosAdmin() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    @GetMapping("/{id}")
    public Optional<Usuario> getUsuarioById(@PathVariable Long id) {
        return usuarioService.getUsuarioById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizarDatosPersonales(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioActualizarDTO usuarioActualizarDTO) {
        usuarioService.actualizarDatosPersonales(id, usuarioActualizarDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cambiar-contrasena/{id}")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @RequestBody CambiarPasswordDTO cambiarPasswordDTO) {
        usuarioService.cambiarPassword(id, cambiarPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/suspender")
    public ResponseEntity<Void> suspenderUsuario(@PathVariable Long id) {
        usuarioService.suspenderUsuario(id);
        return ResponseEntity.ok().build();
    }

}
