package com.refricentro.siver.service.impl;

import com.refricentro.siver.exception.RecursoNoEncontradoException;
import com.refricentro.siver.service.CrudService;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementa el CRUD completo UNA SOLA VEZ para todas las entidades.
 *
 * Los servicios concretos heredan de aqui y solo escriben lo que es propio
 * de su entidad. El DELETE de esta clase es FISICO (borra la fila).
 * Si la entidad necesita borrado logico, se hereda de ServicioSoftDelete.
 *
 * Como se usa:
 *
 *   @Service
 *   public class RolServiceImpl extends ServicioSoftDelete<Rol, Integer>
 *                               implements RolService {
 *
 *       public RolServiceImpl(RolRepository repo) { super(repo); }
 *
 *       protected String nombreRecurso() { return "Rol"; }
 *
 *       protected void copiarDatos(Rol origen, Rol destino) {
 *           destino.setNombre(origen.getNombre());
 *           destino.setDescripcion(origen.getDescripcion());
 *       }
 *   }
 */
public abstract class ServicioGenerico<T, ID> implements CrudService<T, ID> {

    protected final JpaRepository<T, ID> repositorio;

    protected ServicioGenerico(JpaRepository<T, ID> repositorio) {
        this.repositorio = repositorio;
    }

    /** Nombre de la entidad para los mensajes de error. Ej: "Producto". */
    protected abstract String nombreRecurso();

    /**
     * Copia los campos EDITABLES de origen a destino.
     * Lo define cada servicio porque solo el sabe que campos se pueden cambiar:
     * por ejemplo el id, la fecha_registro y el stock NUNCA se copian.
     */
    protected abstract void copiarDatos(T origen, T destino);

    @Override
    @Transactional
    public T crear(T entidad) {
        return repositorio.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> listar() {
        return repositorio.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public T buscarPorId(ID id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(nombreRecurso(), id));
    }

    @Override
    @Transactional
    public T actualizar(ID id, T datos) {
        T existente = buscarPorId(id);
        copiarDatos(datos, existente);
        return repositorio.save(existente);
    }

    /**
     * BORRADO FISICO: la fila desaparece de la tabla.
     * Solo lo usa estado_orden. Las demas entidades heredan de
     * ServicioSoftDelete, que sobrescribe este metodo.
     */
    @Override
    @Transactional
    public void eliminar(ID id) {
        repositorio.delete(buscarPorId(id));
    }
}
