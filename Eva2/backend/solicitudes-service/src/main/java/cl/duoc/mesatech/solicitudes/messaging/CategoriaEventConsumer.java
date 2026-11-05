package cl.duoc.mesatech.solicitudes.messaging;

import cl.duoc.mesatech.solicitudes.config.RabbitMQConfig;
import cl.duoc.mesatech.solicitudes.entity.CategoriaLocal;
import cl.duoc.mesatech.solicitudes.event.CategoriaCreadaEvent;
import cl.duoc.mesatech.solicitudes.repository.CategoriaLocalRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CategoriaEventConsumer {

    private final CategoriaLocalRepository categoriaLocalRepository;

    public CategoriaEventConsumer(
            CategoriaLocalRepository categoriaLocalRepository) {
        this.categoriaLocalRepository = categoriaLocalRepository;
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumirCategoriaCreada(CategoriaCreadaEvent evento) {

        System.out.println(
                "Evento recibido. eventId=" + evento.getEventId()
                        + ", categoriaId=" + evento.getCategoriaId()
                        + ", nombre=" + evento.getNombre()
        );

        if (categoriaLocalRepository
                .existsByCategoriaIdOrigen(evento.getCategoriaId())) {

            System.out.println(
                    "Categoria ya existe en Solicitudes. categoriaId="
                            + evento.getCategoriaId()
            );

            return;
        }

        CategoriaLocal categoriaLocal = CategoriaLocal.builder()
                .categoriaIdOrigen(evento.getCategoriaId())
                .nombre(evento.getNombre())
                .eventId(evento.getEventId())
                .fechaEvento(evento.getFechaEvento())
                .build();

        CategoriaLocal guardada =
                categoriaLocalRepository.save(categoriaLocal);

        System.out.println(
                "Categoria persistida en RDS Solicitudes. idLocal="
                        + guardada.getId()
                        + ", categoriaIdOrigen="
                        + guardada.getCategoriaIdOrigen()
                        + ", eventId="
                        + guardada.getEventId()
        );
    }
}
