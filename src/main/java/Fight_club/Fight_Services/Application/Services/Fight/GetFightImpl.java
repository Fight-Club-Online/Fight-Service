package Fight_club.Fight_Services.Application.Services.Fight;

import Fight_club.Fight_Services.Application.Ports.Input.GetFightUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fight;
import lombok.AllArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GetFightImpl implements GetFightUseCase {
    private final RedissonClient redisson;
    private final CombatRepository combatRepository;


    @Override
    public Fight getFight(String fightId) {
        return combatRepository.findById(fightId)
                .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));
    }
}
