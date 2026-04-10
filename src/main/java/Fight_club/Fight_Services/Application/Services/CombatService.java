package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.Services.ButtonEvent;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import Fight_club.Fight_Services.Application.Ports.Input.ProcessCombatInputUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Skill;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

import static Fight_club.Fight_Services.Application.Services.FightLoopService.updateActiveFight;
import static Fight_club.Fight_Services.Application.Services.LocksStrings.FIGHT_LOCK;
@Service
@RequiredArgsConstructor
public class CombatService implements ProcessCombatInputUseCase {

    private final CombatRepository combatRepository;
    private final FightWsBroker fightWsBroker;
    private final RedissonClient redisson;

    @Override
    public void handlePlayerInput(String fightId, String userId, FighterAction action) {
        RLock lock = redisson.getLock(FIGHT_LOCK + fightId);

        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) { 
                
                Fight fight = FightLoopService.activeFights.get(fightId);

                if (fight == null) {
                    fight = combatRepository.findById(fightId).orElse(null);
                    if (fight == null) {
                        System.err.println("Pelea no encontrada en el sistema: " + fightId);
                        return; 
                    }
                }

                Fighter attacker = fight.getFighterByUserId(userId);
                Fighter defender = fight.getOpponentOf(userId);
                
                if (attacker == null || defender == null) return;

                attacker.executeAction(action);

                if (isAttackAction(action)) {
                    Skill skillUsed = attacker.getSkillForAction(action);

                    if (checkCollision(attacker, defender)) {
                        defender.receiveAttack(skillUsed);
                        defender.setCurrentAction(FighterAction.HURT);

                        if (defender.isDefeated()) {
                            fight.finishFight();
                            handleMatchEnd(fightId, attacker.getUserId());
                        }

                        if (fight.getHelpButton() != null) {
                            be.activate(
                                    fight.getHelpButton(),
                                    defender.getHealth().getCurrentHealth(),
                                    defender.getHealth().getMaxHealth(),
                                    defender.getUserId()
                            );
                        }
                        
                        fightWsBroker.fightStateUpdate(fightId, fight);
                    }
                }

                combatRepository.save(fight); 
                updateActiveFight(fight);
                
                fightWsBroker.fightStateUpdate(fightId, fight);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock interrumpido para la pelea: " + fightId, e);
        } catch (Exception e) {
            System.err.println("Error procesando input de combate: " + e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    ButtonEvent be = (btn, health, maxHe, userId) -> {
        if (health <= maxHe * 3 / 4) {
            btn.activate(userId);
        }
    };



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