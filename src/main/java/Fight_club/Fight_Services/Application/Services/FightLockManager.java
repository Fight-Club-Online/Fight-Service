package Fight_club.Fight_Services.Application.Services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FightLockManager {
    private final Map<String, Object> fightLocks = new ConcurrentHashMap<>();

    public Object getLock(String fightId) {
        return fightLocks.computeIfAbsent(fightId, k -> new Object());
    }
}
