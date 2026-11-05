package cl.duoc.mesatech.bff.controller;

import cl.duoc.mesatech.bff.service.BffService;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    private final BffService service;

    public VersionController(BffService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    @GetMapping("/v2/solicitudes")
    public ResponseEntity<Object> solicitudesV2() {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("version", "v2");
        respuesta.put(
                "descripcion",
                "Version 2 de la API MesaTech"
        );
        respuesta.put(
                "solicitudes",
                service.obtenerSolicitudes()
        );

        return ResponseEntity.ok(respuesta);
    }
}