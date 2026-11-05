package cl.duoc.mesatech.bff.controller;

import cl.duoc.mesatech.bff.service.BffService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class BffController {

    private final BffService service;

    public BffController(BffService service) {
        this.service = service;
    }

    // ---------------- SOLICITUDES ----------------

    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    @GetMapping("/solicitudes")
    public ResponseEntity<Object> obtenerSolicitudes() {

        return ResponseEntity.ok(
                service.obtenerSolicitudes()
        );
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    @GetMapping("/solicitudes/mias")
    public ResponseEntity<Object> obtenerMisSolicitudes(
            @AuthenticationPrincipal Jwt jwt) {

        String usuario = obtenerUsuario(jwt);

        return ResponseEntity.ok(
                service.obtenerMisSolicitudes(usuario)
        );
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/solicitudes")
    public ResponseEntity<Object> crearSolicitud(
            @RequestBody Map<String, Object> solicitud,
            @AuthenticationPrincipal Jwt jwt) {

        String usuario = obtenerUsuario(jwt);

        solicitud.put(
                "usuarioSolicitante",
                usuario
        );

        return ResponseEntity.ok(
                service.crearSolicitud(solicitud)
        );
    }

    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    @PatchMapping("/solicitudes/{id}/estado")
    public ResponseEntity<Object> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {

        return ResponseEntity.ok(
                service.cambiarEstado(id, estado)
        );
    }

    // ---------------- CATALOGO ----------------

    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    @GetMapping("/catalogo")
    public ResponseEntity<Object> obtenerCatalogo() {

        return ResponseEntity.ok(
                service.obtenerCatalogo()
        );
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/catalogo/categorias")
    public ResponseEntity<Object> crearCategoria(
            @RequestBody Object categoria) {

        return ResponseEntity.ok(
                service.crearCategoria(categoria)
        );
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/catalogo/categorias/{id}")
    public ResponseEntity<Object> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody Object categoria) {

        return ResponseEntity.ok(
                service.actualizarCategoria(id, categoria)
        );
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/catalogo/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @PathVariable Long id) {

        service.eliminarCategoria(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/catalogo/prioridades")
    public ResponseEntity<Object> crearPrioridad(
            @RequestBody Object prioridad) {

        return ResponseEntity.ok(
                service.crearPrioridad(prioridad)
        );
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/catalogo/prioridades/{id}")
    public ResponseEntity<Object> actualizarPrioridad(
            @PathVariable Long id,
            @RequestBody Object prioridad) {

        return ResponseEntity.ok(
                service.actualizarPrioridad(id, prioridad)
        );
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/catalogo/prioridades/{id}")
    public ResponseEntity<Void> eliminarPrioridad(
            @PathVariable Long id) {

        service.eliminarPrioridad(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // ---------------- USUARIO DEL JWT ----------------

    private String obtenerUsuario(Jwt jwt) {

        String usuario =
                jwt.getClaimAsString("preferred_username");

        if (usuario == null || usuario.isBlank()) {
            usuario = jwt.getClaimAsString("upn");
        }

        if (usuario == null || usuario.isBlank()) {
            usuario = jwt.getSubject();
        }

        return usuario;
    }
}