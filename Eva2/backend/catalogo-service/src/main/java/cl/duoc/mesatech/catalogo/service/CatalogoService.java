package cl.duoc.mesatech.catalogo.service;

import cl.duoc.mesatech.catalogo.entity.Categoria;
import cl.duoc.mesatech.catalogo.entity.Prioridad;
import cl.duoc.mesatech.catalogo.event.CategoriaCreadaEvent;
import cl.duoc.mesatech.catalogo.messaging.CategoriaEventPublisher;
import cl.duoc.mesatech.catalogo.repository.CategoriaRepository;
import cl.duoc.mesatech.catalogo.repository.PrioridadRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CatalogoService {

    private final CategoriaRepository categoriaRepository;
    private final PrioridadRepository prioridadRepository;
    private final CategoriaEventPublisher categoriaEventPublisher;

    public CatalogoService(
            CategoriaRepository categoriaRepository,
            PrioridadRepository prioridadRepository,
            CategoriaEventPublisher categoriaEventPublisher) {

        this.categoriaRepository = categoriaRepository;
        this.prioridadRepository = prioridadRepository;
        this.categoriaEventPublisher = categoriaEventPublisher;
    }

    // CATEGORIAS

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria crearCategoria(Categoria categoria) {

        categoria.setId(null);

        Categoria guardada = categoriaRepository.save(categoria);

        CategoriaCreadaEvent evento = CategoriaCreadaEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .tipoEvento("CATEGORIA_CREADA")
                .fechaEvento(Instant.now().toString())
                .categoriaId(guardada.getId())
                .nombre(guardada.getNombre())
                .build();

        categoriaEventPublisher.publicarCategoriaCreada(evento);

        return guardada;
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