package Fight_club.Fight_Services.Infrastructure.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public static final String FIGHT_EXCHANGE = "fight.events";
    public static final String FIGHT_FINISHED_ROUTING_KEY = "fight.finished";
    public static final String FIGHT_COMMAND_EXCHANGE = "fight.commands";
    public static final String FIGHT_COMMAND_QUEUE = "fightCommandQueue";
    private static final String FIGHT_COMMAND_QUEUE_PREFIX = "fight.command.slot.";
    private static final String FIGHT_COMMAND_QUEUE_SUFFIX = ".queue";
    private static final String FIGHT_COMMAND_ROUTING_PREFIX = "fight.command.slot.";

    // Exchanges
    @Bean public DirectExchange userExchange() { return new DirectExchange(EXCHANGE_USER); }
    @Bean public TopicExchange roomExchange() { return new TopicExchange(EXCHANGE_ROOM); }
    @Bean public TopicExchange fightExchange() { return new TopicExchange(FIGHT_EXCHANGE); }
    @Bean public TopicExchange fightCommandExchange() { return new TopicExchange(FIGHT_COMMAND_EXCHANGE); }


    @Bean
    public Queue guestRegisteredQueue() {
        return new Queue(QUEUE_GUEST_REGISTERED, true);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(QUEUE_USER_REGISTERED, true);
    }

    @Bean
    public Queue roomQueue() {
        return new Queue(ROOM_QUEUE, false);
    }

    // Bindings 

    @Bean
    public Binding bindingGuestRegistered(
            @Qualifier("guestRegisteredQueue") Queue guestRegisteredQueue,
            @Qualifier("userExchange") DirectExchange userExchange) {
        return BindingBuilder
                .bind(guestRegisteredQueue)
                .to(userExchange)
                .with(ROUTING_KEY_GUEST);
    }

    @Bean
    public Binding bindingUserRegistered(
            @Qualifier("userRegisteredQueue") Queue userRegisteredQueue,
            @Qualifier("userExchange") DirectExchange userExchange) {
        return BindingBuilder
                .bind(userRegisteredQueue)
                .to(userExchange)
                .with(ROUTING_KEY_USER_REG);
    }

    @Bean
    public Binding bindingInitializedRoom(
            @Qualifier("roomQueue") Queue roomQueue,
            @Qualifier("roomExchange") TopicExchange roomExchange) {
        return BindingBuilder
                .bind(roomQueue)
                .to(roomExchange)
                .with(ROUTING_KEY_ROOM);
    }

    @Bean(name = FIGHT_COMMAND_QUEUE)
    public Queue fightCommandQueue(@org.springframework.beans.factory.annotation.Value("${fight.partition.current-slot:0}") int currentSlot) {
        return new Queue(fightCommandQueueName(currentSlot), true);
    }

    @Bean
    public Binding fightCommandBinding(
            @Qualifier(FIGHT_COMMAND_QUEUE) Queue fightCommandQueue,
            @Qualifier("fightCommandExchange") TopicExchange fightCommandExchange,
            @org.springframework.beans.factory.annotation.Value("${fight.partition.current-slot:0}") int currentSlot) {
        return BindingBuilder
                .bind(fightCommandQueue)
                .to(fightCommandExchange)
                .with(fightCommandRoutingKey(currentSlot));
    }

    // RabbitTemplate 

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

    public static String fightCommandRoutingKey(int slot) {
        return FIGHT_COMMAND_ROUTING_PREFIX + slot;
    }

    public static String fightCommandQueueName(int slot) {
        return FIGHT_COMMAND_QUEUE_PREFIX + slot + FIGHT_COMMAND_QUEUE_SUFFIX;
    }
}
