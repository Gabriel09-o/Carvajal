package e_commerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

/**
 * Entidad JPA que representa la tabla "stock_alerts" en la base de datos MySQL.
 */
@Data
@Entity
public class StockAlerts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_sa;

    /**
     * Referencia al producto que ha alcanzado un nivel de stock que requiere atención.
     * Es una relación muchos-a-uno (ManyToOne) donde muchas alertas pueden referenciar
     * el mismo producto a lo largo del tiempo. La clave foránea se almacena en la columna
     * "product_id" y se carga de forma perezosa (LAZY) para evitar traer datos innecesarios
     * del producto al consultar las alertas. Cuando se implemente la funcionalidad completa,
     * este campo permitirá identificar rápidamente qué productos necesitan reposición.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products product;
}
