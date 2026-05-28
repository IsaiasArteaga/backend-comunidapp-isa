package com.pa.comunidapp_backend.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pa.comunidapp_backend.enums.EEstadoArticulo;
import com.pa.comunidapp_backend.enums.EEstadoSolicitud;
import com.pa.comunidapp_backend.enums.ETipoSolicitud;
import com.pa.comunidapp_backend.enums.ETipoTransaccion;
import com.pa.comunidapp_backend.models.Articulo;
import com.pa.comunidapp_backend.models.Categoria;
import com.pa.comunidapp_backend.models.CondicionArticulo;
import com.pa.comunidapp_backend.models.Transaccion;
import com.pa.comunidapp_backend.models.Usuario;
import com.pa.comunidapp_backend.repositories.ArticuloRepository;
import com.pa.comunidapp_backend.repositories.CalificacionRepository;
import com.pa.comunidapp_backend.repositories.CategoriaRepository;
import com.pa.comunidapp_backend.repositories.CondicionArticuloRepository;
import com.pa.comunidapp_backend.repositories.MisGestionesRepository;
import com.pa.comunidapp_backend.repositories.UsuarioRepository;
import com.pa.comunidapp_backend.response.CategoriaTendenciaDTO;
import com.pa.comunidapp_backend.response.ComponentesScoreDTO;
import com.pa.comunidapp_backend.response.CondicionDemandaDTO;
import com.pa.comunidapp_backend.response.DatoMensualDTO;
import com.pa.comunidapp_backend.response.DatosGraficoActividadDTO;
import com.pa.comunidapp_backend.response.DatosGraficoConfiabilidadDTO;
import com.pa.comunidapp_backend.response.DatosGraficoCumplimientoDTO;
import com.pa.comunidapp_backend.response.DatosGraficoDemandaDTO;
import com.pa.comunidapp_backend.response.DatosGraficoExitoVentaDTO;
import com.pa.comunidapp_backend.response.DatosGraficoTendenciaDTO;
import com.pa.comunidapp_backend.response.ExitoVentaPorCategoriaDTO;
import com.pa.comunidapp_backend.response.ExitoVentaPorCondicionDTO;
import com.pa.comunidapp_backend.response.ModeloConfiabilidadUsuarioDTO;
import com.pa.comunidapp_backend.response.ModeloCumplimientoPrestamosDTO;
import com.pa.comunidapp_backend.response.ModeloDemandaCondicionesDTO;
import com.pa.comunidapp_backend.response.ModeloExitoVentaDTO;
import com.pa.comunidapp_backend.response.ModeloInactividadUsuarioDTO;
import com.pa.comunidapp_backend.response.ModeloTendenciaCategoriasDTO;
import com.pa.comunidapp_backend.response.ModelosGlobalesDTO;
import com.pa.comunidapp_backend.response.ModelosUsuarioDTO;
import com.pa.comunidapp_backend.response.PrediccionesResponseDTO;

@Service
@Transactional
public class PrediccionesService {

        @Autowired
        private ArticuloRepository articuloRepository;

        @Autowired
        private MisGestionesRepository misGestionesRepository;

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Autowired
        private CondicionArticuloRepository condicionArticuloRepository;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private CalificacionRepository calificacionRepository;

        @Autowired
        private WekaService wekaService;

        public PrediccionesResponseDTO obtenerPrediccionesCompletas(Long usuarioId) {
                PrediccionesResponseDTO response = new PrediccionesResponseDTO();
                response.setTimestamp(LocalDateTime.now());
                response.setModelosGlobales(obtenerModelosGlobales());
                response.setModelosUsuario(obtenerModelosUsuario(usuarioId));
                return response;
        }

        public ModelosGlobalesDTO obtenerModelosGlobales() {
                ModelosGlobalesDTO modelos = new ModelosGlobalesDTO();
                modelos.setModelo1ExitoVenta(calcularModeloExitoVenta());
                modelos.setModelo2CumplimientoPrestamos(calcularModeloCumplimientoPrestamos());
                modelos.setModelo3TendenciaCategorias(calcularModeloTendenciaCategorias());
                modelos.setModelo4DemandaCondiciones(calcularModeloDemandaCondiciones());
                return modelos;
        }

        public ModelosUsuarioDTO obtenerModelosUsuario(Long usuarioId) {
                ModelosUsuarioDTO modelos = new ModelosUsuarioDTO();
                modelos.setModelo5ConfiabilidadUsuario(calcularModeloConfiabilidadUsuario(usuarioId));
                modelos.setModelo6InactividadUsuario(calcularModeloInactividadUsuario(usuarioId));
                return modelos;
        }

        // MODELO 1: Éxito de Venta
        private ModeloExitoVentaDTO calcularModeloExitoVenta() {
                List<Articulo> articulos = articuloRepository.findByEliminadoEnIsNull();

                // Filtrar solo artículos de venta
                List<Articulo> articulosVenta = articulos.stream()
                                .filter(a -> a.getTipoTransaccionCodigo() != null &&
                                                a.getTipoTransaccionCodigo().equals(ETipoTransaccion.Venta.getCodigo()))
                                .collect(Collectors.toList());

                // Calcular por categoría
                Map<Integer, List<Articulo>> porCategoria = articulosVenta.stream()
                                .filter(a -> a.getCategoriaCodigo() != null)
                                .collect(Collectors.groupingBy(Articulo::getCategoriaCodigo));

                List<ExitoVentaPorCategoriaDTO> exitoPorCategoria = new ArrayList<>();
                for (Map.Entry<Integer, List<Articulo>> entry : porCategoria.entrySet()) {
                        Integer categoriaCodigo = entry.getKey();
                        List<Articulo> arts = entry.getValue();

                        long vendidos = arts.stream()
                                        .filter(a -> esArticuloVendido(a.getId()))
                                        .count();

                        double tasaExito = arts.size() > 0 ? (vendidos * 100.0 / arts.size()) : 0;

                        Categoria categoria = categoriaRepository.findByCodigo(categoriaCodigo).orElse(null);
                        String nombreCategoria = categoria != null ? categoria.getNombre() : "Desconocida";

                        ExitoVentaPorCategoriaDTO dto = new ExitoVentaPorCategoriaDTO();
                        dto.setCategoriaCodigo(categoriaCodigo);
                        dto.setCategoriaNombre(nombreCategoria);
                        dto.setTasaExito(tasaExito);
                        dto.setCantidadArticulos((long) arts.size());
                        dto.setCantidadVendidos(vendidos);
                        exitoPorCategoria.add(dto);
                }

                // Calcular por condición
                Map<Integer, List<Articulo>> porCondicion = articulosVenta.stream()
                                .filter(a -> a.getCondicionCodigo() != null)
                                .collect(Collectors.groupingBy(Articulo::getCondicionCodigo));

                List<ExitoVentaPorCondicionDTO> exitoPorCondicion = new ArrayList<>();
                for (Map.Entry<Integer, List<Articulo>> entry : porCondicion.entrySet()) {
                        Integer condicionCodigo = entry.getKey();
                        List<Articulo> arts = entry.getValue();

                        long vendidos = arts.stream()
                                        .filter(a -> esArticuloVendido(a.getId()))
                                        .count();

                        double tasaExito = arts.size() > 0 ? (vendidos * 100.0 / arts.size()) : 0;

                        CondicionArticulo condicion = condicionArticuloRepository.findByCodigo(condicionCodigo)
                                        .orElse(null);
                        String nombreCondicion = condicion != null ? condicion.getNombre() : "Desconocida";

                        ExitoVentaPorCondicionDTO dto = new ExitoVentaPorCondicionDTO();
                        dto.setCondicionCodigo(condicionCodigo);
                        dto.setCondicionNombre(nombreCondicion);
                        dto.setTasaExito(tasaExito);
                        dto.setCantidadArticulos((long) arts.size());
                        dto.setCantidadVendidos(vendidos);
                        exitoPorCondicion.add(dto);
                }

                // Tasa general
                long totalVendidos = articulosVenta.stream()
                                .filter(a -> esArticuloVendido(a.getId()))
                                .count();
                double tasaExitoGeneral = articulosVenta.size() > 0 ? (totalVendidos * 100.0 / articulosVenta.size())
                                : 0;

                DatosGraficoExitoVentaDTO datosGrafico = new DatosGraficoExitoVentaDTO();
                datosGrafico.setPorCategoria(exitoPorCategoria);
                datosGrafico.setPorCondicion(exitoPorCondicion);

                ModeloExitoVentaDTO modelo = new ModeloExitoVentaDTO();
                modelo.setNombre("Éxito de Venta de Artículos");
                modelo.setDescripcion("Análisis de qué artículos se venden según condición, categoría y precio");
                modelo.setTasaExitoGeneral(tasaExitoGeneral);
                modelo.setGraficoTipo("funnel");
                modelo.setDatosGrafico(datosGrafico);

                return modelo;
        }

        private boolean esArticuloVendido(Long articuloId) {
                Articulo articulo = articuloRepository.findByIdAndEliminadoEnIsNull(articuloId).orElse(null);
                if (articulo == null)
                        return false;

                // Solo considerar artículos de venta
                if (articulo.getTipoTransaccionCodigo() == null ||
                                !articulo.getTipoTransaccionCodigo().equals(ETipoTransaccion.Venta.getCodigo())) {
                        return false;
                }

                // Un artículo se considera vendido si tiene una transacción aceptada
                List<Transaccion> transacciones = misGestionesRepository
                                .findByArticuloIdAndEliminadoEnIsNull(articuloId);
                return transacciones.stream()
                                .anyMatch(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo()) ||
                                                t.getEstadoCodigo().equals(EEstadoSolicitud.Devuelto.getCodigo()));
        }

        // MODELO 2: Cumplimiento en Préstamos
        private ModeloCumplimientoPrestamosDTO calcularModeloCumplimientoPrestamos() {
                List<Transaccion> prestamos = misGestionesRepository.findAll().stream()
                                .filter(t -> t.getEliminadoEn() == null &&
                                                t.getTipoCodigo() != null &&
                                                t.getTipoCodigo().equals(ETipoSolicitud.Prestamo.getCodigo()))
                                .collect(Collectors.toList());

                long total = prestamos.size();
                long cumplidos = prestamos.stream()
                                .filter(t -> t.getEstadoCodigo().equals(EEstadoSolicitud.Devuelto.getCodigo()))
                                .count();

                long retrasados = prestamos.stream()
                                .filter(t -> {
                                        if (t.getFechaEstimadaDevolucion() != null && t.getRespondidoEn() != null) {
                                                return t.getRespondidoEn().isAfter(t.getFechaEstimadaDevolucion()) &&
                                                                t.getEstadoCodigo().equals(
                                                                                EEstadoSolicitud.Devuelto.getCodigo());
                                        }
                                        return false;
                                })
                                .count();

                double tasaCumplimiento = total > 0 ? (cumplidos * 100.0 / total) : 0;
                double tasaRetraso = total > 0 ? (retrasados * 100.0 / total) : 0;

                // Calcular retraso promedio
                double retrasoPromedio = prestamos.stream()
                                .filter(t -> t.getFechaEstimadaDevolucion() != null && t.getRespondidoEn() != null &&
                                                t.getRespondidoEn().isAfter(t.getFechaEstimadaDevolucion()) &&
                                                t.getEstadoCodigo().equals(EEstadoSolicitud.Devuelto.getCodigo()))
                                .mapToLong(t -> ChronoUnit.DAYS.between(t.getFechaEstimadaDevolucion(),
                                                t.getRespondidoEn()))
                                .average()
                                .orElse(0.0);

                String color = tasaCumplimiento >= 80 ? "#4CAF50" : tasaCumplimiento >= 60 ? "#FF9800" : "#F44336";

                DatosGraficoCumplimientoDTO datosGrafico = new DatosGraficoCumplimientoDTO();
                datosGrafico.setTasaCumplimientoPorcentaje(tasaCumplimiento);
                datosGrafico.setTasaRetrasoPorcentaje(tasaRetraso);
                datosGrafico.setPrestamosTotales(total);
                datosGrafico.setPrestamosCumplidos(cumplidos);
                datosGrafico.setPrestamosRetrasados(retrasados);
                datosGrafico.setRetrasoPromedioDias(retrasoPromedio);
                datosGrafico.setColor(color);

                ModeloCumplimientoPrestamosDTO modelo = new ModeloCumplimientoPrestamosDTO();
                modelo.setNombre("Cumplimiento en Préstamos");
                modelo.setDescripcion("Predicción de incumplimiento y retrasos en devoluciones");
                modelo.setTasaCumplimiento(tasaCumplimiento);
                modelo.setGraficoTipo("gauge");
                modelo.setDatosGrafico(datosGrafico);

                return modelo;
        }

        // MODELO 3: Tendencia por Categoría
        private ModeloTendenciaCategoriasDTO calcularModeloTendenciaCategorias() {
                List<Categoria> categorias = categoriaRepository.findByEliminadoEnIsNull();
                List<CategoriaTendenciaDTO> categoriasTendencia = new ArrayList<>();

                for (Categoria categoria : categorias) {
                        List<Articulo> articulosCategoria = articuloRepository.findByEliminadoEnIsNull().stream()
                                        .filter(a -> a.getCategoriaCodigo() != null &&
                                                        a.getCategoriaCodigo().equals(categoria.getCodigo()))
                                        .collect(Collectors.toList());

                        // Calcular datos mensuales (últimos 3 meses)
                        LocalDateTime ahora = LocalDateTime.now();
                        List<DatoMensualDTO> datosMensual = new ArrayList<>();

                        for (int i = 2; i >= 0; i--) {
                                LocalDateTime inicioMes = ahora.minusMonths(i).withDayOfMonth(1).withHour(0)
                                                .withMinute(0)
                                                .withSecond(0);
                                LocalDateTime finMes = inicioMes.plusMonths(1);

                                List<Articulo> articulosMes = articulosCategoria.stream()
                                                .filter(a -> a.getCreadoEn() != null &&
                                                                a.getCreadoEn().isAfter(inicioMes) &&
                                                                a.getCreadoEn().isBefore(finMes))
                                                .collect(Collectors.toList());

                                long vendidosMes = articulosMes.stream()
                                                .filter(a -> esArticuloVendido(a.getId()))
                                                .count();

                                double tasaVenta = articulosMes.size() > 0 ? (vendidosMes * 100.0 / articulosMes.size())
                                                : 0;

                                String[] meses = { "enero", "febrero", "marzo", "abril", "mayo", "junio",
                                                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre" };
                                String nombreMes = meses[inicioMes.getMonthValue() - 1];

                                DatoMensualDTO dato = new DatoMensualDTO();
                                dato.setMes(nombreMes);
                                dato.setTasaVenta(tasaVenta);
                                datosMensual.add(dato);
                        }

                        // Calcular tendencia
                        if (datosMensual.size() >= 2) {
                                double primera = datosMensual.get(0).getTasaVenta();
                                double ultima = datosMensual.get(datosMensual.size() - 1).getTasaVenta();
                                double variacion = primera > 0 ? ((ultima - primera) / primera) * 100 : 0;

                                String tendencia;
                                if (variacion > 5) {
                                        tendencia = "ALCISTA";
                                } else if (variacion < -5) {
                                        tendencia = "BAJISTA";
                                } else {
                                        tendencia = "ESTABLE";
                                }

                                CategoriaTendenciaDTO categoriaTendencia = new CategoriaTendenciaDTO();
                                categoriaTendencia.setCategoriaCodigo(categoria.getCodigo().intValue());
                                categoriaTendencia.setCategoriaNombre(categoria.getNombre());
                                categoriaTendencia.setTendencia(tendencia);
                                categoriaTendencia.setVariacionPorcentaje(variacion);
                                categoriaTendencia.setDatosMensual(datosMensual);
                                categoriasTendencia.add(categoriaTendencia);
                        }
                }

                DatosGraficoTendenciaDTO datosGrafico = new DatosGraficoTendenciaDTO();
                datosGrafico.setCategorias(categoriasTendencia);

                ModeloTendenciaCategoriasDTO modelo = new ModeloTendenciaCategoriasDTO();
                modelo.setNombre("Tendencia por Categoría");
                modelo.setDescripcion("Identificar categorías en crecimiento, estables o en declive");
                modelo.setGraficoTipo("line_chart");
                modelo.setDatosGrafico(datosGrafico);

                return modelo;
        }

        // MODELO 4: Demanda por Condición
        private ModeloDemandaCondicionesDTO calcularModeloDemandaCondiciones() {
                List<CondicionArticulo> condiciones = condicionArticuloRepository.findByEliminadoEnIsNull();
                List<CondicionDemandaDTO> condicionesDemanda = new ArrayList<>();

                for (CondicionArticulo condicion : condiciones) {
                        List<Articulo> articulosCondicion = articuloRepository.findByEliminadoEnIsNull().stream()
                                        .filter(a -> a.getCondicionCodigo() != null &&
                                                        a.getCondicionCodigo().equals(condicion.getCodigo()))
                                        .collect(Collectors.toList());

                        long vendidos = articulosCondicion.stream()
                                        .filter(a -> esArticuloVendido(a.getId()))
                                        .count();

                        double tasaVenta = articulosCondicion.size() > 0
                                        ? (vendidos * 100.0 / articulosCondicion.size())
                                        : 0;

                        double precioPromedio = articulosCondicion.stream()
                                        .filter(a -> a.getPrecio() != null)
                                        .mapToDouble(a -> a.getPrecio().doubleValue())
                                        .average()
                                        .orElse(0.0);

                        // Calcular días promedio de venta
                        double diasVentaPromedio = articulosCondicion.stream()
                                        .filter(a -> esArticuloVendido(a.getId()) && a.getCreadoEn() != null)
                                        .mapToDouble(a -> {
                                                List<Transaccion> trans = misGestionesRepository
                                                                .findByArticuloIdAndEliminadoEnIsNull(a.getId());
                                                LocalDateTime fechaVenta = trans.stream()
                                                                .filter(t -> t.getEstadoCodigo().equals(
                                                                                EEstadoSolicitud.Aceptada.getCodigo())
                                                                                ||
                                                                                t.getEstadoCodigo().equals(
                                                                                                EEstadoSolicitud.Devuelto
                                                                                                                .getCodigo()))
                                                                .map(Transaccion::getCreadoEn)
                                                                .min(LocalDateTime::compareTo)
                                                                .orElse(a.getCreadoEn());
                                                return ChronoUnit.DAYS.between(a.getCreadoEn(), fechaVenta);
                                        })
                                        .average()
                                        .orElse(0.0);

                        String nivelDemanda;
                        if (tasaVenta >= 70) {
                                nivelDemanda = "ALTA";
                        } else if (tasaVenta >= 40) {
                                nivelDemanda = "MEDIA";
                        } else {
                                nivelDemanda = "BAJA";
                        }

                        CondicionDemandaDTO condicionDemanda = new CondicionDemandaDTO();
                        condicionDemanda.setCondicionCodigo(condicion.getCodigo().intValue());
                        condicionDemanda.setCondicionNombre(condicion.getNombre());
                        condicionDemanda.setNivelDemanda(nivelDemanda);
                        condicionDemanda.setTasaVenta(tasaVenta);
                        condicionDemanda.setPrecioPromedio(precioPromedio);
                        condicionDemanda.setDiasVentaPromedio(diasVentaPromedio);
                        condicionesDemanda.add(condicionDemanda);
                }

                DatosGraficoDemandaDTO datosGrafico = new DatosGraficoDemandaDTO();
                datosGrafico.setCondiciones(condicionesDemanda);

                ModeloDemandaCondicionesDTO modelo = new ModeloDemandaCondicionesDTO();
                modelo.setNombre("Demanda por Condición del Artículo");
                modelo.setDescripcion("Impacto de la condición del artículo en la conversión de venta");
                modelo.setGraficoTipo("radar");
                modelo.setDatosGrafico(datosGrafico);

                return modelo;
        }

        // MODELO 5: Potencial de Ventas del Usuario (usando Weka)
        private ModeloConfiabilidadUsuarioDTO calcularModeloConfiabilidadUsuario(Long usuarioId) {
                Usuario usuario = usuarioRepository.findByIdAndEliminadoEnIsNull(usuarioId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Datos para el modelo de potencial de ventas
                // 1. Cantidad de artículos activos
                long cantidadArticulosActivos = articuloRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId)
                                .stream()
                                .filter(a -> a.getEstadoArticuloCodigo() != null &&
                                                a.getEstadoArticuloCodigo()
                                                                .equals(EEstadoArticulo.Disponible.getCodigo())
                                                &&
                                                a.getTipoTransaccionCodigo() != null &&
                                                a.getTipoTransaccionCodigo()
                                                                .equals(ETipoTransaccion.Venta.getCodigo()))
                                .count();

                // 2. Velocidad de venta promedio (días promedio para vender un artículo)
                List<Articulo> articulosVendidos = articuloRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId)
                                .stream()
                                .filter(a -> a.getTipoTransaccionCodigo() != null &&
                                                a.getTipoTransaccionCodigo()
                                                                .equals(ETipoTransaccion.Venta.getCodigo())
                                                &&
                                                esArticuloVendido(a.getId()))
                                .collect(Collectors.toList());

                double velocidadVentaPromedio = 0.0;
                if (!articulosVendidos.isEmpty()) {
                        double sumaDias = articulosVendidos.stream()
                                        .mapToDouble(a -> {
                                                if (a.getCreadoEn() == null)
                                                        return 0;
                                                List<Transaccion> trans = misGestionesRepository
                                                                .findByArticuloIdAndEliminadoEnIsNull(a.getId());
                                                LocalDateTime fechaVenta = trans.stream()
                                                                .filter(t -> t.getEstadoCodigo().equals(
                                                                                EEstadoSolicitud.Aceptada.getCodigo())
                                                                                ||
                                                                                t.getEstadoCodigo().equals(
                                                                                                EEstadoSolicitud.Devuelto
                                                                                                                .getCodigo()))
                                                                .map(Transaccion::getCreadoEn)
                                                                .min(LocalDateTime::compareTo)
                                                                .orElse(a.getCreadoEn());
                                                return ChronoUnit.DAYS.between(a.getCreadoEn(), fechaVenta);
                                        })
                                        .sum();
                        velocidadVentaPromedio = sumaDias / articulosVendidos.size();
                }

                // 3. Calificación promedio de ventas (rating promedio del usuario)
                double calificacionPromedioVentas = usuario.getRatingPromedio() != null
                                ? usuario.getRatingPromedio().doubleValue()
                                : 5.0;

                // 4. Precio promedio de artículos
                List<Articulo> articulosVenta = articuloRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId)
                                .stream()
                                .filter(a -> a.getTipoTransaccionCodigo() != null &&
                                                a.getTipoTransaccionCodigo()
                                                                .equals(ETipoTransaccion.Venta.getCodigo())
                                                &&
                                                a.getPrecio() != null)
                                .collect(Collectors.toList());

                double precioPromedioArticulos = articulosVenta.stream()
                                .mapToDouble(a -> a.getPrecio().doubleValue())
                                .average()
                                .orElse(0.0);

                // Usar modelo de Weka para predecir potencial de ventas
                String potencialVentasWeka = wekaService.predecirPotencialVentas(
                                cantidadArticulosActivos,
                                velocidadVentaPromedio,
                                calificacionPromedioVentas,
                                precioPromedioArticulos);

                // Obtener distribución de probabilidades
                double[] distribucionPotencial = wekaService.obtenerDistribucionPotencialVentas(
                                cantidadArticulosActivos,
                                velocidadVentaPromedio,
                                calificacionPromedioVentas,
                                precioPromedioArticulos);

                // Calcular confianza basada en la distribución
                double confianzaPotencial = Math.max(distribucionPotencial[0], distribucionPotencial[1]);

                // Datos de Weka + datos de entrada
                DatosGraficoConfiabilidadDTO datosGrafico = new DatosGraficoConfiabilidadDTO();
                datosGrafico.setCategoriaConfiabilidad(potencialVentasWeka);
                datosGrafico.setConfiabilidadScore((int) (confianzaPotencial * 100));
                datosGrafico.setCantidadArticulosActivos(cantidadArticulosActivos);
                datosGrafico.setVelocidadVentaPromedio(velocidadVentaPromedio);
                datosGrafico.setCalificacionPromedioVentas(calificacionPromedioVentas);
                datosGrafico.setPrecioPromedioArticulos(precioPromedioArticulos);

                ModeloConfiabilidadUsuarioDTO modelo = new ModeloConfiabilidadUsuarioDTO();
                modelo.setNombre("Potencial de Ventas del Usuario");
                modelo.setDescripcion("Predicción de potencial de ventas basada en análisis predictivo");
                modelo.setUsuarioId(usuarioId);
                modelo.setUsuarioNombre(usuario.getNombreCompleto());
                modelo.setGraficoTipo("score_card");
                modelo.setDatosGrafico(datosGrafico);

                return modelo;
        }

        private int calcularScoreConfiabilidad(double rating, long transacciones,
                        double tasaCumplimiento, long diasAntiguedad,
                        double calificaciones) {
                int score = 0;
                score += (int) ((rating / 5.0) * 30); // 30 puntos por rating
                score += Math.min(transacciones * 2, 25); // 25 puntos por transacciones (máx 12-13 transacciones)
                score += (int) ((tasaCumplimiento / 100.0) * 20); // 20 puntos por cumplimiento
                score += Math.min((int) (diasAntiguedad / 30), 15); // 15 puntos por antigüedad
                score += (int) ((calificaciones / 5.0) * 10); // 10 puntos por calificaciones
                return Math.min(score, 100);
        }

        private ComponentesScoreDTO calcularComponentesScore(double rating, long transacciones,
                        double tasaCumplimiento, long diasAntiguedad,
                        double calificaciones) {
                ComponentesScoreDTO componentes = new ComponentesScoreDTO();
                componentes.setRatingContribucion((int) ((rating / 5.0) * 30));
                componentes.setTransaccionesContribucion((int) Math.min(transacciones * 2, 25));
                componentes.setCumplimientoContribucion((int) ((tasaCumplimiento / 100.0) * 20));
                componentes.setAntiguedadContribucion((int) Math.min((diasAntiguedad / 30), 15));
                componentes.setCalificacionesContribucion((int) ((calificaciones / 5.0) * 10));
                return componentes;
        }

        private int calcularPercentil(int score) {
                // Simplificado: asumimos distribución normal
                if (score >= 90)
                        return 90;
                if (score >= 80)
                        return 75;
                if (score >= 70)
                        return 50;
                if (score >= 60)
                        return 25;
                return 10;
        }

        private String calcularTendenciaUsuario(Long usuarioId) {
                // Comparar actividad últimos 30 días vs 30 días anteriores
                LocalDateTime ahora = LocalDateTime.now();
                LocalDateTime hace30 = ahora.minusDays(30);
                LocalDateTime hace60 = ahora.minusDays(60);

                long actividad30 = misGestionesRepository.findAll().stream()
                                .filter(t -> t.getEliminadoEn() == null &&
                                                (t.getUsuarioPropietarioId().equals(usuarioId) ||
                                                                t.getUsuarioSolicitanteId().equals(usuarioId))
                                                &&
                                                t.getCreadoEn() != null &&
                                                t.getCreadoEn().isAfter(hace30) &&
                                                t.getCreadoEn().isBefore(ahora))
                                .count();

                long actividad60 = misGestionesRepository.findAll().stream()
                                .filter(t -> t.getEliminadoEn() == null &&
                                                (t.getUsuarioPropietarioId().equals(usuarioId) ||
                                                                t.getUsuarioSolicitanteId().equals(usuarioId))
                                                &&
                                                t.getCreadoEn() != null &&
                                                t.getCreadoEn().isAfter(hace60) &&
                                                t.getCreadoEn().isBefore(hace30))
                                .count();

                if (actividad30 > actividad60 * 1.1) {
                        return "CRECIENTE";
                } else if (actividad30 < actividad60 * 0.9) {
                        return "DECRECIENTE";
                } else {
                        return "ESTABLE";
                }
        }

        // MODELO 6: Predicción de Actividad
        private ModeloInactividadUsuarioDTO calcularModeloInactividadUsuario(Long usuarioId) {
                Usuario usuario = usuarioRepository.findByIdAndEliminadoEnIsNull(usuarioId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                LocalDateTime ahora = LocalDateTime.now();
                LocalDateTime hace30Dias = ahora.minusDays(30);
                LocalDateTime hace7Dias = ahora.minusDays(7);

                // Días desde última actividad
                List<Transaccion> ultimasTransacciones = misGestionesRepository.findAll().stream()
                                .filter(t -> t.getEliminadoEn() == null &&
                                                (t.getUsuarioPropietarioId().equals(usuarioId) ||
                                                                t.getUsuarioSolicitanteId().equals(usuarioId))
                                                &&
                                                t.getCreadoEn() != null)
                                .sorted(Comparator.comparing(Transaccion::getCreadoEn).reversed())
                                .limit(1)
                                .collect(Collectors.toList());

                long diasDesdeUltimaActividad = 0;
                if (!ultimasTransacciones.isEmpty()) {
                        diasDesdeUltimaActividad = ChronoUnit.DAYS.between(ultimasTransacciones.get(0).getCreadoEn(),
                                        ahora);
                } else {
                        // Si no hay transacciones, usar días desde creación del usuario
                        diasDesdeUltimaActividad = usuario.getCreadoEn() != null
                                        ? ChronoUnit.DAYS.between(usuario.getCreadoEn(), ahora)
                                        : 999;
                }

                // Artículos publicados último mes
                long articulosPublicadosUltimoMes = articuloRepository.findByUsuarioIdAndEliminadoEnIsNull(usuarioId)
                                .stream()
                                .filter(a -> a.getCreadoEn() != null && a.getCreadoEn().isAfter(hace30Dias))
                                .count();

                // Frecuencia de conexión semanal (transacciones en los últimos 7 días)
                long frecuenciaConexionSemanal = misGestionesRepository.findAll().stream()
                                .filter(t -> t.getEliminadoEn() == null &&
                                                (t.getUsuarioPropietarioId().equals(usuarioId) ||
                                                                t.getUsuarioSolicitanteId().equals(usuarioId))
                                                &&
                                                t.getCreadoEn() != null &&
                                                t.getCreadoEn().isAfter(hace7Dias))
                                .count();

                // Tasa de transacciones completadas
                List<Transaccion> todasTransacciones = misGestionesRepository.findAll().stream()
                                .filter(t -> t.getEliminadoEn() == null &&
                                                (t.getUsuarioPropietarioId().equals(usuarioId) ||
                                                                t.getUsuarioSolicitanteId().equals(usuarioId)))
                                .collect(Collectors.toList());

                long transaccionesCompletadas = todasTransacciones.stream()
                                .filter(t -> t.getEstadoCodigo() != null &&
                                                (t.getEstadoCodigo().equals(EEstadoSolicitud.Aceptada.getCodigo()) ||
                                                                t.getEstadoCodigo()
                                                                                .equals(EEstadoSolicitud.Devuelto
                                                                                                .getCodigo())))
                                .count();

                double tasaTransaccionesCompletadas = todasTransacciones.size() > 0
                                ? (transaccionesCompletadas * 1.0 / todasTransacciones.size())
                                : 0.0;

                // Usar modelo de Weka para predecir
                String prediccionWeka = wekaService.predecirActividadFutura(
                                diasDesdeUltimaActividad,
                                articulosPublicadosUltimoMes,
                                frecuenciaConexionSemanal,
                                tasaTransaccionesCompletadas);

                // Obtener distribución de probabilidades
                double[] distribucion = wekaService.obtenerDistribucionActividadFutura(
                                diasDesdeUltimaActividad,
                                articulosPublicadosUltimoMes,
                                frecuenciaConexionSemanal,
                                tasaTransaccionesCompletadas);

                // Calcular confianza basada en la distribución
                double confianza = Math.max(distribucion[0], distribucion[1]);

                // Datos de Weka + datos de entrada
                DatosGraficoActividadDTO datosGrafico = new DatosGraficoActividadDTO();
                datosGrafico.setPrediccion(prediccionWeka);
                datosGrafico.setConfianzaPrediccion(confianza);
                datosGrafico.setDiasDesdeUltimaActividad(diasDesdeUltimaActividad);
                datosGrafico.setArticulosPublicadosUltimoMes(articulosPublicadosUltimoMes);
                datosGrafico.setFrecuenciaConexionSemanal(frecuenciaConexionSemanal);
                datosGrafico.setTasaTransaccionesCompletadas(tasaTransaccionesCompletadas);

                ModeloInactividadUsuarioDTO modelo = new ModeloInactividadUsuarioDTO();
                modelo.setNombre("Predicción de Actividad");
                modelo.setDescripcion("Predicción de actividad futura basada en análisis predictivo");
                modelo.setUsuarioId(usuarioId);
                modelo.setUsuarioNombre(usuario.getNombreCompleto());
                modelo.setGraficoTipo("spark_line_heatmap");
                modelo.setDatosGrafico(datosGrafico);

                return modelo;
        }
}
