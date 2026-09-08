package com.refricentro.siver.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_orden")
public class EstadoOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Integer idEstado;

    @Column(name = "nombre", length = 50, nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "orden_flujo", nullable = false, unique = true)
    private Byte ordenFlujo;

    @Column(name = "es_final", nullable = false)
    private Boolean esFinal = false;

    public EstadoOrden() {
    }

    public EstadoOrden(Integer idEstado, String nombre, String descripcion, Byte ordenFlujo, Boolean esFinal) {
        this.idEstado = idEstado;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ordenFlujo = ordenFlujo;
        this.esFinal = esFinal;
    }

    public Integer getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Integer idEstado) {
        this.idEstado = idEstado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Byte getOrdenFlujo() {
        return ordenFlujo;
    }

    public void setOrdenFlujo(Byte ordenFlujo) {
        this.ordenFlujo = ordenFlujo;
    }

    public Boolean getEsFinal() {
        return esFinal;
    }

    public void setEsFinal(Boolean esFinal) {
        this.esFinal = esFinal;
    }
}
