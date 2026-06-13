package e_commerce.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import e_commerce.dto.MessageResponseDTO;

/**
 * Manejador global de excepciones para los controladores REST de la aplicación. Está
 * anotado con @RestControllerAdvice, que es una especialización de @ControllerAdvice
 * diseñada específicamente para controladores REST, combinando @ControllerAdvice y
 * @ResponseBody.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones de validación de datos lanzadas cuando un argumento anotado
     * con @Valid en un controlador no cumple con las restricciones de validación (como
     * @NotNull, @Size, @Email, etc.).
     *
     * @param ex Excepción de validación lanzada por Spring al fallar la validación @Valid.
     * @return Mapa con los nombres de los campos como claves y los mensajes de error como
     *         valores, envuelto en una respuesta HTTP 400 (BAD REQUEST).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Maneja cualquier excepción RuntimeException no capturada específicamente por otros
     * métodos de esta clase.
     *
     * @param ex Excepción RuntimeException capturada.
     * @return MessageResponseDTO con el mensaje de error, envuelto en HTTP 400.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponseDTO> handleRuntimeException(RuntimeException ex) {
        MessageResponseDTO response = new MessageResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja excepciones relacionadas con la validación de tokens JWT, como token
     * malformado, firma inválida o token expirado. Estas excepciones son lanzadas por
     * la librería JJWT cuando se intenta parsear o validar un token JWT.
     *
     * @param ex Excepción JwtException lanzada por la librería JJWT.
     * @return MessageResponseDTO con el mensaje de error, envuelto en HTTP 401.
     */
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<MessageResponseDTO> handleJwtException(io.jsonwebtoken.JwtException ex) {
        MessageResponseDTO response = new MessageResponseDTO("Token invalido: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Maneja excepciones de autorización de seguridad (SecurityAuthorizationException)
     * que se lanzan cuando un usuario no tiene permisos suficientes para realizar una
     * operación.
     *
     * @param ex Excepción SecurityAuthorizationException lanzada por servicios como
     *           UserService cuando se valida el rol del usuario autenticado.
     * @return MessageResponseDTO con el mensaje de error, envuelto en HTTP 403.
     */
    @ExceptionHandler(SecurityAuthorizationException.class)
    public ResponseEntity<MessageResponseDTO> handleSecurityException(SecurityAuthorizationException ex) {
        MessageResponseDTO response = new MessageResponseDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
