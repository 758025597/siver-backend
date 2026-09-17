package com.refricentro.siver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cifrado de contrasenas.
 *
 * OJO: esto NO activa login ni bloquea endpoints. Solo publica el objeto
 * que sabe convertir una contrasena en hash. Para tener login habria que
 * agregar spring-boot-starter-security, que es otra cosa.
 *
 * Por que hace falta: la tabla usuario tiene
 *   CONSTRAINT ck_usuario_clave CHECK (CHAR_LENGTH(clave) >= 20)
 * Una contrasena real como "admin123" tiene 8 caracteres y MySQL la
 * rechaza. Un hash BCrypt tiene 60 y pasa. La base esta obligando a
 * guardar el hash, nunca la contrasena.
 */
@Configuration
public class SeguridadConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
