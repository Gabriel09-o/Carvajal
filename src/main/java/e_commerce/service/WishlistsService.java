package e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import e_commerce.config.SecurityContext;
import e_commerce.dto.MessageResponseDTO;
import e_commerce.dto.ProductResponseDTO;
import e_commerce.dto.WishlistsRequestDTO;
import e_commerce.dto.WishlistsResponseDTO;
import e_commerce.entity.Products;
import e_commerce.entity.Users;
import e_commerce.entity.WishlistHistory;
import e_commerce.entity.Wishlists;
import e_commerce.repository.ProductRepository;
import e_commerce.repository.UserRepository;
import e_commerce.repository.WishlistHistoryRepository;
import e_commerce.repository.WishlistsRepository;

/**
 * Servicio que gestiona toda la logica de negocio relacionada con las listas de deseos
 * (wishlists) de los usuarios. Anotado con @Service, Spring lo registra como un bean.
 */
@Service
@Transactional
public class WishlistsService {

    /** Repositorio para operaciones CRUD sobre listas de deseos. */
    @Autowired
    private WishlistsRepository wishlistsRepository;

    /** Repositorio para registrar el historial de operaciones sobre listas de deseos. */
    @Autowired
    private WishlistHistoryRepository wishlistHistoryRepository;

    /** Repositorio para validar la existencia y stock de productos. */
    @Autowired
    private ProductRepository productRepository;

    /** Repositorio para validar la existencia de usuarios. */
    @Autowired
    private UserRepository userRepository;

    /** Contexto de seguridad para obtener el ID del usuario autenticado. */
    @Autowired
    private SecurityContext securityContext;

    /**
     * Obtiene todas las entradas de la lista de deseos de un usuario especifico.
     * Busca en la base de datos todas las entradas Wishlists cuyo user.id coincida
     * con el userId proporcionado.
     *
     * @param userId Identificador del usuario cuyas listas de deseos se consultan.
     * @return WishlistsResponseDTO con la lista de productos y cantidades deseadas.
     * @throws RuntimeException Si no se encuentran listas de deseos para el usuario.
     */
    public WishlistsResponseDTO getWishlistsByUserId(Long userId) {
        List<Wishlists> wishlistEntries = wishlistsRepository.findByUserId(userId);

        if (wishlistEntries.isEmpty()) {
            throw new RuntimeException(
                    "No se encontraron deseos para el usuario con ID: " + userId);
        }

        List<ProductResponseDTO> productDTOs = wishlistEntries.stream().map(w -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId_p(w.getProduct().getId());
            dto.setName(w.getProduct().getName());
            dto.setDescription(w.getProduct().getDescription());
            dto.setPrice(w.getProduct().getPrice());
            dto.setStock(w.getProduct().getStock());
            dto.setCantidadDeseada(w.getCantidadDeseada());

            if (w.getProduct().getStock() == null || w.getProduct().getStock() <= 0) {
                dto.setMensaje("El producto '" + w.getProduct().getName()
                        + "' ya no se encuentra en stock");
            }

            return dto;
        }).toList();

        WishlistsResponseDTO responseDTO = new WishlistsResponseDTO();
        responseDTO.setIdwr(userId);
        responseDTO.setProductoResponseDTO(productDTOs);

        return responseDTO;
    }

    /**
     * Crea una nueva entrada en la lista de deseos para un producto especifico y el
     * usuario autenticado. Primero verifica que el producto exista en la base de datos;
     * si no, lanza una RuntimeException.
     *
     * @param productId            Identificador del producto a agregar a la lista.
     * @param wishlistsRequestDTO DTO con la cantidad deseada.
     * @return WishlistsResponseDTO con los datos de la entrada creada.
     * @throws RuntimeException Si el producto no existe, el usuario no esta autenticado,
     *         el usuario no existe en BD, o la cantidad deseada supera el stock.
     */
    @Transactional
    public WishlistsResponseDTO createWishlists(long productId,
            WishlistsRequestDTO wishlistsRequestDTO) {
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con ID: " + productId));

        Long userId = securityContext.getCurrentEmployeeId();
        if (userId == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado con ID: " + userId));

        if (wishlistsRequestDTO.getCantidadDeseada() != null
                && wishlistsRequestDTO.getCantidadDeseada() > product.getStock()) {
            throw new RuntimeException(
                    "Cantidad no disponible para el producto: " + productId);
        }

        Wishlists wishlists = new Wishlists();
        wishlists.setProduct(product);
        wishlists.setUser(user);
        if (wishlistsRequestDTO.getCantidadDeseada() != null) {
            wishlists.setCantidadDeseada(wishlistsRequestDTO.getCantidadDeseada());
        }
        Wishlists savedWishlist = wishlistsRepository.save(wishlists);

        WishlistHistory history = new WishlistHistory();
        history.setProducts(product);
        history.setCantidadInstantanea(wishlistsRequestDTO.getCantidadDeseada());
        history.setFecha(LocalDateTime.now());
        history.setTipoOperacion("CREATE");
        history.setUserId(userId);
        wishlistHistoryRepository.save(history);

        ProductResponseDTO productDTO = new ProductResponseDTO();
        productDTO.setId_p(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        productDTO.setCantidadDeseada(savedWishlist.getCantidadDeseada());

        WishlistsResponseDTO responseDTO = new WishlistsResponseDTO();
        responseDTO.setIdwr(savedWishlist.getId_w());
        responseDTO.setCantidadDeseada(savedWishlist.getCantidadDeseada());
        responseDTO.setProductoResponseDTO(List.of(productDTO));

        return responseDTO;
    }

    /**
     * Elimina una entrada de la lista de deseos por su ID. Busca la entrada en la base
     * de datos; si no existe, lanza una RuntimeException.
     *
     * @param id Identificador de la entrada en la lista de deseos a eliminar.
     * @return MessageResponseDTO con el mensaje "Lista de deseos eliminada con exito".
     * @throws RuntimeException Si no se encuentra una entrada con el ID especificado.
     */
    public MessageResponseDTO deleteWishlists(Long id) {
        Wishlists wishlists = wishlistsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Wishlist not found with id: " + id));

        WishlistHistory history = new WishlistHistory();
        history.setProducts(wishlists.getProduct());
        history.setCantidadInstantanea(wishlists.getCantidadDeseada());
        history.setFecha(LocalDateTime.now());
        history.setTipoOperacion("DELETE");
        history.setUserId(
                wishlists.getUser() != null ? wishlists.getUser().getId() : null);
        wishlistHistoryRepository.save(history);

        wishlistsRepository.delete(wishlists);
        return new MessageResponseDTO("Lista de deseos eliminada con exito");
    }

    /**
     * Actualiza la cantidad deseada de una entrada existente en la lista de deseos.
     * Busca la entrada por su ID; si no existe, lanza una RuntimeException. Obtiene
     * el producto asociado a la entrada y valida que la nueva cantidad deseada no
     * supere el stock disponible.
     *
     * @param id                  Identificador de la entrada en la lista de deseos.
     * @param wishlistsRequestDTO DTO con la nueva cantidad deseada.
     * @return WishlistsResponseDTO con los datos actualizados de la entrada.
     * @throws RuntimeException Si la entrada no existe o la cantidad supera el stock.
     */
    public WishlistsResponseDTO updateWishlists(Long id,
            WishlistsRequestDTO wishlistsRequestDTO) {
        Wishlists wishlists = wishlistsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Wishlist not found with id: " + id));

        Products product = wishlists.getProduct();

        if (wishlistsRequestDTO.getCantidadDeseada() != null
                && wishlistsRequestDTO.getCantidadDeseada() > product.getStock()) {
            throw new RuntimeException(
                    "Cantidad no disponible para el producto: " + product.getId());
        }

        wishlists.setCantidadDeseada(wishlistsRequestDTO.getCantidadDeseada());
        wishlistsRepository.save(wishlists);

        WishlistHistory history = new WishlistHistory();
        history.setProducts(product);
        history.setCantidadInstantanea(wishlistsRequestDTO.getCantidadDeseada());
        history.setFecha(LocalDateTime.now());
        history.setTipoOperacion("UPDATE");
        history.setUserId(
                wishlists.getUser() != null ? wishlists.getUser().getId() : null);
        wishlistHistoryRepository.save(history);

        ProductResponseDTO productDTO = new ProductResponseDTO();
        productDTO.setId_p(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        productDTO.setCantidadDeseada(wishlists.getCantidadDeseada());

        WishlistsResponseDTO responseDTO = new WishlistsResponseDTO();
        responseDTO.setIdwr(wishlists.getId_w());
        responseDTO.setCantidadDeseada(wishlists.getCantidadDeseada());
        responseDTO.setProductoResponseDTO(List.of(productDTO));

        return responseDTO;
    }
}
