package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Domain.Repository.FightHistoryRepository;
import Fight_club.Fight_Services.Domain.models.FightRecord;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.FightFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightHistoryService {

    private final FightHistoryRepository repository;

    public void saveFightResult(FightFinishedEvent event) {
        repository.save(FightRecord.builder()
                .userId(event.getWinnerUserId())
                .opponentId(event.getLoserUserId())
                .result("VICTORIA")
                .pointsChange(event.getWinnerPointsChange())
                .fightDate(Instant.now())
                .build());

        repository.save(FightRecord.builder()
                .userId(event.getLoserUserId())
                .opponentId(event.getWinnerUserId())
                .result("DERROTA")
                .pointsChange(event.getLoserPointsChange())
                .fightDate(Instant.now())
                .build());

        log.info("[HISTORY] Pelea guardada: winner={} loser={}",
                event.getWinnerUserId(), event.getLoserUserId());
    }

    public List<FightRecord> getHistory(String userId) {
        return repository.findTop10ByUserIdOrderByFightDateDesc(userId);
    }
}