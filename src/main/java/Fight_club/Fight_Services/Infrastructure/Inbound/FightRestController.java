package Fight_club.Fight_Services.Infrastructure.Inbound;

import Fight_club.Fight_Services.Application.Ports.Input.GetFightUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.StartFightUseCase;
import Fight_club.Fight_Services.Domain.models.Fight;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fight")
@AllArgsConstructor
public class FightRestController {

    private final StartFightUseCase startFightUseCase;
    private final GetFightUseCase getFightUseCase;

    @PutMapping("/start")
    public Fight startFight(String fightId){
        return startFightUseCase.startFight(fightId);
    }

    @GetMapping("/{fightId}")
    public Fight getFight(@PathVariable  String fightId){
        return getFightUseCase.getFight(fightId);
    }
}
