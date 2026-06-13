package e_commerce.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import e_commerce.dto.MessageResponseDTO;
import e_commerce.dto.ProductRequestDTO;
import e_commerce.dto.ProductResponseDTO;
import e_commerce.entity.Products;
import e_commerce.repository.ProductRepository;

/**
 * Servicio que gestiona toda la logica de negocio relacionada con los productos del
 * catalogo. Anotado con @Service, Spring lo detecta durante el escaneo de componentes
 * y lo registra como un bean.
 */
@Service
public class ProductService {

    /** Repositorio para operaciones de persistencia de productos. */
    @Autowired
    private ProductRepository productRepository;

    /**
     * Crea un nuevo producto en el catalogo a partir de los datos proporcionados en el
     * DTO de solicitud. Primero verifica que no exista un producto con el mismo nombre
     * mediante ProductRepository.findByName(); si ya existe, lanza una RuntimeException.
     *
     * @param productRequestDTO DTO con los datos del nuevo producto: name, description,
     *                          price y stock.
     * @return MessageResponseDTO con el mensaje "Producto creado con exito con el
     *         nombre: {name}".
     * @throws RuntimeException Si ya existe un producto con el mismo nombre.
     */
    public MessageResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Optional<Products> productOptional =
                productRepository.findByName(productRequestDTO.getName());
        if (productOptional.isPresent()) {
            throw new RuntimeException(
                    "El producto con este nombre ya existe: " + productRequestDTO.getName());
        }
        Products newProduct = new Products();
        newProduct.setName(productRequestDTO.getName());
        newProduct.setDescription(productRequestDTO.getDescription());
        newProduct.setPrice(productRequestDTO.getPrice());
        newProduct.setStock(productRequestDTO.getStock());
        productRepository.save(newProduct);
        return new MessageResponseDTO(
                "Producto creado con exito con el nombre: " + newProduct.getName());
    }

    /**
     * Recupera todos los productos disponibles en el catalogo. Utiliza
     * ProductRepository.findAll() para obtener todas las entidades Products de la base
     * de datos y las transforma a una lista de ProductResponseDTO mediante Stream API.
     *
     * @return Lista de ProductResponseDTO con todos los productos del catalogo, o lista
     *         vacia si no hay productos registrados.
     */
    public List<ProductResponseDTO> getAllProducts() {
        List<Products> products = productRepository.findAll();
        return products.stream().map(p -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId_p(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setStock(p.getStock());
            return dto;
        }).collect(Collectors.toList());
    }
}
