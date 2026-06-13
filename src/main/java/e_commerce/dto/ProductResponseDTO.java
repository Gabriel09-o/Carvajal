package e_commerce.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la respuesta con los datos de un producto,
 * utilizado tanto en la consulta de todos los productos (GET /products) como en las
 * respuestas de las operaciones de listas de deseos (crear, actualizar, consultar).
 */
@Data
public class ProductResponseDTO {

    private Long id_p;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private Integer cantidadDeseada;

    private String mensaje;
}
