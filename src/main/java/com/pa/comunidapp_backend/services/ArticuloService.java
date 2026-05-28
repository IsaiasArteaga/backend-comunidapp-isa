package com.pa.comunidapp_backend.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa.comunidapp_backend.config.services.MapperService;
import com.pa.comunidapp_backend.dto.ArticuloActualizarDTO;
import com.pa.comunidapp_backend.dto.ArticuloCrearDTO;
import com.pa.comunidapp_backend.dto.ArticulosFiltroDTO;
import com.pa.comunidapp_backend.enums.EEstadoArticulo;
import com.pa.comunidapp_backend.models.Articulo;
import com.pa.comunidapp_backend.repositories.ArticuloRepository;
import com.pa.comunidapp_backend.repositories.CategoriaRepository;
import com.pa.comunidapp_backend.repositories.CondicionArticuloRepository;
import com.pa.comunidapp_backend.repositories.EstadoArticuloRepository;
import com.pa.comunidapp_backend.repositories.TipoTransaccionRepository;
import com.pa.comunidapp_backend.repositories.UsuarioRepository;
import com.pa.comunidapp_backend.response.ArticuloResponseDTO;
import com.pa.comunidapp_backend.response.ArticuloUsuarioResponseDTO;
import com.pa.comunidapp_backend.response.UsuarioBasicoDTO;

@Service
@Transactional
public class ArticuloService {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CondicionArticuloRepository condicionArticuloRepository;

    @Autowired
    private EstadoArticuloRepository estadoArticuloRepository;

    @Autowired
    private TipoTransaccionRepository tipoTransaccionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MapperService mapperService;

    @Autowired
    private FileStorageService fileStorageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== MÉTODOS ORIGINALES ====================

    public void crearArticulo(ArticuloCrearDTO articuloCrearDTO, Long usuarioId) {
        Articulo articulo = new Articulo();
        articulo.setTitulo(articuloCrearDTO.getTitulo());
        articulo.setDescripcion(articuloCrearDTO.getDescripcion());
        articulo.setCategoriaCodigo(articuloCrearDTO.getCategoriaCodigo());
        articulo.setCondicionCodigo(articuloCrearDTO.getCondicionCodigo());
        articulo.setTipoTransaccionCodigo(articuloCrearDTO.getTipoTransaccionCodigo());
        articulo.setPrecio(articuloCrearDTO.getPrecio());
        articulo.setUsuarioId(usuarioId);
        articulo.setCreadoEn(LocalDateTime.now());
        articulo.setEstadoArticuloCodigo(EEstadoArticulo.Disponible.getCodigo());

        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            try {
                UsuarioBasicoDTO propietarioDTO = new UsuarioBasicoDTO();
                propietarioDTO.setId(usuario.getId());
                propietarioDTO.setNombre(usuario.getNombreCompleto());
                propietarioDTO.setEmail(usuario.getEmail());
                propietarioDTO.setTelefono(usuario.getTelefono());
                propietarioDTO.setDireccion(usuario.getDireccion());
                articulo.setPropietario(objectMapper.writeValueAsString(propietarioDTO));
            } catch (Exception e) {
                // Si hay error al convertir a JSON, continuar sin propietario
            }
        });

        MultipartFile[] imagenes = articuloCrearDTO.getImagenes();
        if (imagenes != null && imagenes.length > 0) {
            List<String> rutasImagenes = fileStorageService.guardarImagenes(imagenes);
            if (!rutasImagenes.isEmpty()) {
                articulo.setImagenes(String.join(",", rutasImagenes));
            }
        }

        articuloRepository.save(articulo);
    }

    public List<ArticuloResponseDTO> obtenerTodosLosArticulos() {
        List<Articulo> articulos = articuloRepository.findByEliminadoEnIsNull();
        return articulos.stream()
                .map(this::mapToArticuloResponseDTO)
                .collect(Collectors.toList());
    }

    public ArticuloResponseDTO obtenerArticuloPorId(Long id) {
        Articulo articulo = articuloRepository.findByIdAndEliminadoEnIsNull(id)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
        return mapToArticuloResponseDTO(articulo);
    }

    private ArticuloResponseDTO mapToArticuloResponseDTO(Articulo articulo) {
        ArticuloResponseDTO dto = new ArticuloResponseDTO();
        dto.setId(articulo.getId());
        dto.setTitulo(articulo.getTitulo());
        dto.setDescripcion(articulo.getDescripcion());
        dto.setPrecio(articulo.getPrecio());
        dto.setCreadoEn(articulo.getCreadoEn());

        dto.setCategoriaCodigo(articulo.getCategoriaCodigo());
        if (articulo.getCategoriaCodigo() != null) {
            categoriaRepository.findByCodigo(articulo.getCategoriaCodigo())
                .ifPresent(cat -> dto.setCategoriaNombre(cat.getNombre()));
        }
        dto.setCondicionCodigo(articulo.getCondicionCodigo());
        if (articulo.getCondicionCodigo() != null) {
            condicionArticuloRepository.findByCodigo(articulo.getCondicionCodigo())
                .ifPresent(cond -> dto.setCondicionNombre(cond.getNombre()));
        }
        dto.setEstadoArticuloCodigo(articulo.getEstadoArticuloCodigo());
        if (articulo.getEstadoArticuloCodigo() != null) {
            estadoArticuloRepository.findByCodigo(articulo.getEstadoArticuloCodigo())
                .ifPresent(est -> dto.setEstadoArticuloNombre(est.getNombre()));
        }
        dto.setTipoTransaccionCodigo(articulo.getTipoTransaccionCodigo());
        if (articulo.getTipoTransaccionCodigo() != null) {
            tipoTransaccionRepository.findByCodigo(articulo.getTipoTransaccionCodigo())
                .ifPresent(tipo -> dto.setTipoTransaccionNombre(tipo.getNombre()));
        }

        if (articulo.getImagenes() != null && !articulo.getImagenes().isEmpty()) {
            dto.setImagenes(List.of(articulo.getImagenes().split(",")));
        }

        if (articulo.getUsuarioId() != null) {
            usuarioRepository.findById(articulo.getUsuarioId()).ifPresent(usuario -> {
                UsuarioBasicoDTO propietario = new UsuarioBasicoDTO();
                propietario.setId(usuario.getId());
                propietario.setNombre(usuario.getNombreCompleto());
                propietario.setEmail(usuario.getEmail());
                propietario.setTelefono(usuario.getTelefono());
                propietario.setDireccion(usuario.getDireccion());
                dto.setPropietario(propietario);
            });
        }

        if (articulo.getSolicitante() != null && !articulo.getSolicitante().isEmpty()) {
            try {
                UsuarioBasicoDTO solicitante = objectMapper.readValue(
                        articulo.getSolicitante(), UsuarioBasicoDTO.class);
                dto.setSolicitante(solicitante);
            } catch (Exception e) {
                // Si hay error al parsear, dejar solicitante vacío
            }
        }

        return dto;
    }

    public List<ArticuloUsuarioResponseDTO> obtenerArticulosPorUsuario(Long usuarioId) {
        List<Articulo> articulos = articuloRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId);
        return articulos.stream()
                .map(this::mapToArticuloUsuarioResponseDTO)
                .collect(Collectors.toList());
    }

    private ArticuloUsuarioResponseDTO mapToArticuloUsuarioResponseDTO(Articulo articulo) {
        ArticuloUsuarioResponseDTO dto = new ArticuloUsuarioResponseDTO();
        dto.setId(articulo.getId());
        dto.setTitulo(articulo.getTitulo());
        dto.setDescripcion(articulo.getDescripcion());
        dto.setPrecio(articulo.getPrecio());
        dto.setCreadoEn(articulo.getCreadoEn());

        dto.setCategoriaCodigo(articulo.getCategoriaCodigo());
        if (articulo.getCategoriaCodigo() != null) {
            categoriaRepository.findByCodigo(articulo.getCategoriaCodigo())
                .ifPresent(cat -> dto.setCategoriaNombre(cat.getNombre()));
        }
        dto.setCondicionCodigo(articulo.getCondicionCodigo());
        if (articulo.getCondicionCodigo() != null) {
            condicionArticuloRepository.findByCodigo(articulo.getCondicionCodigo())
                .ifPresent(cond -> dto.setCondicionNombre(cond.getNombre()));
        }
        dto.setEstadoArticuloCodigo(articulo.getEstadoArticuloCodigo());
        if (articulo.getEstadoArticuloCodigo() != null) {
            estadoArticuloRepository.findByCodigo(articulo.getEstadoArticuloCodigo())
                .ifPresent(est -> dto.setEstadoArticuloNombre(est.getNombre()));
        }
        dto.setTipoTransaccionCodigo(articulo.getTipoTransaccionCodigo());
        if (articulo.getTipoTransaccionCodigo() != null) {
            tipoTransaccionRepository.findByCodigo(articulo.getTipoTransaccionCodigo())
                .ifPresent(tipo -> dto.setTipoTransaccionNombre(tipo.getNombre()));
        }

        if (articulo.getImagenes() != null && !articulo.getImagenes().isEmpty()) {
            dto.setImagenes(List.of(articulo.getImagenes().split(",")));
        }

        return dto;
    }

    public void actualizarArticulo(Long id, ArticuloActualizarDTO articuloActualizarDTO,
            Long usuarioId) {
        Articulo articulo = articuloRepository.findByIdAndUsuarioIdAndEliminadoEnIsNull(id, usuarioId)
                .orElseThrow(() -> new RuntimeException(
                        "Artículo no encontrado o no tienes permisos para modificarlo"));

        if (articuloActualizarDTO.getTitulo() != null)
            articulo.setTitulo(articuloActualizarDTO.getTitulo());
        if (articuloActualizarDTO.getDescripcion() != null)
            articulo.setDescripcion(articuloActualizarDTO.getDescripcion());
        if (articuloActualizarDTO.getCategoriaCodigo() != null)
            articulo.setCategoriaCodigo(articuloActualizarDTO.getCategoriaCodigo());
        if (articuloActualizarDTO.getCondicionCodigo() != null)
            articulo.setCondicionCodigo(articuloActualizarDTO.getCondicionCodigo());
        if (articuloActualizarDTO.getEstadoArticuloCodigo() != null)
            articulo.setEstadoArticuloCodigo(articuloActualizarDTO.getEstadoArticuloCodigo());
        if (articuloActualizarDTO.getTipoTransaccionCodigo() != null)
            articulo.setTipoTransaccionCodigo(articuloActualizarDTO.getTipoTransaccionCodigo());
        if (articuloActualizarDTO.getPrecio() != null)
            articulo.setPrecio(articuloActualizarDTO.getPrecio());

        MultipartFile[] imagenesSubidas = articuloActualizarDTO.getImagenes();
        if (imagenesSubidas != null && imagenesSubidas.length > 0) {
            if (articulo.getImagenes() != null && !articulo.getImagenes().isEmpty()) {
                fileStorageService.eliminarImagenes(articulo.getImagenes());
            }
            List<String> rutasGuardadas = fileStorageService.guardarImagenes(imagenesSubidas);
            if (!rutasGuardadas.isEmpty()) {
                articulo.setImagenes(String.join(",", rutasGuardadas));
            } else {
                articulo.setImagenes(null);
            }
        }

        articulo.setActualizadoEn(LocalDateTime.now());
        articuloRepository.save(articulo);
    }

    public void eliminarArticulo(Long id, Long usuarioId) {
        Articulo articulo = articuloRepository.findByIdAndUsuarioIdAndEliminadoEnIsNull(id, usuarioId)
                .orElseThrow(() -> new RuntimeException(
                        "Artículo no encontrado o no tienes permisos para eliminarlo"));

        articulo.setEliminadoEn(LocalDateTime.now());
        articuloRepository.save(articulo);
    }

    public void actualizarSolicitante(Long articuloId, Long solicitanteId) {
        Articulo articulo = articuloRepository.findByIdAndEliminadoEnIsNull(articuloId)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));

        usuarioRepository.findById(solicitanteId).ifPresent(usuario -> {
            try {
                UsuarioBasicoDTO solicitanteDTO = new UsuarioBasicoDTO();
                solicitanteDTO.setId(usuario.getId());
                solicitanteDTO.setNombre(usuario.getNombreCompleto());
                solicitanteDTO.setEmail(usuario.getEmail());
                solicitanteDTO.setTelefono(usuario.getTelefono());
                solicitanteDTO.setDireccion(usuario.getDireccion());
                articulo.setSolicitante(objectMapper.writeValueAsString(solicitanteDTO));
                articuloRepository.save(articulo);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar solicitante: " + e.getMessage());
            }
        });
    }

    // ==================== MCP TOOLS ====================

    public List<ArticuloResponseDTO> buscarArticulosMcp(ArticulosFiltroDTO filtro) {
        return articuloRepository.findByEliminadoEnIsNull().stream()
                .filter(a -> filtro.getCategoriaId() == null
                        || (a.getCategoriaCodigo() != null
                                && a.getCategoriaCodigo().equals(filtro.getCategoriaId())))
                .map(this::mapToArticuloResponseDTO)
                .collect(Collectors.toList());
    }

    public String crearArticuloMcp(Long usuarioId, String titulo, String descripcion,
            Double precio, Integer categoriaCodigo, Integer tipoTransaccionCodigo) {
        try {
            Articulo articulo = new Articulo();
            articulo.setTitulo(titulo);
            articulo.setDescripcion(descripcion);
            articulo.setPrecio(precio != null
                    ? BigDecimal.valueOf(precio)
                    : BigDecimal.ZERO);
            articulo.setCategoriaCodigo(categoriaCodigo);
            articulo.setTipoTransaccionCodigo(tipoTransaccionCodigo);
            articulo.setEstadoArticuloCodigo(EEstadoArticulo.Disponible.getCodigo());
            articulo.setUsuarioId(usuarioId);
            articulo.setCreadoEn(LocalDateTime.now());
            articuloRepository.save(articulo);
            return "Artículo '" + titulo + "' creado exitosamente con ID: " + articulo.getId();
        } catch (Exception e) {
            return "Error al crear artículo: " + e.getMessage();
        }
    }

    public String actualizarArticuloMcp(Long articuloId, Long usuarioId,
            String titulo, String descripcion, Double precio) {
        try {
            Articulo articulo = articuloRepository
                    .findByIdAndUsuarioIdAndEliminadoEnIsNull(articuloId, usuarioId)
                    .orElseThrow(() -> new RuntimeException("Artículo no encontrado o sin permiso"));

            if (titulo != null && !titulo.isEmpty())
                articulo.setTitulo(titulo);
            if (descripcion != null && !descripcion.isEmpty())
                articulo.setDescripcion(descripcion);
            if (precio != null && precio >= 0)
                articulo.setPrecio(BigDecimal.valueOf(precio));

            articulo.setActualizadoEn(LocalDateTime.now());
            articuloRepository.save(articulo);
            return "Artículo ID " + articuloId + " actualizado exitosamente";
        } catch (Exception e) {
            return "Error al actualizar artículo: " + e.getMessage();
        }
    }

    public String eliminarArticuloMcp(Long articuloId, Long usuarioId) {
        try {
            Articulo articulo = articuloRepository
                    .findByIdAndUsuarioIdAndEliminadoEnIsNull(articuloId, usuarioId)
                    .orElseThrow(() -> new RuntimeException("Artículo no encontrado o sin permiso"));
            articulo.setEliminadoEn(LocalDateTime.now());
            articuloRepository.save(articulo);
            return "Artículo ID " + articuloId + " eliminado exitosamente";
        } catch (Exception e) {
            return "Error al eliminar artículo: " + e.getMessage();
        }
    }
}