package e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e_commerce.dto.MessageResponseDTO;
import e_commerce.dto.WishlistsRequestDTO;
import e_commerce.dto.WishlistsResponseDTO;
import e_commerce.service.WishlistsService;

/**
 * Controlador REST que expone los endpoints para la gestion de listas de deseos
 * (wishlists) de los usuarios bajo la ruta base "/wishlists".
 */
@RestController
@RequestMapping("/wishlists")
public class WishlistsController {

    /**
     * Servicio de listas de deseos inyectado por Spring que contiene toda la logica
     * de negocio para crear, consultar, actualizar y eliminar entradas de wishlists.
     */
    @Autowired
    private WishlistsService wishlistsService;

    /**
     * Endpoint POST /wishlists/{id} para crear una nueva entrada en la lista de deseos
     * del usuario autenticado.
     *
     * @param id                   ID del producto a agregar a la lista de deseos.
     * @param wishlistsRequestDTO DTO con la cantidad deseada.
     * @return ResponseEntity con WishlistsResponseDTO y HTTP 201.
     */
    @PostMapping("/{id}")
    public ResponseEntity<WishlistsResponseDTO> createWishlists(
            @PathVariable Long id,
            @RequestBody WishlistsRequestDTO wishlistsRequestDTO) {
        WishlistsResponseDTO response =
                wishlistsService.createWishlists(id, wishlistsRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint GET /wishlists/user/{userId} para obtener todas las entradas de la lista
     * de deseos de un usuario especifico. 
     *
     * @param userId ID del usuario cuyas listas de deseos se consultan.
     * @return ResponseEntity con WishlistsResponseDTO y HTTP 200.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<WishlistsResponseDTO> getWishlistsByUser(
            @PathVariable Long userId) {
        WishlistsResponseDTO responseDTO =
                wishlistsService.getWishlistsByUserId(userId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint DELETE /wishlists/{id} para eliminar una entrada especifica de la lista
     * de deseos. El parametro {id} es el ID de la entrada en la tabla wishlists a
     * eliminar.
     *
     * @param id ID de la entrada en la lista de deseos a eliminar.
     * @return ResponseEntity con MessageResponseDTO y HTTP 200.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> deleteWishlists(
            @PathVariable Long id) {
        MessageResponseDTO responseDTO =
                wishlistsService.deleteWishlists(id);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint PUT /wishlists/{id} para actualizar la cantidad deseada de una entrada
     * existente en la lista de deseos. El parametro {id} es el ID de la entrada a
     * actualizar, y el cuerpo de la solicitud contiene la nueva cantidad deseada.
     *
     * @param id                   ID de la entrada en la lista de deseos a actualizar.
     * @param wishlistsRequestDTO DTO con la nueva cantidad deseada.
     * @return ResponseEntity con WishlistsResponseDTO y HTTP 200.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WishlistsResponseDTO> updateWishlists(
            @PathVariable long id,
            @RequestBody WishlistsRequestDTO wishlistsRequestDTO) {
        WishlistsResponseDTO responseDTO =
                wishlistsService.updateWishlists(id, wishlistsRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
