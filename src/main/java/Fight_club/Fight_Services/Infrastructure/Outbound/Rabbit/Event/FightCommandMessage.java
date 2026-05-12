package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FightCommandMessage {
    private FightCommandType type;
    private String fightId;
    private String userId;
    private String action;
    private String characterId;
    private String username;
}
