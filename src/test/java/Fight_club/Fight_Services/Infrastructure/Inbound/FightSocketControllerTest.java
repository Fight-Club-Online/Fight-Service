package Fight_club.Fight_Services.Infrastructure.Inbound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Fight_club.Fight_Services.Application.Services.Fight.FightCommandRoutingService;
import Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket.PlayerInputDto;
import Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket.SelectFighterDTO;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FightSocketController Tests")
class FightSocketControllerTest {

    @Mock
    private FightCommandRoutingService fightCommandRoutingService;

    @InjectMocks
    private FightSocketController fightSocketController;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should handle player input")
    void testHandlePlayerInput() {
        PlayerInputDto input = new PlayerInputDto();
        input.setUserId("user-1");
        input.setAction("BASIC_ATTACK");

        fightSocketController.handlePlayerInput("fight-1", input);

        verify(fightCommandRoutingService).handlePlayerInput("fight-1", "user-1", "BASIC_ATTACK");
    }

    @Test
    @DisplayName("Should handle claim help button")
    void testClaimHelpButton() {
        fightSocketController.claimHelpButton("fight-1", "user-1");

        verify(fightCommandRoutingService).claimHelpButton("fight-1", "user-1");
    }

    @Test
    @DisplayName("Should handle ask help button")
    void testAskHelpButton() {
        fightSocketController.askHelpButton("fight-1", "user-1");

        verify(fightCommandRoutingService).askHelpButton("fight-1", "user-1");
    }

    @Test
    @DisplayName("Should handle take back fighter")
    void testTakeBackFighter() {
        fightSocketController.takeBackFighter("fight-1", "user-1");

        verify(fightCommandRoutingService).takeBackFighter("fight-1", "user-1");
    }

    @Test
    @DisplayName("Should select fighter with character ID")
    void testSelectFighterWithCharacterId() {
        SelectFighterDTO sfDto = new SelectFighterDTO("user-1", "char-1", "Player1");

        fightSocketController.selectFighter("fight-1", sfDto);

        verify(fightCommandRoutingService).selectFighter("fight-1", "user-1", "char-1", "Player1");
    }

    @Test
    @DisplayName("Should select fighter with username when character ID is empty")
    void testSelectFighterWithoutCharacterId() {
        SelectFighterDTO sfDto = new SelectFighterDTO("user-1", "", "Player1");

        fightSocketController.selectFighter("fight-1", sfDto);

        verify(fightCommandRoutingService).selectFighter("fight-1", "user-1", "Player1", "Player1");
    }

    @Test
    @DisplayName("Should select fighter with username when character ID is null")
    void testSelectFighterNullCharacterId() {
        SelectFighterDTO sfDto = new SelectFighterDTO("user-1", null, "Player1");

        fightSocketController.selectFighter("fight-1", sfDto);

        verify(fightCommandRoutingService).selectFighter("fight-1", "user-1", "Player1", "Player1");
    }

    @Test
    @DisplayName("Should pass correct fight ID to routing service")
    void testHandlePlayerInputCorrectFightId() {
        PlayerInputDto input = new PlayerInputDto();
        input.setUserId("user-1");
        input.setAction("IDLE");

        fightSocketController.handlePlayerInput("fight-123", input);

        verify(fightCommandRoutingService).handlePlayerInput("fight-123", "user-1", "IDLE");
    }

}
