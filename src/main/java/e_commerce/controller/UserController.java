package e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e_commerce.dto.MessageResponseDTO;
import e_commerce.dto.UserRequestDTO;
import e_commerce.exception.SecurityAuthorizationException;
import e_commerce.service.UserService;
import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints administrativos para la gestion de usuarios
 * bajo la ruta base "/users".
 */
@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * Servicio de usuarios inyectado por Spring que contiene la logica de negocio
     * para crear usuarios, incluyendo la validacion de rol de administrador.
     */
    @Autowired
    private UserService employeesService;

    /**
     * Endpoint POST /users para crear un nuevo usuario en el sistema. Solo los
     * administradores autenticados pueden ejecutar esta operacion.
     *
     * @param userRequestDTO DTO con los datos del nuevo usuario (username, password, rol).
     * @return ResponseEntity con MessageResponseDTO y HTTP 201 si se creo el usuario,
     *         HTTP 403 si no es administrador, o HTTP 400 si hay errores de validacion.
     * @throws SecurityAuthorizationException Si el usuario autenticado no tiene permisos
     *         de administrador (relanzada para que GlobalExceptionHandler la maneje).
     */
    @PostMapping()
    public ResponseEntity<MessageResponseDTO> createUsers(
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            MessageResponseDTO response =
                    employeesService.createUser(userRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SecurityAuthorizationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(e.getMessage()));
        }
    }
}
