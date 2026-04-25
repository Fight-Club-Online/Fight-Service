package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.Services.ButtonEvent;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.FightEventProducer;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.FightFinishedEvent;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import Fight_club.Fight_Services.Application.Ports.Input.ProcessCombatInputUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.HelpButton;
import Fight_club.Fight_Services.Domain.models.Skill;
import Fight_club.Fight_Services.Domain.models.Enums.ButtonStatus;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Infrastructure.Outbound.Http.VoiceChatNotifier;
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
    private final VoiceChatNotifier voiceChatNotifier;
    private final FightEventProducer fightEventProducer; 

    @Override
    public void handlePlayerInput(String fightId, String userId, FighterAction action) {
        Fight fight = FightLoopService.activeFights.get(fightId);
        
        if (fight == null) {
            fight = combatRepository.findById(fightId)
                    .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));
            updateActiveFight(fight);
        }

        if (!fight.isActive()) return;

        Fighter attacker = fight.getFighterByUserId(userId);
        Fighter defender = fight.getOpponentOf(userId);

        attacker.setCurrentAction(action);

        if (isAttackAction(action)) {
            RLock lock = redisson.getLock(FIGHT_LOCK + fightId);
            try {
                if (lock.tryLock(2, 4, TimeUnit.SECONDS)) {
                    
                    Fight freshFight = combatRepository.findById(fightId)
                    .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));
                    if (!freshFight.isActive()) return;

                    Skill skillUsed = attacker.getSkillForAction(action);

                    if (checkCollision(attacker, defender)) {
                        defender.receiveAttack(skillUsed);
                        defender.setCurrentAction(FighterAction.HURT);

                        if (defender.isDefeated()) {
                            fight.finishFight();
                            handleMatchEnd(fightId, fight, attacker, defender);
                        }

                        be.activate(fight.getHelpButton(), 
                                   defender.getHealth().getCurrentHealth(), 
                                   defender.getHealth().getMaxHealth(), 
                                   defender.getUserId());
                    }
                    combatRepository.save(fight);
                    fightWsBroker.fightStateUpdate(fightId, fight);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lock.isHeldByCurrentThread()) lock.unlock();
            }
        }
        updateActiveFight(fight);
    }

    private void handleMatchEnd(String fightId, Fight fight,
                                Fighter winner, Fighter loser) {
        voiceChatNotifier.notifyFightFinished(fightId);

        boolean draw = winner.getHealth().getCurrentHealth() <= 0
                    && loser.getHealth().getCurrentHealth() <= 0;

        String realWinnerId = resolveRealUserId(fight, winner.getUserId());
        String realLoserId  = resolveRealUserId(fight, loser.getUserId());

        String realWinnerName = winner.getCharacterName();
        String realLoserName  = loser.getCharacterName();

        FightFinishedEvent event;

        if (draw) {
            event = FightFinishedEvent.builder()
                    .fightId(fightId)
                    .winnerUserId(realWinnerId)
                    .loserUserId(realLoserId)
                    .winnerUsername(realWinnerName)  
                    .loserUsername(realLoserName)    
                    .result("DRAW")
                    .winnerPointsChange(5)
                    .loserPointsChange(5)
                    .build();
        } else {
            event = FightFinishedEvent.builder()
                    .fightId(fightId)
                    .winnerUserId(realWinnerId)
                    .loserUserId(realLoserId)
                    .winnerUsername(realWinnerName)   
                    .loserUsername(realLoserName)     
                    .result("WIN_LOSE")
                    .winnerPointsChange(28)
                    .loserPointsChange(-12)
                    .build();
        }

        fightEventProducer.publishFightFinished(event);
    }

    private String resolveRealUserId(Fight fight, String currentUserId) {
        HelpButton btn = fight.getHelpButton();
        if (btn != null
                && btn.getStatus() == ButtonStatus.CLAIMED
                && currentUserId.equals(btn.getClaimedByUserId())) {
            return btn.getActivatedForUserId();
        }
        return currentUserId;
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
}