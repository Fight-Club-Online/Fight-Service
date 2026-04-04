package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Input.AskHelpButtonUseCase;
import Fight_club.Fight_Services.Application.Ports.Output.CombatRepository;
import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.HelpButton;
import Fight_club.Fight_Services.Domain.models.Skill;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AskHelpButtonImp implements AskHelpButtonUseCase {
    private final CombatRepository combatRepository;
    private final FightWsBroker fightWsBroker;
    private final FightLockManager lockManager;

    @Override
    public void askHelpButton(String fightId, String userId) {
        Object lock = lockManager.getLock(fightId);
        synchronized (lock) {
            Fight fight = combatRepository.findById(fightId)
                    .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));

            HelpButton helpButton = fight.getHelpButton();
            helpButton.setVisible(true);
            combatRepository.save(fight);
            fightWsBroker.updateHelpButton(fightId, helpButton);
        }
    }
}
