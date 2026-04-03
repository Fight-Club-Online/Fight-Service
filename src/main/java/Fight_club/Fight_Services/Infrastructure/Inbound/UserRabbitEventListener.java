package Fight_club.Fight_Services.Infrastructure.Inbound;

import Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserRabbitEventListener {

    @RabbitListener(queues = RabbitConfig.QUEUE_USER_REGISTERED)
    public void handleUserRegistration(UserRegisteredEvent event) {
        log.info("Received User Registered Event: {} (ID: {})", 
                 event.getUsername(), event.getUserId());
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_GUEST_REGISTERED)
    public void handleGuestRegistration(GuestRegisteredEvent event) {
        log.info("Received Guest Registered Event: {} (ID: {})", 
                 event.getUsername(), event.getUserId());
    }

    @RabbitListener(queues = RabbitConfig.ROOM_QUEUE)
    public void handleRoomInitialization(Object roomEvent) {
        log.info("Received Room Event: {}", roomEvent);
    }
}