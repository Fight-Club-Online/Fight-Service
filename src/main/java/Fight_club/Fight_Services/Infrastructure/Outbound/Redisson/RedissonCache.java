package Fight_club.Fight_Services.Infrastructure.Outbound.Redisson;

import Fight_club.Fight_Services.Application.Services.FightOwnershipService;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fight;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedissonCache implements CombatRepository {

    private final RedissonClient redisson;
    private final FightOwnershipService fightOwnershipService;

    private static final String CACHE_NAME = "fightsMap";
    private static final String ACTIVE_FIGHTS_INDEX = "activeFightIds";
    private static final String SLOT_KEY_PREFIX = "fights:slot:";
    private final Map<String, Fight> localOwnedFights = new ConcurrentHashMap<>();
    private final Set<String> localActiveFightIds = ConcurrentHashMap.newKeySet();
    private final Set<String> dirtyOwnedFightIds = ConcurrentHashMap.newKeySet();
    private final Object dirtyOwnedLock = new Object();

    @Override
    public Optional<Fight> findById(String fightId) {
        Fight localFight = localOwnedFights.get(fightId);
        if (localFight != null) {
            return Optional.of(localFight);
        }

        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        Fight remoteFight = fightsMap.get(fightId);
        if (remoteFight != null && fightOwnershipService.isOwnedByCurrentNode(fightId)) {
            cacheOwnedFight(remoteFight);
        }
        return Optional.ofNullable(remoteFight);
    }

    @Override
    public List<Fight> findByIds(List<String> fightIds) {
        if (fightIds == null || fightIds.isEmpty()) return List.of();
        return fightIds.stream()
                .map(this::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Fight fight) {
        if (fightOwnershipService.isOwnedByCurrentNode(fight.getId())) {
            synchronized (dirtyOwnedLock) {
                cacheOwnedFight(fight);
                dirtyOwnedFightIds.add(fight.getId());
            }
        } else {
            writeThroughRemote(fight);
        }
    }

    @Override
    public void delete(String fightId) {
        localOwnedFights.remove(fightId);
        localActiveFightIds.remove(fightId);
        dirtyOwnedFightIds.remove(fightId);
        removeRemote(fightId);
    }

    @Override
    public List<Fight> findAll() {
        if (!localOwnedFights.isEmpty()) {
            return new ArrayList<>(localOwnedFights.values());
        }
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        return fightsMap.values().stream()
                .filter(fight -> fightOwnershipService.isOwnedByCurrentNode(fight.getId()))
                .peek(this::cacheOwnedFight)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findActiveFightIdsForCurrentSlot() {
        if (!localActiveFightIds.isEmpty()) {
            return new ArrayList<>(localActiveFightIds);
        }

        RSet<String> slotFightIds = redisson.getSet(slotKey(fightOwnershipService.currentSlot()));
        RSet<String> activeFightIds = redisson.getSet(ACTIVE_FIGHTS_INDEX);
        HashSet<String> intersection = new HashSet<>(slotFightIds.readAll());
        intersection.retainAll(activeFightIds.readAll());
        intersection.forEach(this::findById);
        return new ArrayList<>(intersection);
    }

    @Scheduled(fixedDelayString = "${fight.snapshot.fixed-delay-ms:250}")
    public void flushOwnedSnapshots() {
        synchronized (dirtyOwnedLock) {
            if (dirtyOwnedFightIds.isEmpty()) return;
            Set<String> ids = new HashSet<>(dirtyOwnedFightIds);
            for (String fightId : ids) {
                if (!dirtyOwnedFightIds.remove(fightId)) {
                    continue;
                }
                try {
                    Fight fight = localOwnedFights.get(fightId);
                    if (fight != null) {
                        writeThroughRemote(fight);
                    } else {
                        removeRemote(fightId);
                    }
                } catch (Exception ex) {
                    dirtyOwnedFightIds.add(fightId);
                    log.error("Failed to flush snapshot for fight {}", fightId, ex);
                }
            }
        }
    }

    private void cacheOwnedFight(Fight fight) {
        localOwnedFights.put(fight.getId(), fight);
        if (fight.isActive()) {
            localActiveFightIds.add(fight.getId());
        } else {
            localActiveFightIds.remove(fight.getId());
        }
    }

    private void writeThroughRemote(Fight fight) {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        fightsMap.put(fight.getId(), fight, 60, TimeUnit.MINUTES);
        redisson.getSet(slotKey(fightOwnershipService.slotForFight(fight.getId()))).add(fight.getId());
        RSet<String> activeFightIds = redisson.getSet(ACTIVE_FIGHTS_INDEX);
        if (fight.isActive()) {
            activeFightIds.add(fight.getId());
        } else {
            activeFightIds.remove(fight.getId());
        }
    }

    private void removeRemote(String fightId) {
        RMapCache<String, Fight> fightsMap = redisson.getMapCache(CACHE_NAME);
        fightsMap.remove(fightId);
        redisson.getSet(ACTIVE_FIGHTS_INDEX).remove(fightId);
        redisson.getSet(slotKey(fightOwnershipService.slotForFight(fightId))).remove(fightId);
    }

    private String slotKey(int slot) {
        return SLOT_KEY_PREFIX + slot;
    }
}
