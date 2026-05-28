package com.pa.comunidapp_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pa.comunidapp_backend.dto.LoginDTO;
import com.pa.comunidapp_backend.dto.RegistroDTO;
import com.pa.comunidapp_backend.response.LoginResponseDTO;
import com.pa.comunidapp_backend.services.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Autenticación")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    public AuthController() {
    }

    @PostMapping("/ingresar")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO credentials) {
        return authService.login(credentials);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> register(@RequestBody RegistroDTO user) {
        return authService.register(user);
    }

}
