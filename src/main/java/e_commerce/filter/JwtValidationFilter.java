package e_commerce.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import e_commerce.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tools.jackson.databind.ObjectMapper;

/**
 * Filtro de validacion JWT que intercepta todas las peticiones HTTP entrantes antes de
 * que lleguen a los controladores REST, excepto las rutas de autenticacion publica.
 * Extiende OncePerRequestFilter, lo que garantiza que el filtro se ejecute una sola vez
 * por cada peticion, incluso si hay redirecciones internas o filtros anidados.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Metodo central del filtro que se ejecuta en cada peticion HTTP. Valida el token
     * JWT del header Authorization y decide si la peticion debe continuar hacia el
     * controlador o ser rechazada.
     *
     * @param request     Peticion HTTP entrante.
     * @param response    Respuesta HTTP saliente.
     * @param filterChain Cadena de filtros a continuar si la autenticacion es exitosa.
     * @throws ServletException Si ocurre un error en el procesamiento del filtro.
     * @throws IOException      Si ocurre un error de escritura en la respuesta.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Collections.singletonMap("error",
                            "Header Authorization es requerido con formato Bearer <token>")));
            return;
        }
        String token = authHeader.replaceFirst("Bearer ", "");
        try {
            if (jwtService.isTokenValid(token)) {
                String username = jwtService.extractUsername(token);
                String userId = jwtService.extractUserId(token);
                String rolId = jwtService.extractRolId(token);
                request.setAttribute("username", username);
                request.setAttribute("userId", userId);
                request.setAttribute("employeeId", userId);
                request.setAttribute("rolId", rolId);
                request.setAttribute("role", rolId);
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(
                        Collections.singletonMap("error", "Token invalido o expirado")));
            }
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(
                    Collections.singletonMap("error",
                            "Error validando el token: " + e.getMessage())));
        }
    }

    /**
     * Determina si el filtro NO debe aplicarse a una peticion especifica. Las rutas
     * de autenticacion publica (/auth/login, /auth/register, /auth/refresh) NO deben
     * pasar por la validacion JWT porque precisamente se usan para obtener el token.
     *
     * @param request Peticion HTTP a evaluar.
     * @return true si la peticion NO debe filtrarse (rutas publicas), false si debe
     *         pasar por la validacion JWT.
     * @throws ServletException Si ocurre un error al determinar la exclusion.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        return path.startsWith(contextPath + "/auth/login") ||
                path.startsWith(contextPath + "/auth/register") ||
                path.startsWith(contextPath + "/auth/refresh");
    }
}
