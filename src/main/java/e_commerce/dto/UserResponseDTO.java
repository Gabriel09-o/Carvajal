package e_commerce.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la respuesta con los datos de un usuario.
 * Actualmente definido en el códigobase pero no se utiliza en los servicios o controladores
 * existentes.
 */
@Data
public class UserResponseDTO {

    private Long id;

    private String username;

    private String rol;
}
