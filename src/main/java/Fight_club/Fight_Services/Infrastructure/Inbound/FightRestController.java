package Fight_club.Fight_Services.Infrastructure.Inbound;

import Fight_club.Fight_Services.Application.Ports.Input.GetFightUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.StartFightUseCase;
import Fight_club.Fight_Services.Application.Services.Fight.FightHistoryService;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.FightRecord;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fight")
@AllArgsConstructor
public class FightRestController {

    private final StartFightUseCase startFightUseCase;
    private final GetFightUseCase getFightUseCase;
    private final FightHistoryService fightHistoryService;

    @PutMapping("/start")
    public Fight startFight(String fightId){
        return startFightUseCase.startFight(fightId);
    }

    @GetMapping("/{fightId}")
    public Fight getFight(@PathVariable  String fightId){
        return getFightUseCase.getFight(fightId);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<FightRecord>> getFightHistory(@PathVariable String userId) {
        return ResponseEntity.ok(fightHistoryService.getHistory(userId));
    }
}
