package com.pa.comunidapp_backend.config;

import java.util.List;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pa.comunidapp_backend.mcp.ComunidappMcpTools;

/**
 * Configura el MCP Server de ComuniApp.
 *
 * Registra las herramientas (tools) que el LLM puede invocar
 * a través del protocolo MCP.
 *
 * Arquitectura MCP:
 *   MCP Host (cliente externo / Claude Desktop / otro LLM)
 *       └──► MCP Client
 *               └──► MCP Server (este app, Spring AI)
 *                       └──► Tools: buscarComercios, buscarArticulos, etc.
 */
@Configuration
public class McpServerConfig {

    /**
     * Registra todas las herramientas MCP de ComuniApp.
     * Spring AI las expone automáticamente vía Streamable HTTP
     * en el endpoint: POST /mcp/message
     */
    @Bean
    public ToolCallbackProvider comunidappTools(ComunidappMcpTools mcpTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpTools)
                .build();
    }
}