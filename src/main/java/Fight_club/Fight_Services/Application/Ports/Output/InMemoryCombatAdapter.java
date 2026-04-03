package Fight_club.Fight_Services.Application.Ports.Output;

import org.springframework.stereotype.Repository;
import Fight_club.Fight_Services.Domain.models.Fight;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCombatAdapter implements CombatRepository {
    
    
    private final Map<String, Fight> activeFights = new ConcurrentHashMap<>();

    @Override
    public Optional<Fight> findById(String id) {
        return Optional.ofNullable(activeFights.get(id));
    }

    @Override
    public void save(Fight fight) {
        activeFights.put(fight.getId(), fight);
    }

    @Override
    public void delete(String id) {
        activeFights.remove(id);
    }
}