package e_commerce.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Componente de Spring (@Component) que actúa como fachada para acceder a la información
 * del usuario autenticado durante el procesamiento de una petición HTTP.
 */
@Component
public class SecurityContext {

    /**
     * Obtiene el rol del usuario autenticado desde los atributos de la petición HTTP
     * actual. Internamente consulta RequestContextHolder.currentRequestAttributes() para
     * acceder al ThreadLocal que almacena los atributos de la petición en curso, y
     * recupera el valor del atributo "role" que fue establecido por JwtValidationFilter
     * durante la validación del token JWT.
     * 
     * @return Nombre del rol como String ("administrator" o "cliente"), o null si no
     *         hay un usuario autenticado en la petición actual.
     */
    public String getCurrentRole() {
        Object role = RequestContextHolder.currentRequestAttributes()
                .getAttribute("role", RequestAttributes.SCOPE_REQUEST);
        if (role != null) { return role.toString(); }
        else { return null; }
    }

    /**
     * Obtiene el ID del empleado (usuario) autenticado desde los atributos de la petición
     * HTTP actual.
     * 
     * @return ID del usuario autenticado como Long, o null si no hay un usuario
     *         autenticado en la petición actual.
     */
    public Long getCurrentEmployeeId() {
        Object employeeId = RequestContextHolder.currentRequestAttributes()
                .getAttribute("employeeId", RequestAttributes.SCOPE_REQUEST);
        if (employeeId != null) { return Long.valueOf(employeeId.toString()); }
        else { return null; }
    }
}
