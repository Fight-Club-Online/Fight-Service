package Fight_club.Fight_Services.Domain.Repository;

import Fight_club.Fight_Services.Domain.models.FightRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FightHistoryRepository extends MongoRepository<FightRecord, String> {
    List<FightRecord> findTop10ByUserIdOrderByFightDateDesc(String userId);
}