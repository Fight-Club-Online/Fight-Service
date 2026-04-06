package Fight_club.Fight_Services.Domain.Repository;

import java.util.Optional;
import Fight_club.Fight_Services.Domain.models.Fight; 

public interface CombatRepository {
    Optional<Fight> findById(String fightId);
    void save(Fight fight); 
    void delete(String fightId);
}