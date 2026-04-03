package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import org.springframework.stereotype.Service;
import Fight_club.Fight_Services.Application.Ports.Input.ProcessCombatInputUseCase;
import Fight_club.Fight_Services.Application.Ports.Output.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Skill;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CombatService implements ProcessCombatInputUseCase {

    private final CombatRepository combatRepository;
    private final FightWsBroker fightWsBroker;
    private final Map<String,Object> fightLock = new ConcurrentHashMap<>();

    @Override
    public void handlePlayerInput(String fightId, String userId, FighterAction action) {
        Object lock = fightLock.computeIfAbsent(fightId, k -> new Object());

        synchronized (lock) {
            Fight fight = combatRepository.findById(fightId)
                    .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));

            Fighter attacker = fight.getFighterByUserId(userId);
            Fighter defender = fight.getOpponentOf(userId);

            attacker.executeAction(action);

            if (isAttackAction(action)) {
                Skill skillUsed = attacker.getSkillForAction(action);

                if (checkCollision(attacker, defender)) {
                    defender.receiveAttack(skillUsed);

                    if (defender.isDefeated()) {
                        handleMatchEnd(fightId, attacker.getUserId());
                    }
                }
            }

            combatRepository.save(fight);
            Fight fightUpdated = combatRepository.findById(fightId).orElseThrow();
            fightWsBroker.fightStateUpdate(fightId, fightUpdated);
        }

    }

    private boolean isAttackAction(FighterAction action) {
        return action == FighterAction.BASIC_ATTACK || action == FighterAction.SPECIAL_ATTACK;
    }

    private boolean checkCollision(Fighter a, Fighter b) {
        return Math.abs(a.getPosX() - b.getPosX()) < 50 && 
               Math.abs(a.getPosY() - b.getPosY()) < 20;
    }

    private void handleMatchEnd(String fightId, String winnerId) {
        System.out.println("Combate " + fightId + " finalizado. Ganador: " + winnerId);
    }
}