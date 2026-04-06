package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event;

import Fight_club.Fight_Services.Domain.models.Enums.RoomState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class RoomInitializedEvent {
    private long roomId;
    private String roomCode;
    private RoomState roomState;
    private String hostId;
    private int maxPlayers;
    private int currentPlayers;
    private int maxSpectators;
    private int currentSpectators;
    private List<RoomPlayerEvent> players ;
}
