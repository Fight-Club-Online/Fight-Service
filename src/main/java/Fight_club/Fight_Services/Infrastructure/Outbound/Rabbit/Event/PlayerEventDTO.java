package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event;

import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PlayerEventDTO {
    private String userId;
    private String roomCode;
    private PlayerType playerType;
}
