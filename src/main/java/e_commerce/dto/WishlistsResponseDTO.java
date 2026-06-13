package e_commerce.dto;

import java.util.List;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la respuesta del servidor para las
 * operaciones relacionadas con listas de deseos: creacion, consulta, actualizacion.
 */
@Data
public class WishlistsResponseDTO {

    private Long idwr;

    private List<ProductResponseDTO> productoResponseDTO;

    private Integer cantidadDeseada;
}
