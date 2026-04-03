package Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.models.Fight;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FightWebSocketUpdater implements FightWsBroker {

    private final SimpMessagingTemplate messagingTemplate;


    @Override
    public void fightStateUpdate(String fightId, Fight state) {
        messagingTemplate.convertAndSend(
                "/topic/fight." + fightId,
                state);
    }
}
