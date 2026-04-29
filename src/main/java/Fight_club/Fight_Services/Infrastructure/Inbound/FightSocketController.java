package Fight_club.Fight_Services.Infrastructure.Inbound;


import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import Fight_club.Fight_Services.Application.Ports.Input.AskHelpButtonUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.ClaimHelpButtonUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.ProcessCombatInputUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.SelectFighterUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.TakeBackControlUseCase;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket.PlayerInputDto;
import Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket.SelectFighterDTO;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class FightSocketController {
    private final ProcessCombatInputUseCase processCombatInputUseCase;
    private final ClaimHelpButtonUseCase claimHelpButtonUseCase;
    private final AskHelpButtonUseCase askHelpButtonUseCase;
    private final SelectFighterUseCase selectFighterUseCase;
    private final TakeBackControlUseCase takeBackControlUseCase;

    @MessageMapping("/fight/{fightId}/input")
        public void handlePlayerInput(@DestinationVariable String fightId, @Payload PlayerInputDto input) {
        processCombatInputUseCase.handlePlayerInput(fightId,input.getUserId(), FighterAction.valueOf(input.getAction()));
    }

    @MessageMapping("/fight/{fightId}/claim")
    public void claimHelpButton(@DestinationVariable String fightId, @Payload String userId) {
        claimHelpButtonUseCase.claimHelpButton(fightId,userId);
    }

    @MessageMapping("/fight/{fightId}/help")
    public void askHelpButton(@DestinationVariable String fightId, @Payload String userId) {
        askHelpButtonUseCase.askHelpButton(fightId,userId);
    }

    @MessageMapping("/fight/{fightId}/takeBack")
    public void takeBackFighter(@DestinationVariable String fightId, @Payload String userId) {
        takeBackControlUseCase.takeBackFigther(fightId,userId);
    }

    @MessageMapping("/fight/{fightId}/selectCharacter")
    public void selectFighter(@DestinationVariable String fightId, @Payload SelectFighterDTO sfDto) {
        String characterIdToUse = sfDto.getCharacterId() != null && !sfDto.getCharacterId().isEmpty() 
                ? sfDto.getCharacterId() 
                : sfDto.getUsername();
        
        selectFighterUseCase.selectFigther(fightId, sfDto.getUserId(), characterIdToUse);
    }

}
