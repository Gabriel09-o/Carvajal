package e_commerce.service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio central para la gestión de tokens JWT (JSON Web Tokens) en la aplicación, anotado con
 * @Service para que Spring lo detecte durante el escaneo de componentes y lo registre como un bean
 * en el contexto de la aplicación.
 */
@Service
public class JwtService {

    /**
     * Clave secreta codificada en Base64 que se utiliza para firmar y verificar los tokens JWT mediante
     * el algoritmo HMAC-SHA. Se inyecta desde la propiedad "security.jwt.secret-key" del archivo
     * application.yaml usando @Value, lo que permite cambiar la clave sin recompilar la aplicación.
     */
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Tiempo de expiración del token JWT en milisegundos, inyectado desde la propiedad
     * "security.jwt.token-expiration" del application.yaml. Actualmente configurado en 600000 ms
     * (10 minutos).
     */
    @Value("${security.jwt.token-expiration}")
    private Long tokenExpiration;

    /**
     * Genera una clave secreta HMAC-SHA a partir de la cadena Base64 almacenada en secretKey.
     * Internamente, Decoders.BASE64.decode() convierte la cadena Base64 en un arreglo de bytes, y
     * Keys.hmacShaKeyFor() crea una clave HMAC-SHA adecuada a partir de esos bytes.
     *
     * @return SecretKey lista para usar en operaciones de firma y verificación JWT.
     */
    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un nuevo token JWT firmado con claims personalizados y el subject.
     *
     * @param userId   Identificador único del usuario en la base de datos, convertido a String.
     * @param rolId    Nombre del rol del usuario ("ADMINISTRATOR" o "JUGADOR").
     * @param userName Nombre de usuario que será el subject (sub) del token.
     * @return Token JWT firmado como String compacto.
     */
    public String generateToken(Long userId, String rolId, String userName) {
        return Jwts.builder()
                .claims(Map.of("userId", userId.toString(), "rolId", rolId))
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSigninKey())
                .compact();
    }

    /**
     * Valida si un token JWT es válido verificando su firma y su fecha de expiración.
     *
     * @param token Token JWT a validar.
     * @return true si el token tiene firma válida y no ha expirado, false en caso contrario.
     */
    public Boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigninKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            System.err.println("Token is invalid: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Ocurrio un error inesperado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método genérico que extrae un valor específico del payload (claims) de un token JWT aplicando
     * una función resolver. Parsea el token, obtiene el objeto Claims del payload, y luego aplica la
     * función resolver para transformar los Claims en el tipo de retorno deseado.
     *
     * @param <T>      Tipo de dato del valor a extraer.
     * @param token    Token JWT del cual extraer los claims.
     * @param resolver Función que recibe los Claims y retorna el valor deseado del tipo T.
     * @return Valor extraído de los claims según el resolver proporcionado.
     */
    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    /**
     * Extrae el nombre de usuario (subject) del token JWT. El subject es un claim reservado y
     * estandarizado en RFC 7519 que identifica al principal del token.
     *
     * @param token Token JWT del cual extraer el username.
     * @return Nombre de usuario almacenado en el subject del token.
     */
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Extrae el ID del usuario (su clave primaria en la base de datos) desde el claim personalizado
     * "userId" del token.
     *
     * @param token Token JWT del cual extraer el userId.
     * @return ID del usuario como String.
     */
    public String extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", String.class));
    }

    /**
     * Extrae el nombre del rol del usuario desde el claim personalizado "rolId" del token.
     *
     * @param token Token JWT del cual extraer el rolId.
     * @return Nombre del rol como String ("ADMINISTRATOR" o "JUGADOR").
     */
    public String extractRolId(String token) {
        return extractClaims(token, claims -> claims.get("rolId", String.class));
    }

    /**
     * Alias de extractRolId() que proporciona un nombre de método semánticamente más claro para su
     * uso en AuthService.refreshToken().
     *
     * @param token Token JWT del cual extraer el role.
     * @return Nombre del rol como String ("ADMINISTRATOR" o "JUGADOR").
     */
    public String extractRole(String token) {
        return extractClaims(token, claims -> claims.get("rolId", String.class));
    }

    /**
     * Método que actualmente funciona como alias de extractUsername(), pero que está diseñado para
     * futuras extensiones donde se agregue un claim de email al token.
     *
     * @param token Token JWT del cual extraer el email.
     * @return Actualmente el username (subject del token).
     */
    public String extractEmail(String token) {
        return extractUsername(token);
    }

    /**
     * Refresca un token JWT generando uno nuevo a partir de los claims del token existente. Parsea el
     * token actual para extraer userId, rolId y subject del payload.
     *
     * @param token Token JWT a refrescar (puede estar expirado pero no debe tener firma inválida).
     * @return Nuevo token JWT con fecha de expiración renovada.
     * @throws Exception Si el token tiene firma inválida, está malformado u ocurre un error inesperado.
     */
    public String refreshToken(String token) throws Exception {
        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(getSigninKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new Exception("Token is expired " + e.getMessage());
        } catch (JwtException e) {
            throw new Exception("Token is invalid " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Server error " + e.getMessage());
        }

        return generateToken(Long.valueOf(claims.get("userId", String.class)), claims.get("rolId", String.class), claims.getSubject());
    }
}
