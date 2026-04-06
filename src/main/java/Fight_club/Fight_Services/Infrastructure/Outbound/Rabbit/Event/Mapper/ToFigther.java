package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.Mapper;

import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.RoomPlayerEvent;

import java.util.concurrent.ThreadLocalRandom;

public class ToFigther {
    public static Fighter toFighter(RoomPlayerEvent roomPlayerEvent) {
        long random = ThreadLocalRandom.current().nextLong();
        return Fighter.builder()
                .id(String.valueOf(random))
                .userId(roomPlayerEvent.getUserId())
                .build();
    }
}
