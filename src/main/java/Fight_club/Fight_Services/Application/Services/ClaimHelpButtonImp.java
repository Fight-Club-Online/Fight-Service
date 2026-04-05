package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Input.ClaimHelpButtonUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.*;
import Fight_club.Fight_Services.Domain.models.Enums.ButtomClaimedType;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static Fight_club.Fight_Services.Application.Services.LocksStrings.FIGHT_LOCK;
import static Fight_club.Fight_Services.Domain.models.Enums.PlayerType.SPECTATOR;

@Service
@AllArgsConstructor
public class ClaimHelpButtonImp implements ClaimHelpButtonUseCase {
    private final FightWebSocketUpdater fightWebSocketUpdater;
    private final CombatRepository combatRepository;
    private final RedissonClient redisson;




    @Override
    public void claimHelpButton(String fightId, String userId) {
        RLock lock = redisson.getLock(FIGHT_LOCK + fightId);

        try{
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
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
                    fight.addSpectator(changePlayer(p,f));
                } else {
                    Fighter f = fight.getFighterByUserId(userId);
                    healOpponent(f);
                    helpButton.setType(ButtomClaimedType.OPPONENT);
                }
                helpButton.setVisible(false);

                combatRepository.save(fight);
                fightWebSocketUpdater.changeFighters(fightId, fight);
            }

        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to acquire lock for fight: " + fightId, e);

        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }


    private Player changePlayer(Player player,Fighter fighter){
        fighter.setUserId(player.getUserId());
        player.setPlayerType(PlayerType.HELPER);
        return new Player(fighter.getUserId(),SPECTATOR);
    }

    private void healOpponent(Fighter fighter){
        Health health = fighter.getHealth();
        int he = health.getCurrentHealth();
        int maxHe = health.getMaxHealth();
        int fin = he + (int) (maxHe *0.25);
        health.setCurrentHealth(fin);



    }

}

