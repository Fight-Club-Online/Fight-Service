package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.Mapper;

import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import Fight_club.Fight_Services.Domain.models.Player;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.RoomPlayerEvent;

public class ToPlayer {

    public static Player toPlayer(RoomPlayerEvent roomPlayerEvent) {
        if(roomPlayerEvent.getPlayerType() == PlayerType.PLAYER){
            return null;
        }
        return Player.builder().userId(roomPlayerEvent.getUserId()).playerType(roomPlayerEvent.getPlayerType()).build();
    }
}
