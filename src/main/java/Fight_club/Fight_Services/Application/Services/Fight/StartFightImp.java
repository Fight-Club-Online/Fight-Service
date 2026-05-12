package Fight_club.Fight_Services.Application.Services.Fight;

import Fight_club.Fight_Services.Application.Ports.Input.StartFightUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fight;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import static Fight_club.Fight_Services.Application.Services.Fight.LocksStrings.FIGHT_LOCK;

@Service
@AllArgsConstructor
public class StartFightImp implements StartFightUseCase {

    private final RedissonClient redisson;
    private final CombatRepository combatRepository;

    @Override
    public Fight startFight(String fightId) {
        RLock lock = redisson.getLock(FIGHT_LOCK + fightId);
        try{
            if (!lock.tryLock(2, 4, java.util.concurrent.TimeUnit.SECONDS)) {
                throw new RuntimeException("Failed to acquire lock for fight: " + fightId);
            }
            Fight fight = combatRepository.findById(fightId)
                    .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));

            if(fight.isActive()) return fight;  // Lombok genera isActive() automáticamente

            if(!fight.getPlayer1().getHealth().isAlive() || !fight.getPlayer2().getHealth().isAlive()){
                throw new RuntimeException("El usuario no esta vivo");
            }

            fight.setActive(true);
            fight.setHasPendingUpdate(true);
            combatRepository.save(fight);
            return fight;

        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to acquire lock for fight: " + fightId, e);
        }catch (Exception e){
            throw new RuntimeException("Failed to start fight: " + fightId, e);
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
