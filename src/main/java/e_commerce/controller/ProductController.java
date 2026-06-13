package e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e_commerce.dto.MessageResponseDTO;
import e_commerce.dto.ProductRequestDTO;
import e_commerce.dto.ProductResponseDTO;
import e_commerce.service.ProductService;

/**
 * Controlador REST que expone los endpoints para la gestion de productos del catalogo
 * bajo la ruta base "/products".
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    /**
     * Servicio de productos inyectado por Spring que contiene la logica de negocio
     * para crear y consultar productos del catalogo.
     */
    @Autowired
    private ProductService productService;

    /**
     * Endpoint POST /products para crear un nuevo producto en el catalogo.
     *
     * @param productRequestDTO DTO con los datos del nuevo producto.
     * @return ResponseEntity con MessageResponseDTO y HTTP 201 si se creo el producto.
     */
    @PostMapping
    public ResponseEntity<MessageResponseDTO> createProduct(
            @RequestBody ProductRequestDTO productRequestDTO) {
        MessageResponseDTO responseDTO =
                productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Endpoint GET /products para obtener todos los productos del catalogo.
     * @return ResponseEntity con la lista de ProductResponseDTO y HTTP 200.
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
