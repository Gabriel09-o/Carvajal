package e_commerce.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la solicitud de inicio de sesión enviada
 * por el cliente al endpoint POST /auth/login.
 */
@Data
public class LoginRequestDTO {

    private String username;

    private String password;
}
