package Fight_club.Fight_Services.Infrastructure.Config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


@Configuration
public class RabbitConfig {


    //Como Consumidor
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    public static final String EXCHANGE = "room.exchange"; // decide a quien enviar
    public static final String ROOM_QUEUE = "room.queue"; // donde se recibe
    public static final String ROUTING_KEY = "room.initialized"; // llave para recibir

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }


    @Bean
    public Queue queue() {
        return new Queue(ROOM_QUEUE, false);
    }

    @Bean
    public Binding bindingInitializedRoom(Queue queue, TopicExchange exchange) {
        return BindingBuilder.
                bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }


    //Como Productor

}
