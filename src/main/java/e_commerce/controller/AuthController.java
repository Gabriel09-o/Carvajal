package e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e_commerce.dto.JwtResponseDTO;
import e_commerce.dto.LoginRequestDTO;
import e_commerce.dto.MessageResponseDTO;
import e_commerce.dto.RegisterRequestDTO;
import e_commerce.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST que expone los endpoints publicos de autenticacion bajo la ruta
 * base "/auth". Anotado con @RestController, lo que indica que cada metodo devuelve
 * datos directamente (serializados a JSON) en lugar de vistas.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Servicio de autenticacion inyectado por Spring que contiene la logica de negocio
     * para registro, login y refresco de tokens JWT.
     */
    @Autowired
    private AuthService authService;

    /**
     * Endpoint POST /auth/register que registra un nuevo usuario en el sistema.
     *
     * @param request DTO con los datos de registro del nuevo usuario.
     * @return ResponseEntity con MessageResponseDTO y HTTP 201 si es exitoso,
     *         o HTTP 400 si ocurre un error de validacion de negocio.
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(
            @RequestBody RegisterRequestDTO request) {
        try {
            MessageResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(e.getMessage()));
        }
    }

    /**
     * Endpoint POST /auth/login que autentica a un usuario y genera un token JWT.
     *
     * @param request DTO con las credenciales del usuario (username, password).
     * @return ResponseEntity con JwtResponseDTO y HTTP 200 si es exitoso,
     *         o HTTP 401 si las credenciales son invalidas.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(
            @RequestBody LoginRequestDTO request) {
        try {
            JwtResponseDTO response = authService.login(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Endpoint POST /auth/refresh que genera un nuevo token JWT a partir de uno
     * existente (posiblemente expirado).
     *
     * @param request HttpServletRequest para extraer el header Authorization.
     * @return ResponseEntity con JwtResponseDTO y HTTP 200 si se refresco el token,
     *         o HTTP 401 si el token es invalido o el header falta.
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String token = authHeader.replaceFirst("Bearer ", "");
        try {
            JwtResponseDTO response = authService.refreshToken(token);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
