package com.pa.comunidapp_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pa.comunidapp_backend.config.services.MapperService;
import com.pa.comunidapp_backend.dto.LoginDTO;
import com.pa.comunidapp_backend.dto.MenuDTO;
import com.pa.comunidapp_backend.dto.RegistroDTO;
import com.pa.comunidapp_backend.models.Menu;
import com.pa.comunidapp_backend.models.Rol;
import com.pa.comunidapp_backend.models.Usuario;
import com.pa.comunidapp_backend.repositories.AuthRepository;
import com.pa.comunidapp_backend.repositories.RolRepository;
import com.pa.comunidapp_backend.response.LoginResponseDTO;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private MapperService mapperService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private com.pa.comunidapp_backend.repositories.UsuarioPermisoRepository usuarioPermisoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<LoginResponseDTO> login(LoginDTO credentials) {
        // Buscar usuario por nombre de usuario
        Optional<Usuario> usuarioOpt = authRepository.findByNombreUsuarioAndEliminadoEnIsNull(
                credentials.getUsuario());

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Credenciales inválidas");
        }

        Usuario usuarioObj = usuarioOpt.get();

        // Verificar la contraseña usando BCrypt
        if (!passwordEncoder.matches(credentials.getContrasena(), usuarioObj.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Obtener permisos del usuario
        List<String> permisos = usuarioPermisoRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioObj.getId())
                .stream()
                .map(up -> up.getPermiso().getNombre())
                .collect(Collectors.toList());

        // Obtener menús del rol del usuario
        List<Menu> menus = menuService.getMenusByRolId(usuarioObj.getRol().getId());

        // Filtrar solo menús principales (sin padre) y convertir a DTO con hijos,
        // aplicando filtro de permisos
        List<MenuDTO> menusDTO = menus.stream()
                .filter(m -> m.getMenuPadre() == null)
                .filter(m -> tienePermisoParaMenu(m, permisos))
                .map(m -> convertMenuToDTO(m, permisos))
                .sorted((a, b) -> a.getOrden().compareTo(b.getOrden()))
                .collect(Collectors.toList());

        LoginResponseDTO dto = new LoginResponseDTO(
                usuarioObj.getId(),
                usuarioObj.getNombreCompleto(),
                usuarioObj.getEmail(),
                usuarioObj.getTelefono(),
                usuarioObj.getDireccion(),
                usuarioObj.getAvatarUrl(),
                usuarioObj.getRatingPromedio(),
                usuarioObj.getRol().getNombre(),
                menusDTO,
                permisos);

        return ResponseEntity.ok(dto);
    }

    private boolean tienePermisoParaMenu(Menu menu, List<String> permisosUsuario) {
        if (menu.getPermiso() == null) {
            return true;
        }
        return permisosUsuario.contains(menu.getPermiso().getNombre());
    }

    private MenuDTO convertMenuToDTO(Menu menu, List<String> permisosUsuario) {
        List<MenuDTO> hijosDTO = menu.getHijos().stream()
                .filter(h -> tienePermisoParaMenu(h, permisosUsuario))
                .map(h -> convertMenuToDTO(h, permisosUsuario))
                .sorted((a, b) -> a.getOrden().compareTo(b.getOrden()))
                .collect(Collectors.toList());

        return new MenuDTO(
                menu.getId(),
                menu.getNombre(),
                menu.getRuta(),
                menu.getIcono(),
                menu.getOrden(),
                menu.getPorDefecto(),
                hijosDTO);
    }

    public ResponseEntity<Void> register(RegistroDTO user) {
        Usuario usuario = mapperService.map(user, Usuario.class);

        // Encriptar la contraseña antes de guardar
        String contrasenaEncriptada = passwordEncoder.encode(user.getContrasena());
        usuario.setContrasena(contrasenaEncriptada);

        // Asignar rol "usuario" por defecto
        Rol rolUsuario = rolRepository.findByNombre("usuario")
                .orElseThrow(() -> new RuntimeException("Rol 'usuario' no encontrado en la base de datos"));
        usuario.setRol(rolUsuario);

        usuario.setCreadoEn(java.time.LocalDateTime.now());

        authRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
