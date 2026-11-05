package cl.duoc.mesatech.solicitudes.service;

import cl.duoc.mesatech.solicitudes.entity.CategoriaLocal;
import cl.duoc.mesatech.solicitudes.entity.EstadoSolicitud;
import cl.duoc.mesatech.solicitudes.entity.Solicitud;
import cl.duoc.mesatech.solicitudes.repository.CategoriaLocalRepository;
import cl.duoc.mesatech.solicitudes.repository.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudService {

    private final SolicitudRepository repository;
    private final CategoriaLocalRepository categoriaLocalRepository;

    public SolicitudService(
            SolicitudRepository repository,
            CategoriaLocalRepository categoriaLocalRepository) {

        this.repository = repository;
        this.categoriaLocalRepository = categoriaLocalRepository;
    }

    public Solicitud crear(Solicitud solicitud) {

        boolean categoriaDisponible =
                categoriaLocalRepository.existsByCategoriaIdOrigen(
                        solicitud.getCategoriaId()
                );

        if (!categoriaDisponible) {
            throw new IllegalArgumentException(
                    "La categoria todavia no esta disponible en Solicitudes"
            );
        }

        solicitud.setId(null);
        solicitud.setEstado(EstadoSolicitud.CREADA);

        return repository.save(solicitud);
    }

    public List<Solicitud> listarTodas() {
        return repository.findAll();
    }

    public List<Solicitud> listarPorUsuario(String usuario) {
        return repository.findByUsuarioSolicitante(usuario);
    }

    public Solicitud buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Solicitud no encontrada"
                        )
                );
    }

    public List<CategoriaLocal> listarCategoriasDisponibles() {

        return categoriaLocalRepository.findAll();
    }

    public Solicitud cambiarEstado(
            Long id,
            EstadoSolicitud nuevoEstado) {

        Solicitud solicitud = buscarPorId(id);

        if (nuevoEstado == EstadoSolicitud.RESUELTA
                && solicitud.getEstado() != EstadoSolicitud.EN_PROCESO) {

            throw new IllegalStateException(
                    "La solicitud debe estar EN_PROCESO antes de pasar a RESUELTA"
            );
        }

        solicitud.setEstado(nuevoEstado);

        return repository.save(solicitud);
    }
}