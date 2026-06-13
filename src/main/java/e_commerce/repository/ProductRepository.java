package e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import e_commerce.entity.Products;

/**
 * Repositorio JPA para la entidad Products que proporciona operaciones CRUD estandar
 * (findAll, findById, save, delete) heredadas de JpaRepository, ademas de un metodo
 * personalizado para buscar productos por nombre. Al extender JpaRepository<Products, Long>,
 * Spring Data JPA implementa automaticamente los metodos basicos en tiempo de ejecucion
 * sin necesidad de escribir codigo SQL.
 */
public interface ProductRepository extends JpaRepository<Products, Long> {

    /**
     * Busca un producto por su nombre exacto utilizando una consulta JPQL personalizada.
     * La consulta es equivalente a "SELECT p FROM Products p WHERE p.name = :name" y
     * retorna un Optional que estara presente si existe un producto con ese nombre, o
     * vacio si no se encuentra.
     *
     * @param name Nombre exacto del producto a buscar.
     * @return Optional que contiene el producto si existe, o Optional.empty() si no.
     */
    @Query("SELECT p FROM Products p WHERE p.name = :name")
    Optional<Products> findByName(String name);
}
