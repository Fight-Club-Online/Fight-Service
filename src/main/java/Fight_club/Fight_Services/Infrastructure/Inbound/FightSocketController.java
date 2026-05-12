package Fight_club.Fight_Services.Infrastructure.Inbound;

import Fight_club.Fight_Services.Application.Services.Fight.FightCommandRoutingService;
import Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket.PlayerInputDto;
import Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket.SelectFighterDTO;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class FightSocketController {
    private final FightCommandRoutingService fightCommandRoutingService;

    @MessageMapping("/fight/{fightId}/input")
        public void handlePlayerInput(@DestinationVariable String fightId, @Payload PlayerInputDto input) {
        fightCommandRoutingService.handlePlayerInput(fightId, input.getUserId(), input.getAction());
    }

    @MessageMapping("/fight/{fightId}/claim")
    public void claimHelpButton(@DestinationVariable String fightId, @Payload String userId) {
        fightCommandRoutingService.claimHelpButton(fightId, userId);
    }

    @MessageMapping("/fight/{fightId}/help")
    public void askHelpButton(@DestinationVariable String fightId, @Payload String userId) {
        fightCommandRoutingService.askHelpButton(fightId, userId);
    }

    @MessageMapping("/fight/{fightId}/takeBack")
    public void takeBackFighter(@DestinationVariable String fightId, @Payload String userId) {
        fightCommandRoutingService.takeBackFighter(fightId, userId);
    }

    @MessageMapping("/fight/{fightId}/selectCharacter")
    public void selectFighter(@DestinationVariable String fightId, @Payload SelectFighterDTO sfDto) {
        String characterIdToUse = sfDto.getCharacterId() != null && !sfDto.getCharacterId().isEmpty() 
                ? sfDto.getCharacterId() 
                : sfDto.getUsername();
        
        fightCommandRoutingService.selectFighter(fightId, sfDto.getUserId(), characterIdToUse, sfDto.getUsername());
    }

}
