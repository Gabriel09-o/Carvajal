package e_commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO (Data Transfer Object) que encapsula la respuesta del servidor después de una
 * autenticación exitosa mediante login o refresh de token.
 */
@Data
@AllArgsConstructor
public class JwtResponseDTO {

    private String jwt;

    private String role;

    private String name;
}
