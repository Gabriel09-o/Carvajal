package e_commerce.dto;

import java.time.LocalDate;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la solicitud de registro de un nuevo empleado
 * (usuario) enviada al endpoint POST /auth/register.
 */
@Data
public class RegisterRequestDTO {

    private String username;

    private String password;

    private String rol;

    private LocalDate hireDate;
}
