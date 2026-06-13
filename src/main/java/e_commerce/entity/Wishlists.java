package e_commerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad JPA que representa la tabla "wishlists" en la base de datos. Esta tabla actúa
 * como una tabla de unión (join table) entre users y products, implementando una relación
 * muchos-a-muchos con atributos adicionales.
 */
@Data
@Entity
@Table(name = "wishlists")
public class Wishlists {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_w")
    private Long id_w;

    private Integer cantidadDeseada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
