package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Fight_club.Fight_Services.Application.Services.Fight.FightCommandRoutingService;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.FightCommandMessage;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.FightCommandType;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FightCommandListener Tests")
class FightCommandListenerTest {

    @Mock
    private FightCommandRoutingService fightCommandRoutingService;

    @InjectMocks
    private FightCommandListener fightCommandListener;

    private FightCommandMessage command;

    @BeforeEach
    void setUp() {
        command = new FightCommandMessage(
                FightCommandType.PLAYER_INPUT,
                "fight-1",
                "user-1",
                "BASIC_ATTACK",
                null,
                null
        );
    }

    @Test
    @DisplayName("Should route command to FightCommandRoutingService")
    void testOnCommand() {
        fightCommandListener.onCommand(command);

        verify(fightCommandRoutingService).execute(command);
    }

    @Test
    @DisplayName("Should handle null command")
    void testOnCommandNull() {
        fightCommandListener.onCommand(null);

        verify(fightCommandRoutingService).execute(null);
    }

    @Test
    @DisplayName("Should handle claim help button message")
    void testOnCommandClaimHelpButton() {
        FightCommandMessage claimCommand = new FightCommandMessage(
                FightCommandType.CLAIM_HELP,
                "fight-1",
                "user-1",
                null,
                null,
                null
        );

        fightCommandListener.onCommand(claimCommand);

        verify(fightCommandRoutingService).execute(claimCommand);
    }

    @Test
    @DisplayName("Should handle select fighter message")
    void testOnCommandSelectFighter() {
        FightCommandMessage selectCommand = new FightCommandMessage(
                FightCommandType.SELECT_FIGHTER,
                "fight-1",
                "user-1",
                null,
                "char-1",
                "Player1"
        );

        fightCommandListener.onCommand(selectCommand);

        verify(fightCommandRoutingService).execute(selectCommand);
    }

}
