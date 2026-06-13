package e_commerce.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) genérico para respuestas que solo contienen un mensaje
 * descriptivo, utilizado principalmente para confirmar operaciones exitosas o devolver
 * mensajes de error en un formato JSON estructurado.
 */
@Data
public class MessageResponseDTO {

    private String message;

    /**
     * Construye un MessageResponseDTO con el mensaje especificado.
     *
     * @param message Texto descriptivo del resultado de la operación o del error.
     */
    public MessageResponseDTO(String message) {
        this.message = message;
    }
}
