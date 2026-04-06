package Fight_club.Fight_Services.Infrastructure.Inbound;

import Fight_club.Fight_Services.Application.Ports.Input.StartFightUseCase;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fight")
@AllArgsConstructor
public class FightRestController {

    private final StartFightUseCase startFightUseCase;

    @PutMapping("/start")
    public void startFight(String fightId){
        startFightUseCase.startFight(fightId);
    }
}
