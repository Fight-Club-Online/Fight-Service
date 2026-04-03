package Fight_club.Fight_Services.Infrastructure.Inbound;


import Fight_club.Fight_Services.Application.Ports.Input.ProcessCombatInputUseCase;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket.PlayerInputDto;
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
        public void handlePlayerInput(@DestinationVariable String fightId, @Payload PlayerInputDto input) {
        processCombatInputUseCase.handlePlayerInput(fightId,input.getUserId(), FighterAction.valueOf(input.getAction()));
    }

}
