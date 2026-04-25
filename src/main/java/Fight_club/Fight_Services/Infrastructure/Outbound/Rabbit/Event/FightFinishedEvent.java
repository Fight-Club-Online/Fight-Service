package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FightFinishedEvent {
    private String fightId;
    private String winnerUserId;   
    private String loserUserId; 
    private String result;         
    private int winnerPointsChange;
    private int loserPointsChange;
    private String winnerUsername; 
    private String loserUsername;  
}