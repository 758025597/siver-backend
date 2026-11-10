package com.refricentro.siver.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.refricentro.siver.config.MapperConfig;
import com.refricentro.siver.config.SecurityConfig;
import com.refricentro.siver.config.ValidationConfig;
import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.model.Cliente;
import com.refricentro.siver.security.DetalleUsuarioService;
import com.refricentro.siver.security.JwtUtil;
import com.refricentro.siver.security.PuntoEntradaNoAutorizado;
import com.refricentro.siver.service.ClienteService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba del controlador de clientes, SIN base de datos.
 *
 * @WebMvcTest levanta solo la capa web: el controlador, el manejador de
 * errores, los filtros y la seguridad. El servicio se reemplaza por un
 * simulacro (@MockitoBean): asi se prueba solo lo que hace el controlador
 * (codigos HTTP, cabeceras, JSON) y la prueba corre en segundos.
 *
 * Como ClienteController hereda de ControladorGenerico, estas pruebas
 * cubren tambien el comportamiento de los otros 8 controladores CRUD.
 */
@WebMvcTest(ClienteController.class)
@Import({SecurityConfig.class, PuntoEntradaNoAutorizado.class, MapperConfig.class, ValidationConfig.class})
class ClienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ClienteService clienteService;

    // Los necesita el filtro JWT. Con @WithMockUser no se llegan a usar.
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private DetalleUsuarioService detalleUsuarioService;

    private static Cliente cliente(int id, String nombres) {
        Cliente c = new Cliente();
        c.setIdCliente(id);
        c.setTipoDocumento("DNI");
        c.setNumeroDocumento("4567890" + id);
        c.setNombres(nombres);
        return c;
    }

    @Test
    void sinToken_responde401() throws Exception {
        mvc.perform(get("/api/clientes/paginado"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.estado").value(401));
    }

    @Test
    @WithMockUser
    void paginado_devuelveTotalesYUsaPageYSize() throws Exception {
        Pageable pedida = PageRequest.of(1, 2);
        given(clienteService.listarPaginado(isNull(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(cliente(3, "Ana"), cliente(4, "Luis")), pedida, 5));

        mvc.perform(get("/api/clientes/paginado").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nombres").value("Ana"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3));

        // El page y el size de la URL llegan al servicio tal cual.
        verify(clienteService).listarPaginado(isNull(), eq(pedida));
    }

    @Test
    @WithMockUser
    void paginado_pasaElTextoDeBusquedaAlServicio() throws Exception {
        given(clienteService.listarPaginado(eq("ana"), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(cliente(3, "Ana")), PageRequest.of(0, 10), 1));

        mvc.perform(get("/api/clientes/paginado").param("buscar", "ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    void buscarPorId_inexistente_responde404() throws Exception {
        given(clienteService.buscarPorId(99)).willThrow(new RecursoNoEncontradoException("Cliente", 99));

        mvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Cliente con id 99 no existe"));
    }

    @Test
    @WithMockUser
    void crear_valido_responde201ConLocation() throws Exception {
        given(clienteService.crear(any(Cliente.class))).willReturn(cliente(7, "Rosa Quispe"));

        mvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoDocumento":"DNI","numeroDocumento":"45678907",
                                 "nombres":"Rosa Quispe","telefono":"987654321"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/clientes/7")))
                .andExpect(jsonPath("$.idCliente").value(7));
    }

    @Test
    @WithMockUser
    void crear_invalido_responde400ConLosCampos() throws Exception {
        mvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipoDocumento":"DNI","numeroDocumento":"",
                                 "nombres":"Ro","correo":"no-es-correo"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.numeroDocumento").exists())
                .andExpect(jsonPath("$.campos.nombres").exists())
                .andExpect(jsonPath("$.campos.correo").exists());
    }

    @Test
    @WithMockUser
    void eliminar_responde204() throws Exception {
        mvc.perform(delete("/api/clientes/7"))
                .andExpect(status().isNoContent());

        verify(clienteService).eliminar(7);
    }

    @Test
    void cors_permiteSoloAlFrontend() throws Exception {
        mvc.perform(options("http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));

        mvc.perform(options("http://sitio-malicioso.com"))
                .andExpect(status().isForbidden());
    }

    /** Preflight de CORS: lo que manda el navegador antes de un GET desde otro origen. */
    private static org.springframework.test.web.servlet.RequestBuilder options(String origen) {
        return org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .options("/api/clientes")
                .header("Origin", origen)
                .header("Access-Control-Request-Method", "GET");
    }
}
