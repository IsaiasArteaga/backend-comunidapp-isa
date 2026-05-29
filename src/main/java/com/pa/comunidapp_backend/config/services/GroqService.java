package com.pa.comunidapp_backend.config.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pa.comunidapp_backend.dto.ChatRespuestaDTO;
import com.pa.comunidapp_backend.mcp.ComunidappMcpTools;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Servicio que conecta el chatbot ComuniBot con Groq LLM.
 *
 * Integra las herramientas MCP para que el LLM pueda:
 * - Leer y gestionar comercios (CRUD)
 * - Leer y gestionar artículos (CRUD)
 * - Consultar el filesystem del servidor
 * - Ver estadísticas y perfiles de usuario
 */
@Service
public class GroqService {

    @Value("${groq.api-key}")
    private String apiKey;

    @Value("${groq.api-url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    @Autowired
    private ComunidappMcpTools mcpTools;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Procesa el mensaje del usuario enriquecido con contexto MCP.
     */
    public ChatRespuestaDTO procesarMensaje(String mensaje, Long usuarioId) {
        // Construir contexto MCP con datos reales de la app
        String contextoMcp = construirContextoMcp(mensaje, usuarioId);

        // Llamar a Groq con el contexto
        return llamarGroq(mensaje, contextoMcp);
    }

    /**
     * Construye el contexto MCP según el tipo de pregunta del usuario.
     * El LLM recibirá datos reales de la BD para responder con precisión.
     */
    private String construirContextoMcp(String mensaje, Long usuarioId) {
        StringBuilder contexto = new StringBuilder();
        String mensajeLower = mensaje.toLowerCase();

        contexto.append("=== CONTEXTO DE COMUNIAPP (datos en tiempo real) ===\n\n");

        // Contexto de comercios
        if (mensajeLower.contains("comercio") || mensajeLower.contains("tienda")
                || mensajeLower.contains("negocio") || mensajeLower.contains("local")) {
            try {
                List<?> comercios = mcpTools.leerComercios("");
                contexto.append("COMERCIOS DISPONIBLES:\n");
                contexto.append(comercios.toString()).append("\n\n");
            } catch (Exception e) {
                contexto.append("No se pudo obtener comercios.\n\n");
            }
        }

        // Contexto de artículos
        if (mensajeLower.contains("artículo") || mensajeLower.contains("articulo")
                || mensajeLower.contains("producto") || mensajeLower.contains("venta")
                || mensajeLower.contains("donación") || mensajeLower.contains("donacion")
                || mensajeLower.contains("intercambio")) {
            try {
                List<?> articulos = mcpTools.leerArticulos(0);
                contexto.append("ARTÍCULOS DISPONIBLES:\n");
                contexto.append(articulos.toString()).append("\n\n");
            } catch (Exception e) {
                contexto.append("No se pudo obtener artículos.\n\n");
            }
        }

        // Contexto de estadísticas
        if (mensajeLower.contains("estadística") || mensajeLower.contains("estadistica")
                || mensajeLower.contains("total") || mensajeLower.contains("cuántos")
                || mensajeLower.contains("cuantos")) {
            try {
                Map<String, Object> stats = mcpTools.estadisticasGenerales();
                contexto.append("ESTADÍSTICAS DE LA PLATAFORMA:\n");
                contexto.append(stats.toString()).append("\n\n");
            } catch (Exception e) {
                contexto.append("No se pudo obtener estadísticas.\n\n");
            }
        }

        // Contexto de perfil de usuario
        if (mensajeLower.contains("mi perfil") || mensajeLower.contains("mi cuenta")
                || mensajeLower.contains("mis datos") && usuarioId != null) {
            try {
                Map<String, Object> perfil = mcpTools.perfilUsuario(usuarioId);
                contexto.append("PERFIL DEL USUARIO:\n");
                contexto.append(perfil.toString()).append("\n\n");
            } catch (Exception e) {
                contexto.append("No se pudo obtener perfil.\n\n");
            }
        }

        // Contexto de archivos
        if (mensajeLower.contains("archivo") || mensajeLower.contains("imagen")
                || mensajeLower.contains("foto") || mensajeLower.contains("fichero")) {
            try {
                List<String> archivos = mcpTools.listarArchivos("");
                contexto.append("ARCHIVOS EN EL SERVIDOR:\n");
                contexto.append(archivos.toString()).append("\n\n");
            } catch (Exception e) {
                contexto.append("No se pudo obtener archivos.\n\n");
            }
        }

        contexto.append("=== FIN DEL CONTEXTO ===\n");
        return contexto.toString();
    }

    /**
     * Llama a la API de Groq con el mensaje y contexto MCP.
     */
    @SuppressWarnings("unchecked")
    private ChatRespuestaDTO llamarGroq(String mensaje, String contextoMcp) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // System prompt con contexto MCP
        String systemPrompt = "Eres ComuniBot, el asistente virtual de ComuniApp, "
                + "una plataforma comunitaria para comprar, vender, donar e intercambiar artículos, "
                + "y para descubrir comercios locales. "
                + "Responde siempre en español de manera amigable y concisa. "
                + "Usa el contexto proporcionado para dar respuestas precisas y actualizadas. "
                + "Si el contexto tiene datos vacíos, indica que no hay información disponible.\n\n"
                + contextoMcp;

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", mensaje);
        messages.add(userMessage);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("max_tokens", 1000);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(apiUrl, entity, Map.class);

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, String> messageResponse = (Map<String, String>) choice.get("message");
                    String contenido = messageResponse.get("content");

                    Map<String, Object> usage = (Map<String, Object>) response.get("usage");
                    int tokensUsados = usage != null
                            ? ((Number) usage.get("total_tokens")).intValue()
                            : 0;

                    return new ChatRespuestaDTO(contenido, model, tokensUsados);
                }
            }
        } catch (Exception e) {
            return new ChatRespuestaDTO(
                    "Lo siento, tuve un problema al procesar tu mensaje. Por favor intenta de nuevo.",
                    model, 0);
        }

        return new ChatRespuestaDTO(
                "No pude generar una respuesta en este momento.",
                model, 0);
    }
}