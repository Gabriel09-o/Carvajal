package e_commerce.exception;

/**
 * Excepción personalizada que extiende RuntimeException para representar errores de
 * autorización de seguridad en la aplicación. Se lanza cuando un usuario autenticado
 * intenta realizar una operación para la cual no tiene permisos suficientes, como cuando
 * un usuario con rol "cliente" intenta crear un nuevo usuario (operación reservada para
 * administradores).
 */
public class SecurityAuthorizationException extends RuntimeException {

    /**
     * Construye una nueva excepción de autorización con el mensaje descriptivo del error.
     * El mensaje se pasa al constructor de RuntimeException y posteriormente es utilizado
     * por GlobalExceptionHandler para construir la respuesta HTTP 403 con el detalle del
     * error, permitiendo al cliente entender por qué su solicitud fue rechazada.
     *
     * @param message Descripción del error de autorización, como "EL rol: 'cliente' no
     *                esta permitido" cuando un usuario sin permisos intenta una operación
     *                restringida.
     */
    public SecurityAuthorizationException(String message) {
        super(message);
    }
}
