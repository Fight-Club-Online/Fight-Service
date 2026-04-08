package Fight_club.Fight_Services.Application.Ports.Input;

import Fight_club.Fight_Services.Domain.models.Fight;

public interface GetFightUseCase {
    Fight getFight(String fightId);

}
