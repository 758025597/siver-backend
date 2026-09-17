package com.refricentro.siver.controller;

import com.refricentro.siver.dto.ProveedorRequest;
import com.refricentro.siver.dto.ProveedorResponse;
import com.refricentro.siver.modelos.Proveedor;
import com.refricentro.siver.service.ProveedorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController
        extends ControladorGenerico<Proveedor, Integer, ProveedorRequest, ProveedorResponse> {

    public ProveedorController(ProveedorService servicio) {
        super(servicio);
    }

    @Override
    protected Proveedor aEntidad(ProveedorRequest request) {
        Proveedor proveedor = new Proveedor();

        proveedor.setRuc(request.getRuc());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setDireccion(request.getDireccion());

        return proveedor;
    }

    @Override
    protected ProveedorResponse aRespuesta(Proveedor proveedor) {
        return new ProveedorResponse(
                proveedor.getIdProveedor(),
                proveedor.getRuc(),
                proveedor.getRazonSocial(),
                proveedor.getContacto(),
                proveedor.getTelefono(),
                proveedor.getCorreo(),
                proveedor.getDireccion(),
                proveedor.getActivo(),
                proveedor.getFechaRegistro()
        );
    }
}