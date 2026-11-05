package cl.duoc.mesatech.catalogo.messaging;

import cl.duoc.mesatech.catalogo.config.RabbitMQConfig;
import cl.duoc.mesatech.catalogo.event.CategoriaCreadaEvent;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CategoriaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CategoriaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarCategoriaCreada(CategoriaCreadaEvent evento) {

        try {
            CorrelationData correlationData =
                    new CorrelationData(evento.getEventId());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    evento,
                    message -> {
                        message.getMessageProperties()
                                .setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                        message.getMessageProperties()
                                .setMessageId(evento.getEventId());

                        return message;
                    },
                    correlationData
            );

            CorrelationData.Confirm confirm =
                    correlationData.getFuture().get(5, TimeUnit.SECONDS);

            ReturnedMessage returned =
                    correlationData.getReturned();

            if (returned != null) {
                throw new RuntimeException(
                        "RabbitMQ no encontró una ruta para el evento: "
                                + returned.getReplyText()
                );
            }

            if (!confirm.ack()) {
                throw new RuntimeException(
                        "RabbitMQ no confirmó la publicación: "
                                + confirm.reason()
                );
            }

            System.out.println(
                    "Evento categoria.creada confirmado. eventId="
                            + evento.getEventId()
                            + ", categoriaId="
                            + evento.getCategoriaId()
            );

        } catch (Exception e) {

            System.err.println(
                    "ERROR publicando categoria.creada. eventId="
                            + evento.getEventId()
                            + " - "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "La categoría fue guardada, pero no se pudo confirmar su publicación en RabbitMQ.",
                    e
            );
        }
    }
}