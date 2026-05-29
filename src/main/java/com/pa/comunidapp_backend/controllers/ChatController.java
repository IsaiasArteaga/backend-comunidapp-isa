package com.pa.comunidapp_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pa.comunidapp_backend.config.services.GroqService;
import com.pa.comunidapp_backend.dto.ChatMensajeDTO;
import com.pa.comunidapp_backend.dto.ChatRespuestaDTO;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat")
public class ChatController {

    @Autowired
    private GroqService groqService;

    /**
     * Endpoint principal del chatbot ComuniBot.
     * Recibe el mensaje del usuario y retorna la respuesta del LLM
     * con contexto de los CRUDs y filesystem via MCP.
     */
    @PostMapping("/mensaje")
    public ResponseEntity<ChatRespuestaDTO> enviarMensaje(@RequestBody ChatMensajeDTO request) {
        ChatRespuestaDTO respuesta = groqService.procesarMensaje(
                request.getMensaje(),
                request.getUsuarioId());
        return ResponseEntity.ok(respuesta);
    }
}