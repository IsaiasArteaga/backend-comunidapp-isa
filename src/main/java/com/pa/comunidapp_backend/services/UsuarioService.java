package com.pa.comunidapp_backend.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pa.comunidapp_backend.config.services.MapperService;
import com.pa.comunidapp_backend.dto.CambiarPasswordDTO;
import com.pa.comunidapp_backend.dto.UsuarioActualizarDTO;
import com.pa.comunidapp_backend.models.Usuario;
import com.pa.comunidapp_backend.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MapperService mapperService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findByEliminadoEnIsNull();
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findByIdAndEliminadoEnIsNull(id);
    }

    public void actualizarDatosPersonales(Long id, UsuarioActualizarDTO usuarioActualizarDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuarioActualizarDTO.getNombreCompleto() != null) {
            usuario.setNombreCompleto(usuarioActualizarDTO.getNombreCompleto());
        }
        if (usuarioActualizarDTO.getEmail() != null) {
            usuario.setEmail(usuarioActualizarDTO.getEmail());
        }
        if (usuarioActualizarDTO.getDireccion() != null) {
            usuario.setDireccion(usuarioActualizarDTO.getDireccion());
        }
        if (usuarioActualizarDTO.getTelefono() != null) {
            usuario.setTelefono(usuarioActualizarDTO.getTelefono());
        }

        usuario.setActualizadoEn(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    public void cambiarPassword(Long id, CambiarPasswordDTO cambiarPasswordDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(cambiarPasswordDTO.getPasswordActual(), usuario.getContrasena())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setContrasena(passwordEncoder.encode(cambiarPasswordDTO.getPasswordNuevo()));
        usuario.setActualizadoEn(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    public void suspenderUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEliminadoEn(LocalDateTime.now());
        usuario.setActualizadoEn(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    // ==================== MCP TOOLS ====================

    /**
     * MCP: Retorna el perfil básico de un usuario por ID
     */
    public Map<String, Object> obtenerPerfilMcp(Long usuarioId) {
        Usuario usuario = usuarioRepository.findByIdAndEliminadoEnIsNull(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("id",             usuario.getId());
        perfil.put("nombre",         usuario.getNombreCompleto());
        perfil.put("email",          usuario.getEmail());
        perfil.put("telefono",       usuario.getTelefono());
        perfil.put("direccion",      usuario.getDireccion());
        return perfil;
    }
}
