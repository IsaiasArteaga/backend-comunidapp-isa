package com.pa.comunidapp_backend.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pa.comunidapp_backend.dto.ComercioCrearDTO;
import com.pa.comunidapp_backend.dto.ComercioDetalleDTO;
import com.pa.comunidapp_backend.dto.ComercioResumenDTO;
import com.pa.comunidapp_backend.models.CategoriaComercio;
import com.pa.comunidapp_backend.models.Comercio;
import com.pa.comunidapp_backend.models.Usuario;
import com.pa.comunidapp_backend.models.UsuarioPermiso;
import com.pa.comunidapp_backend.repositories.ArticuloRepository;
import com.pa.comunidapp_backend.repositories.CategoriaComercioRepository;
import com.pa.comunidapp_backend.repositories.ComercioRepository;
import com.pa.comunidapp_backend.repositories.UsuarioPermisoRepository;
import com.pa.comunidapp_backend.repositories.UsuarioRepository;

@Service
public class ComercioService {

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioPermisoRepository usuarioPermisoRepository;

    @Autowired
    private CategoriaComercioRepository categoriaComercioRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ArticuloComercioService articuloComercioService;

    @Autowired
    private CategoriaArticuloComercioService categoriaArticuloComercioService;

    @Autowired
    private ArticuloRepository articuloRepository;

    // ==================== COMERCIOS ====================

    public List<ComercioResumenDTO> obtenerTodosComercios() {
        return comercioRepository.findByEliminadoEnIsNullAndActivoTrue().stream()
                .map(comercio -> new ComercioResumenDTO(
                        comercio.getId(),
                        comercio.getNombre(),
                        comercio.getDescripcion(),
                        comercio.getDireccion(),
                        comercio.getTelefono(),
                        comercio.getEmail(),
                        comercio.getImagenes(),
                        comercio.getSitioWeb(),
                        comercio.getTieneEnvio(),
                        comercio.getCategoria().getNombre()))
                .toList();
    }

    public Optional<Comercio> obtenerComercioById(Long id) {
        return comercioRepository.findByIdAndEliminadoEnIsNull(id);
    }

    public List<Comercio> obtenerComerciosPorUsuario(Long usuarioId) {
        return comercioRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId);
    }

    public Comercio crearComercio(Long usuarioId, ComercioCrearDTO comercioDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CategoriaComercio categoria = categoriaComercioRepository.findById(comercioDTO.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        List<UsuarioPermiso> permisos = usuarioPermisoRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId);
        boolean tienePermiso = permisos.stream()
                .anyMatch(p -> "GESTIONAR_COMERCIOS".equals(p.getPermiso().getNombre()));

        if (!tienePermiso) {
            throw new RuntimeException("El usuario no tiene permiso para crear comercios");
        }

        Comercio comercio = new Comercio();
        comercio.setUsuario(usuario);
        comercio.setCategoria(categoria);
        comercio.setNombre(comercioDTO.getNombre());
        comercio.setDescripcion(comercioDTO.getDescripcion());
        comercio.setDireccion(comercioDTO.getDireccion());
        comercio.setTelefono(comercioDTO.getTelefono());
        comercio.setEmail(comercioDTO.getEmail());
        comercio.setSitioWeb(comercioDTO.getSitioWeb());
        comercio.setTieneEnvio(comercioDTO.getTieneEnvio());
        comercio.setActivo(true);
        comercio.setCreadoEn(LocalDateTime.now());
        comercio.setActualizadoEn(LocalDateTime.now());

        org.springframework.web.multipart.MultipartFile[] imagenes = comercioDTO.getImagenes();
        if (imagenes != null && imagenes.length > 0) {
            List<String> rutasImagenes = fileStorageService.guardarImagenes(imagenes);
            if (!rutasImagenes.isEmpty()) {
                comercio.setImagenes(rutasImagenes);
            }
        }

        return comercioRepository.save(comercio);
    }

    public Comercio actualizarComercio(Long comercioId, Long usuarioId, ComercioCrearDTO comercioDTO) {
        Comercio comercio = comercioRepository.findByIdAndEliminadoEnIsNull(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));

        if (!comercio.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para actualizar este comercio");
        }

        CategoriaComercio categoria = categoriaComercioRepository.findById(comercioDTO.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        comercio.setCategoria(categoria);
        comercio.setNombre(comercioDTO.getNombre());
        comercio.setDescripcion(comercioDTO.getDescripcion());
        comercio.setDireccion(comercioDTO.getDireccion());
        comercio.setTelefono(comercioDTO.getTelefono());
        comercio.setEmail(comercioDTO.getEmail());
        comercio.setSitioWeb(comercioDTO.getSitioWeb());
        comercio.setTieneEnvio(comercioDTO.getTieneEnvio());
        comercio.setActualizadoEn(LocalDateTime.now());

        org.springframework.web.multipart.MultipartFile[] imagenes = comercioDTO.getImagenes();
        if (imagenes != null && imagenes.length > 0) {
            List<String> rutasImagenes = fileStorageService.guardarImagenes(imagenes);
            if (!rutasImagenes.isEmpty()) {
                comercio.setImagenes(rutasImagenes);
            }
        }

        return comercioRepository.save(comercio);
    }

    public Optional<ComercioResumenDTO> obtenerComercioByIdDTO(Long id) {
        Optional<Comercio> comercio = comercioRepository.findByIdAndEliminadoEnIsNull(id);
        return comercio.map(c -> new ComercioResumenDTO(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.getDireccion(),
                c.getTelefono(),
                c.getEmail(),
                c.getImagenes(),
                c.getSitioWeb(),
                c.getTieneEnvio(),
                c.getCategoria().getNombre()));
    }

    public List<ComercioResumenDTO> obtenerComerciosPorUsuarioDTO(Long usuarioId) {
        return comercioRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId).stream()
                .map(c -> new ComercioResumenDTO(
                        c.getId(),
                        c.getNombre(),
                        c.getDescripcion(),
                        c.getDireccion(),
                        c.getTelefono(),
                        c.getEmail(),
                        c.getImagenes(),
                        c.getSitioWeb(),
                        c.getTieneEnvio(),
                        c.getCategoria().getNombre()))
                .toList();
    }

    public void desactivarComercio(Long comercioId, Long usuarioId) {
        Comercio comercio = comercioRepository.findByIdAndEliminadoEnIsNull(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));

        if (!comercio.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para desactivar este comercio");
        }

        comercio.setEliminadoEn(LocalDateTime.now());
        comercio.setActualizadoEn(LocalDateTime.now());
        comercioRepository.save(comercio);
    }

    public Optional<ComercioDetalleDTO> obtenerComercioByIdConArticulos(Long id) {
        Optional<Comercio> comercio = comercioRepository.findByIdAndEliminadoEnIsNull(id);
        return comercio.map(c -> new ComercioDetalleDTO(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.getDireccion(),
                c.getTelefono(),
                c.getEmail(),
                c.getImagenes(),
                c.getSitioWeb(),
                c.getTieneEnvio(),
                c.getCategoria().getId(),
                c.getCategoria().getNombre(),
                categoriaArticuloComercioService.obtenerCategoriasComercio(c.getId()),
                articuloComercioService.obtenerArticulosComercio(c.getId())));
    }

    // ==================== MCP TOOLS ====================

    public List<ComercioResumenDTO> buscarComerciosMcp(String categoria) {
        return comercioRepository.findByEliminadoEnIsNullAndActivoTrue().stream()
                .filter(c -> categoria == null || categoria.isEmpty()
                        || (c.getCategoria() != null
                                && c.getCategoria().getNombre().equalsIgnoreCase(categoria)))
                .map(c -> new ComercioResumenDTO(
                        c.getId(),
                        c.getNombre(),
                        c.getDescripcion(),
                        c.getDireccion(),
                        c.getTelefono(),
                        c.getEmail(),
                        c.getImagenes(),
                        c.getSitioWeb(),
                        c.getTieneEnvio(),
                        c.getCategoria().getNombre()))
                .collect(Collectors.toList());
    }

    public ComercioDetalleDTO obtenerDetalleMcp(Long comercioId) {
        Comercio c = comercioRepository.findByIdAndEliminadoEnIsNull(comercioId)
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado: " + comercioId));
        return new ComercioDetalleDTO(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.getDireccion(),
                c.getTelefono(),
                c.getEmail(),
                c.getImagenes(),
                c.getSitioWeb(),
                c.getTieneEnvio(),
                c.getCategoria().getId(),
                c.getCategoria().getNombre(),
                categoriaArticuloComercioService.obtenerCategoriasComercio(c.getId()),
                articuloComercioService.obtenerArticulosComercio(c.getId()));
    }

    public String crearComercioMcp(Long usuarioId, String nombre, String descripcion,
            Long categoriaId, String telefono, String direccion) {
        try {
            CategoriaComercio categoria = categoriaComercioRepository.findById(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Comercio comercio = new Comercio();
            comercio.setUsuario(usuario);
            comercio.setCategoria(categoria);
            comercio.setNombre(nombre);
            comercio.setDescripcion(descripcion);
            comercio.setTelefono(telefono);
            comercio.setDireccion(direccion);
            comercio.setActivo(true);
            comercio.setTieneEnvio(false);
            comercio.setCreadoEn(LocalDateTime.now());
            comercio.setActualizadoEn(LocalDateTime.now());
            comercioRepository.save(comercio);
            return "Comercio '" + nombre + "' creado exitosamente con ID: " + comercio.getId();
        } catch (Exception e) {
            return "Error al crear comercio: " + e.getMessage();
        }
    }

    public String actualizarComercioMcp(Long comercioId, Long usuarioId,
            String nombre, String descripcion, String telefono, String direccion) {
        try {
            Comercio comercio = comercioRepository.findByIdAndEliminadoEnIsNull(comercioId)
                    .orElseThrow(() -> new RuntimeException("Comercio no encontrado: " + comercioId));

            if (!comercio.getUsuario().getId().equals(usuarioId)) {
                return "Error: no tienes permiso para actualizar este comercio";
            }
            if (nombre != null && !nombre.isEmpty())
                comercio.setNombre(nombre);
            if (descripcion != null && !descripcion.isEmpty())
                comercio.setDescripcion(descripcion);
            if (telefono != null && !telefono.isEmpty())
                comercio.setTelefono(telefono);
            if (direccion != null && !direccion.isEmpty())
                comercio.setDireccion(direccion);

            comercio.setActualizadoEn(LocalDateTime.now());
            comercioRepository.save(comercio);
            return "Comercio ID " + comercioId + " actualizado exitosamente";
        } catch (Exception e) {
            return "Error al actualizar comercio: " + e.getMessage();
        }
    }

    public String eliminarComercioMcp(Long comercioId, Long usuarioId) {
        try {
            Comercio comercio = comercioRepository.findByIdAndEliminadoEnIsNull(comercioId)
                    .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));
            if (!comercio.getUsuario().getId().equals(usuarioId)) {
                return "Error: no tienes permiso para eliminar este comercio";
            }
            comercio.setEliminadoEn(LocalDateTime.now());
            comercio.setActualizadoEn(LocalDateTime.now());
            comercioRepository.save(comercio);
            return "Comercio ID " + comercioId + " eliminado exitosamente";
        } catch (Exception e) {
            return "Error al eliminar comercio: " + e.getMessage();
        }
    }

    public Map<String, Object> obtenerEstadisticasGeneralesMcp() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalComercios", comercioRepository.count());
        stats.put("totalArticulos",  articuloRepository.count());
        stats.put("totalUsuarios",   usuarioRepository.count());
        return stats;
    }

    public List<String> listarArchivosMcp(String carpeta) {
        try {
            String basePath = System.getProperty("user.home") + "/uploads";
            java.io.File dir = (carpeta != null && !carpeta.isEmpty())
                    ? new java.io.File(basePath + "/" + carpeta)
                    : new java.io.File(basePath);

            if (!dir.exists())
                return List.of("Carpeta no encontrada: " + dir.getAbsolutePath());

            java.io.File[] archivos = dir.listFiles();
            if (archivos == null || archivos.length == 0)
                return List.of("No hay archivos en: " + dir.getAbsolutePath());

            return Arrays.stream(archivos)
                    .map(f -> f.getName() + " (" + (f.length() / 1024) + " KB)")
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of("Error: " + e.getMessage());
        }
    }

    public Map<String, String> obtenerInfoArchivoMcp(String nombreArchivo) {
        Map<String, String> info = new HashMap<>();
        try {
            java.io.File archivo = new java.io.File(
                    System.getProperty("user.home") + "/uploads/" + nombreArchivo);
            if (!archivo.exists()) {
                info.put("error", "Archivo no encontrado: " + nombreArchivo);
                return info;
            }
            info.put("nombre",     archivo.getName());
            info.put("tamaño",     (archivo.length() / 1024) + " KB");
            info.put("ruta",       archivo.getAbsolutePath());
            info.put("modificado", new java.util.Date(archivo.lastModified()).toString());
        } catch (Exception e) {
            info.put("error", e.getMessage());
        }
        return info;
    }
}