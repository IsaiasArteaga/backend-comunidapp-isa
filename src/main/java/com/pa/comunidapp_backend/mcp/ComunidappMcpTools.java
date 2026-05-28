package com.pa.comunidapp_backend.mcp;

import java.util.List;
import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pa.comunidapp_backend.dto.ArticulosFiltroDTO;
import com.pa.comunidapp_backend.dto.ComercioDetalleDTO;
import com.pa.comunidapp_backend.dto.ComercioResumenDTO;
import com.pa.comunidapp_backend.response.ArticuloResponseDTO;
import com.pa.comunidapp_backend.services.ArticuloService;
import com.pa.comunidapp_backend.services.ComercioService;
import com.pa.comunidapp_backend.services.UsuarioService;

/**
 * Herramientas MCP de ComuniApp.
 *
 * CRUD 1 — Comercios: leer, leerDetalle, crear, actualizar, eliminar
 * CRUD 2 — Artículos: leer, leerDetalle, crear, actualizar, eliminar
 * Filesystem: listar archivos, info de archivo
 * Extra: estadísticas, perfil usuario
 */
@Component
public class ComunidappMcpTools {

    @Autowired private ComercioService comercioService;
    @Autowired private ArticuloService  articuloService;
    @Autowired private UsuarioService   usuarioService;

    // ══════════════════════════════════════════
    // CRUD 1 — COMERCIOS
    // ══════════════════════════════════════════

    /** LEER — lista todos los comercios con filtro opcional por categoría */
    @Tool(description = "Lee y lista los comercios disponibles en ComuniApp. "
            + "Puedes filtrar por categoría o dejar vacío para ver todos.")
    public List<ComercioResumenDTO> leerComercios(
            @ToolParam(description = "Nombre de categoría para filtrar (opcional, dejar vacío para todos)") String categoria) {
        return comercioService.buscarComerciosMcp(categoria);
    }

    /** LEER — detalle de un comercio por ID */
    @Tool(description = "Lee el detalle completo de un comercio específico por su ID, "
            + "incluyendo artículos y categorías.")
    public ComercioDetalleDTO leerDetalleComercio(
            @ToolParam(description = "ID numérico del comercio") Long comercioId) {
        return comercioService.obtenerDetalleMcp(comercioId);
    }

    /** CREAR — nuevo comercio */
    @Tool(description = "Crea un nuevo comercio en ComuniApp.")
    public String crearComercio(
            @ToolParam(description = "ID del usuario propietario") Long usuarioId,
            @ToolParam(description = "Nombre del comercio") String nombre,
            @ToolParam(description = "Descripción del comercio") String descripcion,
            @ToolParam(description = "ID de la categoría del comercio") Long categoriaId,
            @ToolParam(description = "Teléfono de contacto (opcional)") String telefono,
            @ToolParam(description = "Dirección del comercio (opcional)") String direccion) {
        return comercioService.crearComercioMcp(usuarioId, nombre, descripcion, categoriaId, telefono, direccion);
    }

    /** ACTUALIZAR — modifica datos de un comercio existente */
    @Tool(description = "Actualiza los datos de un comercio existente. "
            + "Solo se modifican los campos que se envíen con valor.")
    public String actualizarComercio(
            @ToolParam(description = "ID del comercio a actualizar") Long comercioId,
            @ToolParam(description = "ID del usuario propietario (para verificar permiso)") Long usuarioId,
            @ToolParam(description = "Nuevo nombre (opcional)") String nombre,
            @ToolParam(description = "Nueva descripción (opcional)") String descripcion,
            @ToolParam(description = "Nuevo teléfono (opcional)") String telefono,
            @ToolParam(description = "Nueva dirección (opcional)") String direccion) {
        return comercioService.actualizarComercioMcp(comercioId, usuarioId, nombre, descripcion, telefono, direccion);
    }

    /** ELIMINAR — eliminación lógica de un comercio */
    @Tool(description = "Elimina un comercio de ComuniApp (eliminación lógica).")
    public String eliminarComercio(
            @ToolParam(description = "ID del comercio a eliminar") Long comercioId,
            @ToolParam(description = "ID del usuario propietario") Long usuarioId) {
        return comercioService.eliminarComercioMcp(comercioId, usuarioId);
    }

    // ══════════════════════════════════════════
    // CRUD 2 — ARTÍCULOS
    // ══════════════════════════════════════════

    /** LEER — lista artículos con filtro opcional por categoría */
    @Tool(description = "Lee y lista los artículos publicados en ComuniApp. "
            + "Puedes filtrar por categoría o pasar 0 para ver todos.")
    public List<ArticuloResponseDTO> leerArticulos(
            @ToolParam(description = "ID de categoría para filtrar (0 para todos)") Integer categoriaId) {
        ArticulosFiltroDTO filtro = new ArticulosFiltroDTO();
        filtro.setCategoriaId(categoriaId == 0 ? null : categoriaId);
        return articuloService.buscarArticulosMcp(filtro);
    }

    /** LEER — detalle de un artículo por ID */
    @Tool(description = "Lee el detalle completo de un artículo específico por su ID.")
    public ArticuloResponseDTO leerDetalleArticulo(
            @ToolParam(description = "ID numérico del artículo") Long articuloId) {
        return articuloService.obtenerArticuloPorId(articuloId);
    }

    /** CREAR — nuevo artículo */
    @Tool(description = "Crea un nuevo artículo en ComuniApp (venta, donación o intercambio).")
    public String crearArticulo(
            @ToolParam(description = "ID del usuario que publica") Long usuarioId,
            @ToolParam(description = "Título del artículo") String titulo,
            @ToolParam(description = "Descripción del artículo") String descripcion,
            @ToolParam(description = "Precio (0 si es donación)") Double precio,
            @ToolParam(description = "Código de categoría") Integer categoriaCodigo,
            @ToolParam(description = "Tipo: 1=Venta 2=Donación 3=Intercambio") Integer tipoTransaccionCodigo) {
        return articuloService.crearArticuloMcp(usuarioId, titulo, descripcion, precio, categoriaCodigo, tipoTransaccionCodigo);
    }

    /** ACTUALIZAR — modifica datos de un artículo existente */
    @Tool(description = "Actualiza los datos de un artículo existente. "
            + "Solo se modifican los campos que se envíen con valor.")
    public String actualizarArticulo(
            @ToolParam(description = "ID del artículo a actualizar") Long articuloId,
            @ToolParam(description = "ID del usuario propietario") Long usuarioId,
            @ToolParam(description = "Nuevo título (opcional)") String titulo,
            @ToolParam(description = "Nueva descripción (opcional)") String descripcion,
            @ToolParam(description = "Nuevo precio (opcional, -1 para no cambiar)") Double precio) {
        return articuloService.actualizarArticuloMcp(articuloId, usuarioId, titulo, descripcion, precio);
    }

    /** ELIMINAR — eliminación lógica de un artículo */
    @Tool(description = "Elimina un artículo de ComuniApp (eliminación lógica).")
    public String eliminarArticulo(
            @ToolParam(description = "ID del artículo a eliminar") Long articuloId,
            @ToolParam(description = "ID del usuario propietario") Long usuarioId) {
        return articuloService.eliminarArticuloMcp(articuloId, usuarioId);
    }

    // ══════════════════════════════════════════
    // FILESYSTEM
    // ══════════════════════════════════════════

    @Tool(description = "Lista los archivos almacenados en el servidor.")
    public List<String> listarArchivos(
            @ToolParam(description = "Subcarpeta a listar (dejar vacío para ver todas)") String carpeta) {
        return comercioService.listarArchivosMcp(carpeta);
    }

    @Tool(description = "Obtiene información de un archivo específico del servidor.")
    public Map<String, String> infoArchivo(
            @ToolParam(description = "Nombre del archivo") String nombreArchivo) {
        return comercioService.obtenerInfoArchivoMcp(nombreArchivo);
    }

    // ══════════════════════════════════════════
    // EXTRA
    // ══════════════════════════════════════════

    @Tool(description = "Estadísticas generales de ComuniApp: total usuarios, comercios y artículos.")
    public Map<String, Object> estadisticasGenerales() {
        return comercioService.obtenerEstadisticasGeneralesMcp();
    }

    @Tool(description = "Perfil básico de un usuario por ID.")
    public Map<String, Object> perfilUsuario(
            @ToolParam(description = "ID del usuario") Long usuarioId) {
        return usuarioService.obtenerPerfilMcp(usuarioId);
    }
}