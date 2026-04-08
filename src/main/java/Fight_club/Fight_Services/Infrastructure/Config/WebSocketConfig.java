package Fight_club.Fight_Services.Infrastructure.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // ruta q envia el back al front (escuchar)
        registry.setApplicationDestinationPrefixes("/fightService"); //ruta q envia el front al nack (enviar)
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
