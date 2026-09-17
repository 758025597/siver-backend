package com.refricentro.siver.config;

import com.refricentro.siver.security.JwtAuthFilter;
import com.refricentro.siver.security.PuntoEntradaNoAutorizado;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracion de seguridad de la API.
 *
 * Reglas:
 *   - /api/auth/**  abierto: ahi esta el login, si estuviera cerrado nadie
 *                   podria entrar nunca.
 *   - OPTIONS       abierto: el navegador manda esa peticion antes de cada
 *                   llamada real (preflight de CORS) y no lleva token.
 *   - todo lo demas exige un JWT valido.
 *
 * STATELESS significa que el servidor no guarda sesiones: cada peticion se
 * identifica sola con su token. Es lo que corresponde en una API REST.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final PuntoEntradaNoAutorizado puntoEntradaNoAutorizado;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          PuntoEntradaNoAutorizado puntoEntradaNoAutorizado) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.puntoEntradaNoAutorizado = puntoEntradaNoAutorizado;
    }

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            // La API no usa formularios ni cookies de sesion, asi que el
            // token CSRF no aplica. Se desactiva porque si no bloquea los POST.
            .csrf(csrf -> csrf.disable())

            // Usa la configuracion de CorsConfig.
            .cors(cors -> {})

            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Sin esto, entrar sin token devuelve un 403 con cuerpo vacio.
            .exceptionHandling(e -> e.authenticationEntryPoint(puntoEntradaNoAutorizado))

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .anyRequest().authenticated())

            // El filtro del token corre ANTES del de usuario y contrasena:
            // si el token ya identifico a alguien, no hace falta lo demas.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * El que compara la contrasena que llega con el hash guardado.
     * Lo usa AuthController al iniciar sesion.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /** BCrypt: al guardar cifra, al iniciar sesion compara. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
