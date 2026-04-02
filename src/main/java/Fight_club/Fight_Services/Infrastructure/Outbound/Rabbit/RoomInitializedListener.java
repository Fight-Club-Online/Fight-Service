package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit;

import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.RoomInitializedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig.ROOM_QUEUE;

@Component
@Slf4j
public class RoomInitializedListener {

    @RabbitListener(queues = ROOM_QUEUE)
    public void handleRoomInitialized(RoomInitializedEvent roomInitializedEvent) {
        log.info("Received message: {}", roomInitializedEvent);
    }
}
