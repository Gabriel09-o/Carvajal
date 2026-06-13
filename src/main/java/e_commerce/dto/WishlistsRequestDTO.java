package e_commerce.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la solicitud enviada por el cliente para
 * crear o actualizar una entrada en la lista de deseos.
 */
@Data
public class WishlistsRequestDTO {

    private Long idwr;

    private Integer cantidadDeseada;
}
