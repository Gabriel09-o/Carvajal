package e_commerce.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import e_commerce.config.SecurityContext;
import e_commerce.dto.MessageResponseDTO;
import e_commerce.dto.UserRequestDTO;
import e_commerce.entity.Users;
import e_commerce.enums.Role;
import e_commerce.exception.SecurityAuthorizationException;
import e_commerce.repository.UserRepository;

/**
 * Servicio que gestiona las operaciones administrativas sobre usuarios del sistema.
 * Anotado con @Service, Spring lo registra como un bean en el contexto.
 */
@Service
public class UserService {

    /** Repositorio para operaciones de persistencia de usuarios. */
    @Autowired
    private UserRepository userRepository;

    /** Codificador de contrasenas BCrypt para hash seguro. */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Contexto de seguridad para obtener el rol del usuario autenticado. */
    @Autowired
    private SecurityContext security;

    /**
     * Valida que el usuario autenticado en la peticion actual tenga el rol de
     * administrador. Obtiene el rol mediante SecurityContext.getCurrentRole() y lo
     * compara con el nombre del enum Role.administrator.
     *
     * @throws SecurityAuthorizationException Si el usuario autenticado no tiene rol
     *         de administrador.
     */
    private void validateAdminRole() {
        if (!Role.administrator.name().equals(security.getCurrentRole())) {
            throw new SecurityAuthorizationException(
                    "EL rol: '" + security.getCurrentRole() + "' no esta permitido");
        }
    }

    /**
     * Crea un nuevo usuario en el sistema con los datos proporcionados. Solo los
     * usuarios con rol de administrador pueden ejecutar esta operacion, validado por
     * validateAdminRole().
     *
     * @param request DTO con los datos del nuevo usuario: username, password y rol
     *                (el rol se ignora, siempre se asigna "cliente").
     * @return MessageResponseDTO con el mensaje "Usuario creado exitosamente".
     * @throws SecurityAuthorizationException Si el usuario autenticado no es admin.
     * @throws RuntimeException Si el nombre de usuario ya existe en la base de datos.
     */
    @Transactional
    public MessageResponseDTO createUser(UserRequestDTO request) {
        validateAdminRole();
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException(
                    "El nombre de usuario ya existe: " + request.getUsername());
        }
        Role role = Role.cliente;
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return new MessageResponseDTO("Usuario creado exitosamente");
    }
}
