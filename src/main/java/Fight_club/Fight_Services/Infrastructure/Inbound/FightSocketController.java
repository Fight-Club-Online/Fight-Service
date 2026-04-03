package Fight_club.Fight_Services.Infrastructure.Inbound;


import Fight_club.Fight_Services.Application.Ports.Input.ProcessCombatInputUseCase;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class FightSocketController {
    private final ProcessCombatInputUseCase processCombatInputUseCase;


    @MessageMapping("/fight/{fightId}/input")
    public void handlePlayerInput(@DestinationVariable String fightId, String userId, @Payload String action) {
        processCombatInputUseCase.handlePlayerInput(fightId,userId, FighterAction.valueOf(action));
    }

}
