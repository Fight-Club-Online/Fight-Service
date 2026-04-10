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

    private final CombatRepository combatRepository;
    private final FightWsBroker fightWsBroker;

    public  static final ConcurrentHashMap<String, Fight> activeFights = new ConcurrentHashMap<>();


    private static final int MOVE_SPEED = 6;
    private static final int JUMP_SPEED = -16;
    private static final int GRAVITY = 1;
    private static final int GROUND_Y = 280;




    @Scheduled(fixedRate = 16)
    public void tick() {

        for (Fight fight : activeFights.values()) {
            if (!fight.isActive()) {
                continue;
            }

            boolean changed = false;

            changed |= applyPhysics(fight.getPlayer1());
            changed |= applyPhysics(fight.getPlayer2());

            if (changed) {
                combatRepository.save(fight);
                fightWsBroker.fightStateUpdate(fight.getId(), fight);
            }
        }
    }

    private boolean applyPhysics(Fighter fighter) {
        boolean changed = false;

        if (fighter == null || fighter.isDefeated()) {
            return false;
        }

        FighterAction action = fighter.getCurrentAction();

        switch (action) {
            case MOVE_LEFT -> {
                int newX = Math.max(0, fighter.getPosX() - MOVE_SPEED);
                fighter.setPosX(newX);
                fighter.setDirection(Direction.LEFT);
                changed = true;
            }
            case MOVE_RIGHT -> {
                int newX = Math.min(750, fighter.getPosX() + MOVE_SPEED);
                fighter.setPosX(newX);
                fighter.setDirection(Direction.RIGHT);
                changed = true;
            }
            case JUMP -> {
                fighter.setPosY(fighter.getPosY() - JUMP_SPEED);
                changed = true;
            }
            default -> {
            }
        }

        if (fighter.getPosY() < GROUND_Y) {
            fighter.setPosY(Math.min(GROUND_Y, fighter.getPosY() + GRAVITY));
            changed = true;
        }

        return changed;
    }

    public static void updateActiveFight(Fight fight) {
        activeFights.put(fight.getId(), fight);
    }
}