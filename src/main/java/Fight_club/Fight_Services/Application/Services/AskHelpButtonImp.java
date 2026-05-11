package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Input.AskHelpButtonUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.models.Enums.ButtonStatus;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.HelpButton;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static Fight_club.Fight_Services.Application.Services.FightLoopService.updateActiveFight;
import static Fight_club.Fight_Services.Application.Services.LocksStrings.FIGHT_LOCK;

@Service
@AllArgsConstructor
public class AskHelpButtonImp implements AskHelpButtonUseCase {
    private final CombatRepository combatRepository;
    private final FightWsBroker fightWsBroker;
    private final RedissonClient redisson;



    @Override
    public void askHelpButton(String fightId, String userId) {
        RLock lock = redisson.getLock(FIGHT_LOCK + fightId);

        try{
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                Fight fight = combatRepository.findById(fightId)
                        .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));
                HelpButton helpButton = fight.getHelpButton();
                if(helpButton.getStatus() == ButtonStatus.CLAIMED || !helpButton.getClaimedByUserId().isEmpty()) return;

                helpButton.setVisible(true);
                combatRepository.save(fight);
                fightWsBroker.updateHelpButton(fightId, helpButton);
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
