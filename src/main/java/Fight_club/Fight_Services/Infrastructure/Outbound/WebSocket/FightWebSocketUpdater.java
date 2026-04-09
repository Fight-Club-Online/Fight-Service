package Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.HelpButton;
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

    @Override
    public void updateHelpButton(String fightId,HelpButton helpButton) {
        messagingTemplate.convertAndSend(
                "/topic/fight." + fightId,
                helpButton);
        System.out.println("boton: " + helpButton);
    }

    @Override
    public void changeFighters(String fightId, Fight fight) {
        messagingTemplate.convertAndSend(
                "/topic/fight." + fightId,
                fight);
        System.out.println("Bton cambio: " + fight.getHelpButton());
    }

    @Override
    public void selectFighter(String fightId, Fight fight) {
        messagingTemplate.convertAndSend(
                "/topic/fight." + fightId,
                fight);
        System.out.println(fightId);
    }
}
