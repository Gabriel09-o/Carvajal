package e_commerce.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la respuesta con los datos de un registro
 * del historial de listas de deseos. Actualmente definido en el códigobase pero no se
 * utiliza en los servicios o controladores existentes.
 */
@Data
public class WishlistHistoryResponse {

    private Long id_wh;

    private Long productId;

    private String productName;

    private Integer cantidadInstantanea;
}
