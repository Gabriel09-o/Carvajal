package e_commerce.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import e_commerce.dto.JwtResponseDTO;
import e_commerce.dto.LoginRequestDTO;
import e_commerce.dto.MessageResponseDTO;
import e_commerce.dto.RegisterRequestDTO;
import e_commerce.entity.Users;
import e_commerce.enums.Role;
import e_commerce.repository.UserRepository;

/**
 * Servicio de autenticacion que encapsula toda la logica de negocio relacionada con el
 * registro de nuevos usuarios y el inicio de sesion en el sistema.
 */
@Service
public class AuthService {

    /** Repositorio para operaciones de persistencia de usuarios. */
    @Autowired
    private UserRepository userRepository;

    /** Codificador de contrasenas BCrypt para hash y verificacion. */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Servicio JWT para generacion y manejo de tokens de autenticacion. */
    @Autowired
    private JwtService jwtService;

    /**
     * Registra un nuevo usuario en el sistema con los datos proporcionados en la
     * solicitud.
     *
     * @param request DTO con los datos del nuevo usuario: username, password, rol y
     *                fecha de contratacion (opcional).
     * @return MessageResponseDTO con el mensaje "Empleado registrado exitosamente".
     * @throws RuntimeException Si el username ya existe, el rol es invalido o el rol
     *                          esta vacio.
     */
    public MessageResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername().toLowerCase().trim()).isPresent()) {
            throw new RuntimeException(
                    "El nombre de USUSRIO ya esta registrado: " + request.getUsername());
        }
        if (request.getRol() == null || request.getRol().isBlank()) {
            throw new RuntimeException("El rol es requerido");
        }
        Role role;
        try {
            role = Role.valueOf(request.getRol().toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Rol invalido. Debe ser: administrator o cliente");
        }
        Users users = new Users();
        users.setUsername(request.getUsername());
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        users.setRole(role);
        users.setCreatedAt(LocalDateTime.now());
        userRepository.save(users);
        return new MessageResponseDTO("Empleado registrado exitosamente");
    }

    /**
     * Autentica a un usuario verificando sus credenciales y genera un token JWT para
     * sesiones posteriores.
     *
     * @param request DTO con las credenciales del usuario: username y password.
     * @return JwtResponseDTO con el token JWT, el rol y el nombre del usuario.
     * @throws RuntimeException Si el usuario no existe o la contrasena es incorrecta.
     */
    public JwtResponseDTO login(LoginRequestDTO request) {
        Optional<Users> usersOpt = userRepository.findByUsername(request.getUsername());
        if (usersOpt.isEmpty()) {
            throw new RuntimeException(
                    "Numero de documento no registrado: " + request.getUsername());
        }
        Users users = usersOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), users.getPassword())) {
            throw new RuntimeException("Contrasena incorrecta");
        }
        String jwt = jwtService.generateToken(
                users.getId(), users.getRole().name(), users.getUsername());
        return new JwtResponseDTO(jwt, users.getRole().name(), users.getUsername());
    }

    /**
     * Refresca un token JWT generando uno nuevo a partir de un token existente. Primero,
     * utiliza JwtService.refreshToken() para generar un nuevo token con los mismos claims
     * pero nueva fecha de expiracion. Luego, extrae el rol y el email (username) del token
     * original mediante JwtService.extractRole() y extractEmail().
     *
     * @param token Token JWT actual que se desea refrescar (puede estar expirado pero
     *              debe tener firma valida).
     * @return JwtResponseDTO con el nuevo token JWT, el rol y el nombre del usuario.
     * @throws Exception Si el token es invalido, esta malformado, el usuario no existe
     *                   u ocurre un error inesperado.
     */
    public JwtResponseDTO refreshToken(String token) throws Exception {
        String newToken = jwtService.refreshToken(token);
        String role = jwtService.extractRole(token);
        String email = jwtService.extractEmail(token);
        Optional<Users> userOpt = userRepository.findByUsername(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return new JwtResponseDTO(newToken, role, userOpt.get().getUsername());
    }
}
