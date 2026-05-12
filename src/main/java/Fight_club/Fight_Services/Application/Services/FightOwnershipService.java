package Fight_club.Fight_Services.Application.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FightOwnershipService {

    @Value("${fight.partition.total-slots:1}")
    private int totalSlots;

    @Value("${fight.partition.current-slot:0}")
    private int currentSlot;

    public int slotForFight(String fightId) {
        return Math.floorMod(fightId.hashCode(), resolveTotalSlots());
    }

    public boolean isOwnedByCurrentNode(String fightId) {
        return slotForFight(fightId) == resolveCurrentSlot();
    }

    public int currentSlot() {
        return resolveCurrentSlot();
    }

    private int resolveCurrentSlot() {
        return Math.floorMod(currentSlot, resolveTotalSlots());
    }

    private int resolveTotalSlots() {
        return Math.max(totalSlots, 1);
    }
}
