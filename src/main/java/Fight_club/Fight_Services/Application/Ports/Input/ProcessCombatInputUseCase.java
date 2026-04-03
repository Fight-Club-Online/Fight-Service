package Fight_club.Fight_Services.Application.Ports.Input;

import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;

public interface ProcessCombatInputUseCase {
    void handlePlayerInput(String fightId, String userId, FighterAction action);
}