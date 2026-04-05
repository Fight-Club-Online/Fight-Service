package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Input.TakeBackControlUseCase;
import Fight_club.Fight_Services.Application.Ports.Output.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.ButtomClaimedType;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.HelpButton;
import Fight_club.Fight_Services.Domain.models.Player;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TakeBackControlImp implements TakeBackControlUseCase {
    private final FightWebSocketUpdater fightWebSocketUpdater;
    private final CombatRepository combatRepository;
    private final FightLockManager lockManager;


    @Override
    public void takeBackFigther(String fightId, String userId) {
        Object lock = lockManager.getLock(fightId);
        synchronized (lock) {
            Fight fight = combatRepository.findById(fightId)
                    .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));

            HelpButton helpButton = fight.getHelpButton();
            String originalFigtherId = helpButton.getActivatedForUserId();
            String helperId = helpButton.getClaimedByUserId();

            if(fight.getSpectatorByUserId(helperId).isPresent()){
                Player p = fight.getSpectatorByUserId(helperId).get();
                p.setPlayerType(PlayerType.SPECTATOR);
            }
            if(fight.getSpectatorByUserId(originalFigtherId).isPresent()){
                Player p = fight.getSpectatorByUserId(originalFigtherId).get();
                fight.removeSpectator(p);
            }

            Fighter originalFigther = fight.getFighterByUserId(helperId);
            originalFigther.setUserId(originalFigtherId);
            helpButton.deactivate();
            combatRepository.save(fight);
            Fight fightUpdated = combatRepository.findById(fightId).orElseThrow();
            fightWebSocketUpdater.changeFighters(fightId, fightUpdated);}
    }
}
