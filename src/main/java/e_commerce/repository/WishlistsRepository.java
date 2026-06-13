package e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import e_commerce.entity.Wishlists;

/**
 * Repositorio JPA para la entidad Wishlists que proporciona operaciones CRUD estandar
 * heredadas de JpaRepository<Wishlists, Long>, ademas de metodos personalizados para
 * consultar listas de deseos por usuario y verificar la existencia de una combinacion
 * de lista de deseos y producto.
 */
public interface WishlistsRepository extends JpaRepository<Wishlists, Long> {

    /**
     * Verifica si existe una entrada en la lista de deseos con un ID especifico y que
     * ademas este asociada a un producto especifico.
     *
     * @param id        Identificador de la entrada en la lista de deseos.
     * @param productId Identificador del producto a verificar.
     * @return true si existe una entrada con ese ID y producto, false en caso contrario.
     */
    @Query("SELECT COUNT(w) > 0 FROM Wishlists w WHERE w.id_w = :id AND w.product.id = :productId")
    boolean existsByIdAndProductId(@Param("id") Long id, @Param("productId") Long productId);

    /**
     * Busca todas las entradas de lista de deseos asociadas a un usuario especifico.
     * La consulta JPQL "SELECT w FROM Wishlists w WHERE w.user.id = :userId" recupera
     * todas las filas de la tabla wishlists donde el user_id coincide con el parametro.
     *
     * @param userId Identificador del usuario cuyas listas de deseos se desean consultar.
     * @return Lista de entidades Wishlists asociadas al usuario, o lista vacia si no
     *         hay ninguna.
     */
    @Query("SELECT w FROM Wishlists w WHERE w.user.id = :userId")
    java.util.List<Wishlists> findByUserId(@Param("userId") Long userId);
}
