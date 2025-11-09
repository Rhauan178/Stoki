package com.seu.restaurante;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Esta anotação diz ao Spring Boot: 
// "Comece o servidor web e procure por @RestController aqui."
@SpringBootApplication
public class AppRestauranteApplication {

    public static void main(String[] args) {
        // Isso inicia o Tomcat embutido e carrega o servidor web.
        SpringApplication.run(AppRestauranteApplication.class, args);
    }
}