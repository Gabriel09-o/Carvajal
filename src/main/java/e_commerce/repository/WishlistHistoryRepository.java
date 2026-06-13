package e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import e_commerce.entity.WishlistHistory;

/**
 * Repositorio JPA para la entidad WishlistHistory que proporciona operaciones CRUD
 * estandar heredadas de JpaRepository<WishlistHistory, Long>.
 */
public interface WishlistHistoryRepository extends JpaRepository<WishlistHistory, Long> {
}
