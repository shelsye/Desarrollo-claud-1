package cl.duoc.mesatech.catalogo.service;

import cl.duoc.mesatech.catalogo.entity.Categoria;
import cl.duoc.mesatech.catalogo.entity.Prioridad;
import cl.duoc.mesatech.catalogo.repository.CategoriaRepository;
import cl.duoc.mesatech.catalogo.repository.PrioridadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoService {

    private final CategoriaRepository categoriaRepository;
    private final PrioridadRepository prioridadRepository;

    public CatalogoService(
            CategoriaRepository categoriaRepository,
            PrioridadRepository prioridadRepository) {

        this.categoriaRepository = categoriaRepository;
        this.prioridadRepository = prioridadRepository;
    }

    // CATEGORIAS

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria crearCategoria(Categoria categoria) {
        categoria.setId(null);
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizarCategoria(Long id, Categoria datos) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Categoria no encontrada"));

        categoria.setNombre(datos.getNombre());

        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {

        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria no encontrada");
        }

        categoriaRepository.deleteById(id);
    }

    // PRIORIDADES

    public List<Prioridad> listarPrioridades() {
        return prioridadRepository.findAll();
    }

    public Prioridad crearPrioridad(Prioridad prioridad) {
        prioridad.setId(null);
        return prioridadRepository.save(prioridad);
    }

    public Prioridad actualizarPrioridad(Long id, Prioridad datos) {

        Prioridad prioridad = prioridadRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Prioridad no encontrada"));

        prioridad.setNombre(datos.getNombre());

        return prioridadRepository.save(prioridad);
    }

    public void eliminarPrioridad(Long id) {

        if (!prioridadRepository.existsById(id)) {
            throw new RuntimeException("Prioridad no encontrada");
        }

        prioridadRepository.deleteById(id);
    }
}
