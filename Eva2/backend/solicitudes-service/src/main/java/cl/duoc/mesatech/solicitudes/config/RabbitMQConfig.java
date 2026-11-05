package cl.duoc.mesatech.solicitudes.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "catalogo.exchange";
    public static final String QUEUE = "solicitudes.catalogo.queue";
    public static final String ROUTING_KEY = "categoria.creada";

    @Bean
    public DirectExchange catalogoExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue solicitudesCatalogoQueue() {
        return QueueBuilder
                .durable(QUEUE)
                .build();
    }

    @Bean
    public Binding catalogoBinding(
            Queue solicitudesCatalogoQueue,
            DirectExchange catalogoExchange) {

        return BindingBuilder
                .bind(solicitudesCatalogoQueue)
                .to(catalogoExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}