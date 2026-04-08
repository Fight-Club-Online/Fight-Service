package Fight_club.Fight_Services.Application.Ports.Output;


import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.HelpButton;

public interface FightWsBroker {
    void fightStateUpdate(String fightId, Fight state);
    void updateHelpButton(String fightId, HelpButton button);
    void changeFighters(String fightId, Fight fight);
    void selectFighter(String fightId,Fight fight);
}
