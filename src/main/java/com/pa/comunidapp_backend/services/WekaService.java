package com.pa.comunidapp_backend.services;

import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

@Service
public class WekaService {

    private Classifier modeloActividadFutura;
    private Classifier modeloPotencialVentas;
    private Instances estructuraActividadFutura;
    private Instances estructuraPotencialVentas;

    @PostConstruct
    public void inicializarModelos() {
        try {
            // Cargar modelo de actividad futura
            ClassPathResource recursoActividad = new ClassPathResource("Actividad_futura.model");
            InputStream inputStreamActividad = recursoActividad.getInputStream();
            modeloActividadFutura = (Classifier) SerializationHelper.read(inputStreamActividad);
            inputStreamActividad.close();

            // Cargar modelo de potencial de ventas
            ClassPathResource recursoPotencial = new ClassPathResource("modelo_confiabilidad.model");
            InputStream inputStreamPotencial = recursoPotencial.getInputStream();
            modeloPotencialVentas = (Classifier) SerializationHelper.read(inputStreamPotencial);
            inputStreamPotencial.close();

            // Crear estructuras de datos para las instancias
            estructuraActividadFutura = crearEstructuraActividadFutura();
            estructuraPotencialVentas = crearEstructuraPotencialVentas();

        } catch (Exception e) {
            throw new RuntimeException("Error al cargar los modelos de Weka: " + e.getMessage(), e);
        }
    }

    /**
     * Predice la actividad futura del usuario (ACTIVO o INACTIVO)
     *
     * @param diasDesdeUltimaActividad     Días desde la última actividad
     * @param articulosPublicadosUltimoMes Artículos publicados en el último mes
     * @param frecuenciaConexionSemanal    Frecuencia de conexión semanal
     * @param tasaTransaccionesCompletadas Tasa de transacciones completadas (0-1)
     * @return String "ACTIVO" o "INACTIVO"
     */
    public String predecirActividadFutura(double diasDesdeUltimaActividad,
            double articulosPublicadosUltimoMes,
            double frecuenciaConexionSemanal,
            double tasaTransaccionesCompletadas) {
        try {
            Instance instancia = new DenseInstance(5);
            instancia.setDataset(estructuraActividadFutura);
            instancia.setValue(0, diasDesdeUltimaActividad);
            instancia.setValue(1, articulosPublicadosUltimoMes);
            instancia.setValue(2, frecuenciaConexionSemanal);
            instancia.setValue(3, tasaTransaccionesCompletadas);

            double prediccion = modeloActividadFutura.classifyInstance(instancia);
            String clase = estructuraActividadFutura.classAttribute().value((int) prediccion);

            return clase;
        } catch (Exception e) {
            throw new RuntimeException("Error al predecir actividad futura: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la distribución de probabilidades para actividad futura
     */
    public double[] obtenerDistribucionActividadFutura(double diasDesdeUltimaActividad,
            double articulosPublicadosUltimoMes,
            double frecuenciaConexionSemanal,
            double tasaTransaccionesCompletadas) {
        try {
            Instance instancia = new DenseInstance(5);
            instancia.setDataset(estructuraActividadFutura);
            instancia.setValue(0, diasDesdeUltimaActividad);
            instancia.setValue(1, articulosPublicadosUltimoMes);
            instancia.setValue(2, frecuenciaConexionSemanal);
            instancia.setValue(3, tasaTransaccionesCompletadas);

            return modeloActividadFutura.distributionForInstance(instancia);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener distribución de actividad futura: " + e.getMessage(), e);
        }
    }

    /**
     * Predice el potencial de ventas del usuario (ALTO_POTENCIAL o BAJO_POTENCIAL)
     *
     * @param cantidadArticulosActivos   Cantidad de artículos activos
     * @param velocidadVentaPromedio     Velocidad de venta promedio (días)
     * @param calificacionPromedioVentas Calificación promedio de ventas (0-5)
     * @param precioPromedioArticulos    Precio promedio de artículos
     * @return String "ALTO_POTENCIAL" o "BAJO_POTENCIAL"
     */
    public String predecirPotencialVentas(double cantidadArticulosActivos,
            double velocidadVentaPromedio,
            double calificacionPromedioVentas,
            double precioPromedioArticulos) {
        try {
            Instance instancia = new DenseInstance(5);
            instancia.setDataset(estructuraPotencialVentas);
            instancia.setValue(0, cantidadArticulosActivos);
            instancia.setValue(1, velocidadVentaPromedio);
            instancia.setValue(2, calificacionPromedioVentas);
            instancia.setValue(3, precioPromedioArticulos);

            double prediccion = modeloPotencialVentas.classifyInstance(instancia);
            String clase = estructuraPotencialVentas.classAttribute().value((int) prediccion);

            return clase;
        } catch (Exception e) {
            throw new RuntimeException("Error al predecir potencial de ventas: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la distribución de probabilidades para potencial de ventas
     */
    public double[] obtenerDistribucionPotencialVentas(double cantidadArticulosActivos,
            double velocidadVentaPromedio,
            double calificacionPromedioVentas,
            double precioPromedioArticulos) {
        try {
            Instance instancia = new DenseInstance(5);
            instancia.setDataset(estructuraPotencialVentas);
            instancia.setValue(0, cantidadArticulosActivos);
            instancia.setValue(1, velocidadVentaPromedio);
            instancia.setValue(2, calificacionPromedioVentas);
            instancia.setValue(3, precioPromedioArticulos);

            return modeloPotencialVentas.distributionForInstance(instancia);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener distribución de potencial de ventas: " + e.getMessage(), e);
        }
    }

    /**
     * Crea la estructura de datos para el modelo de actividad futura
     */
    private Instances crearEstructuraActividadFutura() {
        java.util.ArrayList<Attribute> atributos = new java.util.ArrayList<>();

        atributos.add(new Attribute("dias_desde_ultima_actividad"));
        atributos.add(new Attribute("articulos_publicados_ultimo_mes"));
        atributos.add(new Attribute("frecuencia_conexion_semanal"));
        atributos.add(new Attribute("tasa_transacciones_completadas"));

        java.util.ArrayList<String> valoresClase = new java.util.ArrayList<>();
        valoresClase.add("ACTIVO");
        valoresClase.add("INACTIVO");
        atributos.add(new Attribute("actividad_futura", valoresClase));

        Instances estructura = new Instances("actividad_futura", atributos, 0);
        estructura.setClassIndex(4); // El último atributo es la clase

        return estructura;
    }

    /**
     * Crea la estructura de datos para el modelo de potencial de ventas
     */
    private Instances crearEstructuraPotencialVentas() {
        java.util.ArrayList<Attribute> atributos = new java.util.ArrayList<>();

        atributos.add(new Attribute("cantidad_articulos_activos"));
        atributos.add(new Attribute("velocidad_venta_promedio"));
        atributos.add(new Attribute("calificacion_promedio_ventas"));
        atributos.add(new Attribute("precio_promedio_articulos"));

        java.util.ArrayList<String> valoresClase = new java.util.ArrayList<>();
        valoresClase.add("ALTO_POTENCIAL");
        valoresClase.add("BAJO_POTENCIAL");
        atributos.add(new Attribute("potencial_ventas", valoresClase));

        Instances estructura = new Instances("potencial_ventas", atributos, 0);
        estructura.setClassIndex(4); // El último atributo es la clase

        return estructura;
    }
}
