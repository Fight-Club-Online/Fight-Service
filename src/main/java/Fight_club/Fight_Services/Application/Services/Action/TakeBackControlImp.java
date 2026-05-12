package Fight_club.Fight_Services.Application.Services.Action;

import Fight_club.Fight_Services.Application.Ports.Input.TakeBackControlUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.HelpButton;
import Fight_club.Fight_Services.Domain.models.Player;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static Fight_club.Fight_Services.Application.Services.Fight.LocksStrings.FIGHT_LOCK;

@Service
@AllArgsConstructor
public class TakeBackControlImp implements TakeBackControlUseCase {
    private final FightWebSocketUpdater fightWebSocketUpdater;
    private final CombatRepository combatRepository;
    private final RedissonClient redisson;


    @Override
    public void takeBackFigther(String fightId, String userId) {
        RLock lock = redisson.getLock(FIGHT_LOCK + fightId);

        try{
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
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
                fightWebSocketUpdater.changeFighters(fightId, fight);
                combatRepository.save(fight);
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
}
