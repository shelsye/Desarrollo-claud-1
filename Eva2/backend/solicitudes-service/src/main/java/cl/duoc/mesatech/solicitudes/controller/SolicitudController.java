package cl.duoc.mesatech.solicitudes.controller;

import cl.duoc.mesatech.solicitudes.entity.CategoriaLocal;
import cl.duoc.mesatech.solicitudes.entity.EstadoSolicitud;
import cl.duoc.mesatech.solicitudes.entity.Solicitud;
import cl.duoc.mesatech.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Solicitud> crear(
            @Valid @RequestBody Solicitud solicitud) {

        return ResponseEntity.ok(
                service.crear(solicitud)
        );
    }

    @GetMapping
    public ResponseEntity<List<Solicitud>> listarTodas() {

        return ResponseEntity.ok(
                service.listarTodas()
        );
    }

    @GetMapping("/mias")
    public ResponseEntity<List<Solicitud>> listarMias(
            @RequestParam String usuario) {

        return ResponseEntity.ok(
                service.listarPorUsuario(usuario)
        );
    }

    @GetMapping("/categorias-disponibles")
    public ResponseEntity<List<CategoriaLocal>>
            listarCategoriasDisponibles() {

        return ResponseEntity.ok(
                service.listarCategoriasDisponibles()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.buscarPorId(id)
        );
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Solicitud> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoSolicitud estado) {

        return ResponseEntity.ok(
                service.cambiarEstado(id, estado)
        );
    }
}