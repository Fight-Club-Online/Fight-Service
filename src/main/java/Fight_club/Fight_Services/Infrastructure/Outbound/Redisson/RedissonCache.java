package Fight_club.Fight_Services.Infrastructure.Outbound.Redisson;

import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fight;
import lombok.AllArgsConstructor;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class RedissonCache implements CombatRepository {

    private final RedissonClient redisson;
    private static final String CACHE_NAME = "fightsMap";


    @Override
    public Optional<Fight> findById(String fightId) {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        return Optional.ofNullable(fightsMap.get(fightId));
    }

    @Override
    public void save(Fight fight) {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        fightsMap.put(fight.getId(), fight, 60, TimeUnit.MINUTES);
    }

    @Override
    public void delete(String fightId) {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        fightsMap.remove(fightId);
    }
}
