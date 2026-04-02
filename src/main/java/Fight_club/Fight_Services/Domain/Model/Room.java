package Fight_club.Fight_Services.Domain.Model;

import Fight_club.Fight_Services.Domain.Model.Enums.RoomState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    private long roomId;
    private String roomCode;
    private RoomState roomState;
    private String hostId;
    private int maxPlayers;
    private int currentPlayers;
    private int maxSpectators;
    private int currentSpectators;
    private List<Player> players = new ArrayList<>();
}