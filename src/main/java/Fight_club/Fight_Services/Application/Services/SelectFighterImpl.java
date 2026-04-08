package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Input.SelectFighterUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import lombok.AllArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SelectFighterImpl implements SelectFighterUseCase {
    private final FightWebSocketUpdater fightWebSocketUpdater;
    private final CombatRepository combatRepository;
    private final RedissonClient redisson;

    @Override
    public void selectFigther(String fightId, String userId, String figtherId) {
        Fight fight = combatRepository.findById(fightId)
                .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));



        //guardar con nuevos datos del pelad y otros
        combatRepository.save(fight);
        //para mandar al front via ws
        fightWebSocketUpdater.selectFighter(fightId, fight);
    }
}
