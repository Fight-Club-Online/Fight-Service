package Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.HelpButton;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO.Mappers.FightMapperDTO;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO.Mappers.FighterSocketMapperDTO;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FightWebSocketUpdater implements FightWsBroker {

    private final SimpMessagingTemplate messagingTemplate;
    private final FightMapperDTO fightMapperDTO;

    @Override
    public void fightStateUpdate(String fightId, Fight state) {

        messagingTemplate.convertAndSend(
                "/topic/fight." + fightId,
                fightMapperDTO.toFightSocketDTO(state));
    }

    @Override
    public void updateHelpButton(String fightId,HelpButton helpButton) {
        messagingTemplate.convertAndSend(
                "/topic/fight." + fightId + ".helpButton",
                helpButton);
        System.out.println("boton: " + helpButton);
    }

    @Override
    public void changeFighters(String fightId, Fight fight) {
        messagingTemplate.convertAndSend(
                "/topic/fight." + fightId + ".fighters",
                fight);
        System.out.println("Bton cambio: " + fight.getHelpButton());
    }

    @Override
    public void selectFighter(String fightId, Fight fight) {
        messagingTemplate.convertAndSend(
                "/topic/fight." + fightId + ".selected",
                fight);
        System.out.println(fightId);
    }
}
