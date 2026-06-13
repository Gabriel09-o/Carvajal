package e_commerce.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la solicitud de creación de un nuevo usuario
 * enviada por un administrador al endpoint POST /users.
 */
@Data
public class UserRequestDTO {

    private String username;

    private String password;

    private String rol;
}
