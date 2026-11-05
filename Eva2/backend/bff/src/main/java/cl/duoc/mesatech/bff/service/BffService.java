package cl.duoc.mesatech.bff.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BffService {

    private final RestClient solicitudesClient;
    private final RestClient catalogoClient;

    public BffService(
            @Value("${mesatech.solicitudes.url}") String solicitudesUrl,
            @Value("${mesatech.catalogo.url}") String catalogoUrl) {

        this.solicitudesClient = RestClient.create(solicitudesUrl);
        this.catalogoClient = RestClient.create(catalogoUrl);
    }

    // ---------------- SOLICITUDES ----------------

    public Object obtenerSolicitudes() {
        return solicitudesClient.get()
                .uri("/v1/solicitudes")
                .retrieve()
                .body(Object.class);
    }

    public Object obtenerMisSolicitudes(String usuario) {
        return solicitudesClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/solicitudes/mias")
                        .queryParam("usuario", usuario)
                        .build())
                .retrieve()
                .body(Object.class);
    }

    public Object obtenerCategoriasDisponibles() {
        return solicitudesClient.get()
                .uri("/v1/solicitudes/categorias-disponibles")
                .retrieve()
                .body(Object.class);
    }

    public Object crearSolicitud(Object solicitud) {
        return solicitudesClient.post()
                .uri("/v1/solicitudes")
                .body(solicitud)
                .retrieve()
                .body(Object.class);
    }

    public Object cambiarEstado(Long id, String estado) {
        return solicitudesClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/solicitudes/{id}/estado")
                        .queryParam("estado", estado)
                        .build(id))
                .retrieve()
                .body(Object.class);
    }

    // ---------------- CATALOGO ----------------

    public Object obtenerCatalogo() {
        return catalogoClient.get()
                .uri("/v1/catalogo")
                .retrieve()
                .body(Object.class);
    }

    public Object crearCategoria(Object categoria) {
        return catalogoClient.post()
                .uri("/v1/catalogo/categorias")
                .body(categoria)
                .retrieve()
                .body(Object.class);
    }

    public Object actualizarCategoria(Long id, Object categoria) {
        return catalogoClient.put()
                .uri("/v1/catalogo/categorias/{id}", id)
                .body(categoria)
                .retrieve()
                .body(Object.class);
    }

    public void eliminarCategoria(Long id) {
        catalogoClient.delete()
                .uri("/v1/catalogo/categorias/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    public Object crearPrioridad(Object prioridad) {
        return catalogoClient.post()
                .uri("/v1/catalogo/prioridades")
                .body(prioridad)
                .retrieve()
                .body(Object.class);
    }

    public Object actualizarPrioridad(Long id, Object prioridad) {
        return catalogoClient.put()
                .uri("/v1/catalogo/prioridades/{id}", id)
                .body(prioridad)
                .retrieve()
                .body(Object.class);
    }

    public void eliminarPrioridad(Long id) {
        catalogoClient.delete()
                .uri("/v1/catalogo/prioridades/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}