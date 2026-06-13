package e_commerce.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa la solicitud de creación de un nuevo producto
 * enviada por el cliente al endpoint POST /products.
 */
@Data
public class ProductRequestDTO {

    private String name;

    private String description;

    private Double price;

    private Integer stock;
}
