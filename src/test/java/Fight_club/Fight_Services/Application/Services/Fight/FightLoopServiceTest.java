package Fight_club.Fight_Services.Application.Services.Fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Fight_club.Fight_Services.Application.Ports.Output.FightWsBroker;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Health;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FightLoopService Tests")
class FightLoopServiceTest {

    @Mock
    private CombatRepository combatRepository;

    @Mock
    private FightWsBroker fightWsBroker;

    @InjectMocks
    private FightLoopService fightLoopService;

    private Fight fight;
    private Fighter fighter1;
    private Fighter fighter2;

    @BeforeEach
    void setUp() {
        fighter1 = Fighter.builder()
                .userId("user-1")
                .hasCharacter(true)
                .health(new Health(100, 100))
                .currentAction(FighterAction.MOVE_RIGHT)
                .posX(0)
                .posY(280)
                .build();

        fighter2 = Fighter.builder()
                .userId("user-2")
                .hasCharacter(true)
                .health(new Health(100, 100))
                .currentAction(FighterAction.IDLE)
                .posX(100)
                .posY(280)
                .build();

        fight = Fight.builder()
                .id("fight-1")
                .player1(fighter1)
                .player2(fighter2)
                .active(true)
                .hasPendingUpdate(true)
                .spectators(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should update health state properly")
    void testGameLoopUpdatesHealth() {
        when(combatRepository.findActiveFightIdsForCurrentSlot())
                .thenReturn(List.of("fight-1"));

        when(combatRepository.findByIds(List.of("fight-1")))
                .thenReturn(List.of(fight));

        fightLoopService.tick();

        verify(fightWsBroker).fightStateUpdate("fight-1", fight);
    }

    @Test
    @DisplayName("Should handle empty active fights list")
    void testGameLoopWithNoActiveFights() {
        when(combatRepository.findActiveFightIdsForCurrentSlot())
                .thenReturn(new ArrayList<>());

        fightLoopService.tick();

        verify(fightWsBroker, never())
                .fightStateUpdate(anyString(), any(Fight.class));
    }
}