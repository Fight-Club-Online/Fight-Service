package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class FightLoopService {

    private final CombatRepository combatRepository; // Solo lo usaremos para persistencia final
    private final FightWsBroker fightWsBroker;

    public static final ConcurrentHashMap<String, Fight> activeFights = new ConcurrentHashMap<>();

<<<<<<< HEAD
    private static final int MOVE_SPEED = 8;
    private static final int JUMP_SPEED = -16;
    private static final int GRAVITY = 1;
    private static final int GROUND_Y = 280;

    @Scheduled(fixedRate = 16)
=======
    
    private static final int MOVE_SPEED = 6;      
    private static final int JUMP_FORCE = -16;    
    private static final int GRAVITY = 1;         
    private static final int GROUND_Y = 280;      

    @Scheduled(fixedRate = 16) 
>>>>>>> e5775b62aac11778153794c51910f8e095b1151b
    public void tick() {
        for (Fight fight : activeFights.values()) {
            if (!fight.isActive()) continue;

            boolean p1Changed = applyPhysics(fight.getPlayer1());
            boolean p2Changed = applyPhysics(fight.getPlayer2());

            if (p1Changed || p2Changed) {
                fightWsBroker.fightStateUpdate(fight.getId(), fight);
            }
        }
    }

    private boolean applyPhysics(Fighter fighter) {
        if (fighter == null || fighter.isDefeated()) return false;
<<<<<<< HEAD
        
        boolean moved = false;
=======

        boolean changed = false;
>>>>>>> e5775b62aac11778153794c51910f8e095b1151b
        FighterAction action = fighter.getCurrentAction();

        if (fighter.getCurrentStunFrames() > 0) {
            fighter.setCurrentStunFrames(fighter.getCurrentStunFrames() - 1);
            return true; 
        }

        if (action == FighterAction.MOVE_LEFT) {
            fighter.setPosX(Math.max(0, fighter.getPosX() - MOVE_SPEED));
            fighter.setDirection(Direction.LEFT);
<<<<<<< HEAD
            moved = true;
        } else if (action == FighterAction.MOVE_RIGHT) {
            fighter.setPosX(Math.min(750, fighter.getPosX() + MOVE_SPEED));
            fighter.setDirection(Direction.RIGHT);
            moved = true;
=======
            changed = true;
        } else if (action == FighterAction.MOVE_RIGHT) {
            fighter.setPosX(Math.min(740, fighter.getPosX() + MOVE_SPEED));
            fighter.setDirection(Direction.RIGHT);
            changed = true;
        }

        if (action == FighterAction.JUMP && fighter.isGrounded()) {
            fighter.setVelocityY(JUMP_FORCE);
            fighter.setGrounded(false);
            fighter.setCurrentAction(FighterAction.IDLE);
            changed = true;
        }

        if (!fighter.isGrounded()) {
            fighter.setPosY(fighter.getPosY() + fighter.getVelocityY());
            fighter.setVelocityY(fighter.getVelocityY() + GRAVITY);
            changed = true;
        }

        if (fighter.getPosY() >= GROUND_Y) {
            fighter.setPosY(GROUND_Y);
            fighter.setVelocityY(0);
            fighter.setGrounded(true);
            changed = true;
>>>>>>> e5775b62aac11778153794c51910f8e095b1151b
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

    public static void updateActiveFight(Fight fight) {
        activeFights.put(fight.getId(), fight);
    }
}