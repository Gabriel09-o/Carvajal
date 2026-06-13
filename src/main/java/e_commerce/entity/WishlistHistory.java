package e_commerce.entity;

import java.time.LocalDateTime;

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
 * Entidad JPA que representa la tabla "wishlist_history" en la base de datos MySQL. Esta
 * entidad funciona como un registro de auditoría (audit log) para todas las operaciones
 * realizadas sobre las listas de deseos del sistema.
 */
@Data
@Entity
@Table(name = "wishlist_history")
public class WishlistHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_wh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products products;

    private Integer cantidadInstantanea;

    private LocalDateTime fecha;

    private String tipoOperacion;

    private Long userId;
}
