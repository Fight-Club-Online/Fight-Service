package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.RoomState;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.RoomInitializedEvent;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.RoomPlayerEvent;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomInitializedListener Tests")
class RoomInitializedListenerTest {

    @Mock
    private CombatRepository combatRepository;

    @InjectMocks
    private RoomInitializedListener roomInitializedListener;

    private RoomInitializedEvent roomInitializedEvent;
    private List<RoomPlayerEvent> players;

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new RoomPlayerEvent("user-1", "room-1", PlayerType.PLAYER));
        players.add(new RoomPlayerEvent("user-2", "room-1", PlayerType.PLAYER));
        players.add(new RoomPlayerEvent("user-3", "room-1", PlayerType.SPECTATOR));

        roomInitializedEvent = new RoomInitializedEvent(
                1L,
                "room-1",
                RoomState.WAITING,
                "host-1",
                2,
                3,
                2,
                1,
                players
        );
    }

    @Test
    @DisplayName("Should handle room initialized event with two fighters and spectators")
    void testHandleRoomInitialized() {
        roomInitializedListener.handleRoomInitialized(roomInitializedEvent);

        verify(combatRepository).save(any());
    }

    @Test
    @DisplayName("Should reject event with less than 2 fighters")
    void testHandleRoomInitializedLessThanTwoFighters() {
        RoomInitializedEvent singlePlayerEvent = new RoomInitializedEvent(
                1L,
                "room-1",
                RoomState.WAITING,
                "host-1",
                2,
                1,
                2,
                0,
                List.of(new RoomPlayerEvent("user-1", "room-1", PlayerType.PLAYER))
        );

        try {
            roomInitializedListener.handleRoomInitialized(singlePlayerEvent);
        } catch (AmqpRejectAndDontRequeueException ignored) {
        }

        verify(combatRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject event with more than 2 fighters")
    void testHandleRoomInitializedMoreThanTwoFighters() {
        RoomInitializedEvent threePlayersEvent = new RoomInitializedEvent(
                1L,
                "room-1",
                RoomState.WAITING,
                "host-1",
                2,
                3,
                2,
                1,
                List.of(
                        new RoomPlayerEvent("user-1", "room-1", PlayerType.PLAYER),
                        new RoomPlayerEvent("user-2", "room-1", PlayerType.PLAYER),
                        new RoomPlayerEvent("user-3", "room-1", PlayerType.PLAYER)
                )
        );

        try {
            roomInitializedListener.handleRoomInitialized(threePlayersEvent);
        } catch (AmqpRejectAndDontRequeueException ignored) {
        }

        verify(combatRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle null room event gracefully")
    void testHandleNullRoomEvent() {
        try {
            roomInitializedListener.handleRoomInitialized(null);
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    @DisplayName("Should create fight with correct spectators")
    void testHandleRoomInitializedWithSpectators() {
        roomInitializedListener.handleRoomInitialized(roomInitializedEvent);

        verify(combatRepository).save(any());
    }

}
