package e_commerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación E-Commerce que sirve como punto de entrada para el
 * arranque del sistema mediante Spring Boot.
 */
@SpringBootApplication
public class ECommerceApplication {

    /**
     * Método principal que inicia la aplicación Spring Boot. Internamente,
     * SpringApplication.run() crea el ApplicationContext, registra todos los beans,
     * inicia el servidor web embebido (Tomcat) y expone los endpoints REST configurados.
     *
     * @param args Argumentos de línea de comandos pasados al iniciar la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }
}
