package Fight_club.Fight_Services.Application.Ports.Output;


import Fight_club.Fight_Services.Domain.models.Fight;

public interface FightWsBroker {
    void fightStateUpdate(String fightId, Fight state);
}
