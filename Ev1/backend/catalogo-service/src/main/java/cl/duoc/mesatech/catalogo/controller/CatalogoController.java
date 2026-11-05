package cl.duoc.mesatech.catalogo.controller;

import cl.duoc.mesatech.catalogo.entity.Categoria;
import cl.duoc.mesatech.catalogo.entity.Prioridad;
import cl.duoc.mesatech.catalogo.service.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/catalogo")
public class CatalogoController {

    private final CatalogoService service;

    public CatalogoController(CatalogoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerCatalogo() {
        return ResponseEntity.ok(
                Map.of(
                        "categorias", service.listarCategorias(),
                        "prioridades", service.listarPrioridades()
                )
        );
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(service.listarCategorias());
    }

    @PostMapping("/categorias")
    public ResponseEntity<Categoria> crearCategoria(
            @Valid @RequestBody Categoria categoria) {

        return ResponseEntity.ok(
                service.crearCategoria(categoria)
        );
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody Categoria categoria) {

        return ResponseEntity.ok(
                service.actualizarCategoria(id, categoria)
        );
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @PathVariable Long id) {

        service.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/prioridades")
    public ResponseEntity<List<Prioridad>> listarPrioridades() {
        return ResponseEntity.ok(service.listarPrioridades());
    }

    @PostMapping("/prioridades")
    public ResponseEntity<Prioridad> crearPrioridad(
            @Valid @RequestBody Prioridad prioridad) {

        return ResponseEntity.ok(
                service.crearPrioridad(prioridad)
        );
    }

    @PutMapping("/prioridades/{id}")
    public ResponseEntity<Prioridad> actualizarPrioridad(
            @PathVariable Long id,
            @Valid @RequestBody Prioridad prioridad) {

        return ResponseEntity.ok(
                service.actualizarPrioridad(id, prioridad)
        );
    }

    @DeleteMapping("/prioridades/{id}")
    public ResponseEntity<Void> eliminarPrioridad(
            @PathVariable Long id) {

        service.eliminarPrioridad(id);
        return ResponseEntity.noContent().build();
    }
}