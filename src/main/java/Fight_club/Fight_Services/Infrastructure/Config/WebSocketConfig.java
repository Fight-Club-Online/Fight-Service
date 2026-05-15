package Fight_club.Fight_Services.Infrastructure.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${stomp.relay.host}")
    private String relayHost;

    @Value("${stomp.relay.port}")
    private int relayPort;

    @Value("${stomp.relay.username}")
    private String relayUsername;

    @Value("${stomp.relay.password}")
    private String relayPassword;

    @Value("${stomp.relay.virtual-host}")
    private String relayVirtualHost;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic", "/queue") // broker compartido via RabbitMQ STOMP
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                .setSystemLogin(relayUsername)
                .setSystemPasscode(relayPassword)
                .setClientLogin(relayUsername)
                .setClientPasscode(relayPassword)
                .setVirtualHost(relayVirtualHost);
        registry.setApplicationDestinationPrefixes("/fightService"); //ruta q envia el front al back (enviar)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/fightService")
                .setAllowedOriginPatterns(
                    "http://localhost:5173",
                    "https://lamentaciones-frontend-juan-caballeros-projects.vercel.app/",
                     "*"
                )
                .withSockJS();
    }

}
