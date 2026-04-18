package Fight_club.Fight_Services.Domain.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "fight_history")
public class FightRecord {

    @Id
    private String id;

    @Indexed
    private String userId;
    
    private String opponentId;
    private String opponentName;
    private String result;        
    private int pointsChange;
    private Instant fightDate;
}