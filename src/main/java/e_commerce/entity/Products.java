package e_commerce.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad JPA que representa la tabla "products" en la base de datos MySQL. Cada instancia
 * de esta clase corresponde a un producto disponible en el catálogo del sistema de comercio
 * electrónico.
 */
@Data
@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    @OneToMany(mappedBy = "product")
    private List<Wishlists> wishlistsList;
}
