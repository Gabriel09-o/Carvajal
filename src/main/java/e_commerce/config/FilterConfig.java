package e_commerce.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import e_commerce.filter.JwtValidationFilter;

/**
 * Clase de configuración que registra el filtro de validación JWT (JwtValidationFilter)
 * en la cadena de filtros de la aplicación Spring Boot.
 */
@Configuration
public class FilterConfig {

    /**
     * Crea y configura un FilterRegistrationBean para el JwtValidationFilter.
     * @param jwtValidationFilter Instancia de JwtValidationFilter inyectada automáticamente
     * por Spring gracias a la anotación @Component en el filtro.
     * @return FilterRegistrationBean configurado con el filtro, el patrón de URL y el orden de precedencia.
     */
    @Bean
    public FilterRegistrationBean<JwtValidationFilter> jwtFilter(JwtValidationFilter jwtValidationFilter) {
        FilterRegistrationBean<JwtValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtValidationFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
