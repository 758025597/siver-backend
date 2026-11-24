package com.refricentro.siver.config;

import static com.refricentro.siver.security.Roles.ADMIN;
import static com.refricentro.siver.security.Roles.TECNICO;
import static com.refricentro.siver.security.Roles.VENDEDOR;

import com.refricentro.siver.security.AccesoDenegado;
import com.refricentro.siver.security.JwtAuthFilter;
import com.refricentro.siver.security.PuntoEntradaNoAutorizado;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
 *   - Swagger       abierto: es solo la documentacion.
 *   - todo lo demas exige un JWT valido. Sin token -> 401.
 *   - ademas, cada modulo exige un ROL. Con token pero sin el rol -> 403.
 *
 * Matriz de permisos (ver tambien security/Roles.java):
 *
 *   Modulo                    Leer (GET)               Escribir (POST/PUT/PATCH/DELETE)
 *   ------------------------  -----------------------  --------------------------------
 *   usuarios                  cualquiera con sesion*   ADMINISTRADOR
 *   roles, auditoria          ADMINISTRADOR            ADMINISTRADOR
 *   categorias, proveedores,
 *   productos, estados-orden  cualquiera con sesion    ADMINISTRADOR
 *   clientes                  cualquiera con sesion    ADMIN, VENDEDOR, TECNICO (borrar: ADMIN)
 *   ventas                    ADMIN, VENDEDOR          ADMIN, VENDEDOR (anular: ADMIN)
 *   ordenes-servicio          ADMIN, TECNICO, VENDEDOR ADMIN, TECNICO (borrar: ADMIN)
 *   movimientos-inventario    ADMIN, VENDEDOR          ADMINISTRADOR (ajustes)
 *   reportes                  segun el reporte (@PreAuthorize en ReporteController)
 *   dashboard, notificaciones cualquiera con sesion
 *
 *   * el formulario de ordenes de servicio necesita la lista de tecnicos.
 *
 * STATELESS significa que el servidor no guarda sesiones: cada peticion se
 * identifica sola con su token. Es lo que corresponde en una API REST.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** La pagina de Swagger y el JSON que lee. */
    private static final String[] RUTAS_SWAGGER = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final PuntoEntradaNoAutorizado puntoEntradaNoAutorizado;
    private final AccesoDenegado accesoDenegado;

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            // La API no usa formularios ni cookies de sesion, asi que el
            // token CSRF no aplica. Se desactiva porque si no bloquea los POST.
            .csrf(csrf -> csrf.disable())

            // Ni formulario de login de Spring ni usuario/clave por cabecera:
            // la unica forma de entrar es POST /api/auth/login con JWT.
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // Usa la configuracion de CorsConfig.
            .cors(cors -> {})

            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Sin esto, entrar sin token devuelve un 403 con cuerpo vacio.
            // 401 sin token y 403 sin el rol, los dos con el JSON de siempre.
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(puntoEntradaNoAutorizado)
                    .accessDeniedHandler(accesoDenegado))

            // Cabeceras de proteccion que el navegador respeta:
            //  - X-Content-Type-Options: nosniff  -> no "adivina" tipos (XSS)
            //  - X-Frame-Options: DENY            -> nadie embebe la API en un iframe
            //  - Referrer-Policy                  -> no filtra URLs internas
            .headers(h -> h
                    .contentTypeOptions(c -> {})
                    .frameOptions(f -> f.deny())
                    .referrerPolicy(r -> r.policy(
                            org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
                                    .ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    // Swagger solo muestra la documentacion: abrirlo NO abre la
                    // API. Cada endpoint sigue exigiendo el token.
                    .requestMatchers(RUTAS_SWAGGER).permitAll()
                    .requestMatchers("/error").permitAll()

                    // --- Solo administrador ---
                    .requestMatchers("/api/roles/**", "/api/auditoria/**").hasRole(ADMIN)
                    .requestMatchers(HttpMethod.GET, "/api/usuarios/**").authenticated()
                    .requestMatchers("/api/usuarios/**").hasRole(ADMIN)

                    // --- Catalogos: todos leen, solo el administrador cambia ---
                    .requestMatchers(HttpMethod.GET,
                            "/api/categorias/**", "/api/proveedores/**",
                            "/api/productos/**", "/api/estados-orden/**").authenticated()
                    .requestMatchers(
                            "/api/categorias/**", "/api/proveedores/**",
                            "/api/productos/**", "/api/estados-orden/**").hasRole(ADMIN)

                    // --- Clientes: los atienden los tres roles ---
                    .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasRole(ADMIN)
                    .requestMatchers("/api/clientes/**").hasAnyRole(ADMIN, VENDEDOR, TECNICO)

                    // --- Ventas: anular es decision del administrador ---
                    .requestMatchers(HttpMethod.PATCH, "/api/ventas/*/anular").hasRole(ADMIN)
                    .requestMatchers("/api/ventas/**").hasAnyRole(ADMIN, VENDEDOR)

                    // --- Ordenes de servicio ---
                    .requestMatchers(HttpMethod.GET, "/api/ordenes-servicio/**")
                            .hasAnyRole(ADMIN, TECNICO, VENDEDOR)
                    .requestMatchers(HttpMethod.DELETE, "/api/ordenes-servicio/**").hasRole(ADMIN)
                    .requestMatchers("/api/ordenes-servicio/**").hasAnyRole(ADMIN, TECNICO)

                    // --- Inventario ---
                    .requestMatchers(HttpMethod.GET, "/api/movimientos-inventario/**")
                            .hasAnyRole(ADMIN, VENDEDOR)
                    .requestMatchers("/api/movimientos-inventario/**").hasRole(ADMIN)

                    // dashboard, notificaciones y reportes (estos con @PreAuthorize)
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
