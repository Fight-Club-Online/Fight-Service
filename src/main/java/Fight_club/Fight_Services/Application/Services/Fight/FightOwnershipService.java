package Fight_club.Fight_Services.Application.Services.Fight;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FightOwnershipService {

    @Value("${fight.partition.total-slots:1}")
    private int totalSlots;

    @Value("${fight.partition.current-slot:0}")
    private int currentSlot;

    public int slotForFight(String fightId) {
        return Math.floorMod(fightId.hashCode(), totalSlots());
    }

    public boolean isOwnedByCurrentNode(String fightId) {
        return slotForFight(fightId) == currentSlot();
    }

    public int currentSlot() {

        return Math.floorMod(currentSlot, totalSlots());
    }

    private int totalSlots() {
        return Math.max(totalSlots, 1);
    }
}
