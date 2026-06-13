package e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import e_commerce.entity.Users;

/**
 * Repositorio JPA para la entidad Users que proporciona operaciones CRUD estandar
 * heredadas de JpaRepository<Users, Long>, ademas de un metodo personalizado para
 * buscar usuarios por nombre de usuario.
 */
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     * Busca un usuario por su nombre de usuario exacto. Spring Data JPA genera
     * automaticamente la consulta JPQL basandose en el nombre del metodo, que sigue
     * la convencion "findBy + NombreDelCampo".
     *
     * @param username Nombre de usuario a buscar (se normaliza a minusculas en los
     *                 servicios antes de llamar a este metodo).
     * @return Optional que contiene el usuario si existe, o Optional.empty() si no.
     */
    Optional<Users> findByUsername(String username);
}
