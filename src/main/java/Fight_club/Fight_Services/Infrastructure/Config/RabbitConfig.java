package Fight_club.Fight_Services.Infrastructure.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_ROOM = "room.exchange";
    public static final String ROOM_QUEUE = "room.queue";
    public static final String ROUTING_KEY_ROOM = "room.initialized";

    public static final String EXCHANGE_USER = "user.events";
    public static final String QUEUE_USER_REGISTERED = "fight.user.registered.queue";
    public static final String ROUTING_KEY_USER_REG = "user.registered";
    
    public static final String QUEUE_GUEST_REGISTERED = "fight.guest.registered.queue";
    public static final String ROUTING_KEY_GUEST = "user.guest.registered";
    

    @Bean
    public Queue guestRegisteredQueue() {
        return new Queue(QUEUE_GUEST_REGISTERED, true);
    }

    @Bean
    public Binding bindingGuestRegistered(Queue guestRegisteredQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(guestRegisteredQueue)
                .to(userExchange)
                .with(ROUTING_KEY_GUEST);
    }
    @Bean
    public TopicExchange roomExchange() {
        return new TopicExchange(EXCHANGE_ROOM);
    }

    @Bean
    public Queue roomQueue() {
        return new Queue(ROOM_QUEUE, false);
    }

    @Bean
    public Binding bindingInitializedRoom(Queue roomQueue, TopicExchange roomExchange) {
        return BindingBuilder
                .bind(roomQueue)
                .to(roomExchange)
                .with(ROUTING_KEY_ROOM);
    }

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(EXCHANGE_USER);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(QUEUE_USER_REGISTERED, true); 
    }

    @Bean
    public Binding bindingUserRegistered(Queue userRegisteredQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(userRegisteredQueue)
                .to(userExchange)
                .with(ROUTING_KEY_USER_REG);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}