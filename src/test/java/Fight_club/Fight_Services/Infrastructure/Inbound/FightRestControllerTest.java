package Fight_club.Fight_Services.Infrastructure.Inbound;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import Fight_club.Fight_Services.Application.Ports.Input.GetFightUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.StartFightUseCase;
import Fight_club.Fight_Services.Application.Services.Fight.FightHistoryService;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.FightRecord;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FightRestController Tests")
class FightRestControllerTest {

    @Mock
    private StartFightUseCase startFightUseCase;

    @Mock
    private GetFightUseCase getFightUseCase;

    @Mock
    private FightHistoryService fightHistoryService;

    @InjectMocks
    private FightRestController fightRestController;

    private Fight fight;
    private FightRecord fightRecord;

    @BeforeEach
    void setUp() {
        fight = Fight.builder().id("fight-1").active(true).build();
        fightRecord = FightRecord.builder().id("fight-1").userId("user-1").build();
    }

    @Test
    @DisplayName("Should start a fight and return it")
    void testStartFight() {
        when(startFightUseCase.startFight("fight-1")).thenReturn(fight);

        Fight result = fightRestController.startFight("fight-1");

        assert(result != null);
        assert(result.getId().equals("fight-1"));
        verify(startFightUseCase).startFight("fight-1");
    }

    @Test
    @DisplayName("Should get fight by ID")
    void testGetFight() {
        when(getFightUseCase.getFight("fight-1")).thenReturn(fight);

        Fight result = fightRestController.getFight("fight-1");

        assert(result != null);
        assert(result.getId().equals("fight-1"));
        verify(getFightUseCase).getFight("fight-1");
    }

    @Test
    @DisplayName("Should get fight history for user")
    void testGetFightHistory() {
        List<FightRecord> history = List.of(fightRecord);
        when(fightHistoryService.getHistory("user-1")).thenReturn(history);

        ResponseEntity<List<FightRecord>> result = fightRestController.getFightHistory("user-1");

        assert(result.getStatusCode() == HttpStatus.OK);
        assert(result.getBody() != null);
        assert(result.getBody().size() == 1);
        verify(fightHistoryService).getHistory("user-1");
    }

    @Test
    @DisplayName("Should return empty history when no fights found")
    void testGetFightHistoryEmpty() {
        when(fightHistoryService.getHistory("user-2")).thenReturn(List.of());

        ResponseEntity<List<FightRecord>> result = fightRestController.getFightHistory("user-2");

        assert(result.getStatusCode() == HttpStatus.OK);
        assert(result.getBody() != null);
        assert(result.getBody().isEmpty());
    }

    @Test
    @DisplayName("Should pass correct userId to history service")
    void testGetFightHistoryCorrectUserId() {
        when(fightHistoryService.getHistory("user-123")).thenReturn(List.of());

        fightRestController.getFightHistory("user-123");

        verify(fightHistoryService).getHistory("user-123");
    }

}
