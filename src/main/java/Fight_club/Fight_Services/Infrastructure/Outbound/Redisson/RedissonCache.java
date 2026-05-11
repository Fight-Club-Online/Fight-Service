package Fight_club.Fight_Services.Infrastructure.Outbound.Redisson;

import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fight;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedissonCache implements CombatRepository {

    private final RedissonClient redisson;

    private static final String CACHE_NAME = "fightsMap";
    private static final String ACTIVE_FIGHTS_INDEX = "activeFightIds";
    private static final String SLOT_KEY_PREFIX = "fights:slot:";

    @Value("${fight.partition.total-slots:1}")
    private int totalSlots;

    @Value("${fight.partition.current-slot:0}")
    private int currentSlot;

    @Override
    public Optional<Fight> findById(String fightId) {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        return Optional.ofNullable(fightsMap.get(fightId));
    }

    @Override
    public void save(Fight fight) {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        fightsMap.put(fight.getId(), fight, 60, TimeUnit.MINUTES);

        redisson.getSet(slotKey(resolveSlot(fight.getId()))).add(fight.getId());

        RSet<String> activeFightIds = redisson.getSet(ACTIVE_FIGHTS_INDEX);
        if (fight.isActive()) {
            activeFightIds.add(fight.getId());
        } else {
            activeFightIds.remove(fight.getId());
        }
    }

    @Override
    public void delete(String fightId) {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        fightsMap.remove(fightId);
        redisson.getSet(ACTIVE_FIGHTS_INDEX).remove(fightId);
        redisson.getSet(slotKey(resolveSlot(fightId))).remove(fightId);
    }

    @Override
    public List<Fight> findAll() {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        return new ArrayList<>(fightsMap.values());
    }

    @Override
    public List<String> findActiveFightIdsForCurrentSlot() {
        RSet<String> slotFightIds = redisson.getSet(slotKey(resolveCurrentSlot()));
        RSet<String> activeFightIds = redisson.getSet(ACTIVE_FIGHTS_INDEX);
        HashSet<String> intersection = new HashSet<>(slotFightIds.readAll());
        intersection.retainAll(activeFightIds.readAll());
        return new ArrayList<>(intersection);
    }

    private int resolveSlot(String fightId) {
        return Math.floorMod(fightId.hashCode(), resolveTotalSlots());
    }

    private int resolveCurrentSlot() {
        return Math.floorMod(currentSlot, resolveTotalSlots());
    }

    private int resolveTotalSlots() {
        return Math.max(totalSlots, 1);
    }

    private String slotKey(int slot) {
        return SLOT_KEY_PREFIX + slot;
    }
}
