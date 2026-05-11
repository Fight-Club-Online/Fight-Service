package Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO;

import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Domain.models.Health;

public record FigtherSocketDTO(
        String userId,
        Health health,
        int posX,
        int posY,
        boolean isGrounded,
        Direction direction,
        FighterAction currentAction,
        boolean isBlocking,
        int currentStunFrames
) {
}
