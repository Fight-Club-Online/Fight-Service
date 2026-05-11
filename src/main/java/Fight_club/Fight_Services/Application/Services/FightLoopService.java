package Fight_club.Fight_Services.Application.Services;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import lombok.RequiredArgsConstructor;

import static Fight_club.Fight_Services.Application.Services.LocksStrings.FIGHT_LOCK;

@Service
@RequiredArgsConstructor
public class FightLoopService {

    private final CombatRepository combatRepository;
    private final FightWsBroker fightWsBroker;
    private final RedissonClient redisson;

    private static final int MOVE_SPEED = 8;
    private static final int JUMP_SPEED = -16;
    private static final int GRAVITY = 1;
    private static final int GROUND_Y = 280;

    @Scheduled(fixedRate = 16)
    public void tick() {

        for (Fight cachedFight : combatRepository.findAll()) {
            String fightId = cachedFight.getId();
            RLock lock = redisson.getLock(FIGHT_LOCK + fightId);
            try {
                if (!lock.tryLock(1, 4, TimeUnit.SECONDS)) continue;

                Fight fight = combatRepository.findById(fightId).orElse(null);
                if (fight == null || !fight.isActive()) continue;

                boolean p1Changed = applyPhysics(fight.getPlayer1());
                boolean p2Changed = applyPhysics(fight.getPlayer2());

                if (p1Changed || p2Changed) {
                    combatRepository.save(fight);
                    fightWsBroker.fightStateUpdate(fightId, fight);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    private boolean applyPhysics(Fighter fighter) {
        if (fighter == null || fighter.isDefeated()) return false;
        
        boolean moved = false;
        FighterAction action = fighter.getCurrentAction();

        if (fighter.getCurrentStunFrames() > 0) {
            fighter.setCurrentStunFrames(fighter.getCurrentStunFrames() - 1);
            return true; 
        }

        if (action == FighterAction.MOVE_LEFT) {
            fighter.setPosX(Math.max(0, fighter.getPosX() - MOVE_SPEED));
            fighter.setDirection(Direction.LEFT);
            moved = true;
        } else if (action == FighterAction.MOVE_RIGHT) {
            fighter.setPosX(Math.min(750, fighter.getPosX() + MOVE_SPEED));
            fighter.setDirection(Direction.RIGHT);
            moved = true;
        }

        if (action == FighterAction.JUMP && fighter.isGrounded()) {
            fighter.setVelocityY(JUMP_SPEED);
            fighter.setGrounded(false);
            moved = true;
        }

        if (!fighter.isGrounded()) {
            fighter.setPosY(fighter.getPosY() + fighter.getVelocityY());
            fighter.setVelocityY(fighter.getVelocityY() + GRAVITY);
            moved = true;
        }

        if (fighter.getPosY() >= GROUND_Y) {
            fighter.setPosY(GROUND_Y);
            fighter.setVelocityY(0);
            fighter.setGrounded(true);
            moved = true;
        }

        return moved;
    }


}
