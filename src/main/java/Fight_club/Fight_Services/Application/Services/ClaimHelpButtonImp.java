package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Input.ClaimHelpButtonUseCase;
import Fight_club.Fight_Services.Application.Ports.Output.CombatRepository;
import Fight_club.Fight_Services.Domain.models.*;
import Fight_club.Fight_Services.Domain.models.Enums.ButtomClaimedType;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ClaimHelpButtonImp implements ClaimHelpButtonUseCase {
    private final FightWebSocketUpdater fightWebSocketUpdater;
    private final CombatRepository combatRepository;
    private final FightLockManager lockManager;


    @Override
    public void claimHelpButton(String fightId, String userId) {
        Object lock = lockManager.getLock(fightId);
        synchronized (lock) {
            Fight fight = combatRepository.findById(fightId)
                    .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));

            HelpButton helpButton = fight.getHelpButton();
            if (helpButton.getClaimedByUserId() != null) {
                return;
            }
            helpButton.setClaimedByUserId(userId);

            if (fight.getSpectatorByUserId(userId).isPresent()) {
                Player p = fight.getSpectatorByUserId(userId).get();
                helpButton.setType(ButtomClaimedType.SPECTATOR);
                String userHelp = helpButton.getActivatedForUserId();
                Fighter f = fight.getFighterByUserId(userHelp);
                changePlayer(p,f);
            } else {
                Fighter f = fight.getFighterByUserId(userId);
                healOpponent(f);
                helpButton.setType(ButtomClaimedType.OPPONENT);
            }
            helpButton.setVisible(false);



            combatRepository.save(fight);
            Fight fightUpdated = combatRepository.findById(fightId).orElseThrow();
            fightWebSocketUpdater.changeFighters(fightId, fightUpdated);

        }
    }


    private void changePlayer(Player player,Fighter fighter){
        fighter.setUserId(player.getUserId());
        player.setPlayerType(PlayerType.HELPER);

    }

    private void healOpponent(Fighter fighter){
        Health health = fighter.getHealth();
        int he = health.getCurrentHealth();
        int maxHe = health.getMaxHealth();
        int fin = he + (int) (maxHe *0.25);
        health.setCurrentHealth(fin);



    }

}

