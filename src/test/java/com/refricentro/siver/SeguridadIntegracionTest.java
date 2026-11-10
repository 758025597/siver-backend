package com.refricentro.siver;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba de INTEGRACION: la aplicacion completa, con la base MySQL real.
 *
 * Comprueba lo que pide la rubrica de seguridad y paginacion de punta a
 * punta (filtro JWT + Spring Security + controlador + JPA + MySQL):
 *   - sin token, todos los endpoints protegidos responden 401
 *   - un token inventado tambien es rechazado
 *   - con un login real, la API responde y pagina
 *   - el tamano de pagina esta limitado a 50
 *   - Swagger es publico, pero solo documenta: no abre la API
 *
 * Usa el usuario de docs/usuario-inicial.sql (admin@refricentro.pe).
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadIntegracionTest {

    @Autowired
    private MockMvc mvc;

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/roles", "/api/usuarios", "/api/clientes", "/api/categorias",
            "/api/proveedores", "/api/productos", "/api/movimientos-inventario",
            "/api/ventas", "/api/estados-orden", "/api/ordenes-servicio",
            "/api/productos/paginado"})
    void sinToken_todoEndpointProtegidoResponde401(String ruta) throws Exception {
        mvc.perform(get(ruta))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInventado_responde401() throws Exception {
        mvc.perform(get("/api/productos").header("Authorization", "Bearer esto.no.es-un-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConClaveIncorrecta_responde401() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@refricentro.pe\",\"clave\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conTokenReal_paginaYLimitaElTamano() throws Exception {
        String token = iniciarSesion();

        mvc.perform(get("/api/productos/paginado")
                        .param("page", "0")
                        .param("size", "1000")   // pide de mas a proposito
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(50))   // Spring lo bajo al maximo
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber());
    }

    @Test
    void swaggerEsPublicoYDocumentaElJwt() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes", hasKey("bearerAuth")))
                .andExpect(jsonPath("$.paths", hasKey("/api/clientes/paginado")));
    }

    private String iniciarSesion() throws Exception {
        String respuesta = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"admin@refricentro.pe\",\"clave\":\"Admin2026\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.token");
    }
}
