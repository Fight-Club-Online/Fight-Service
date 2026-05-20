package Fight_club.Fight_Services.Application.Services.Character;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;

import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO.UserCharacterDTO;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;

@ExtendWith(MockitoExtension.class)
@DisplayName("SelectFighterImpl Tests")
class SelectFighterImplTest {

    @Mock
    private FightWebSocketUpdater fightWebSocketUpdater;

    @Mock
    private CombatRepository combatRepository;

    @Mock
    private RedissonClient redisson;

    @Mock
    private GetUserCharacterByUserIdAndCharacterIdImp getUserCharacterService;

    @InjectMocks
    private SelectFighterImpl selectFighterImpl;

    private Fight fight;
    private Fighter fighter1;
    private Fighter fighter2;
    private UserCharacterDTO userCharacter;

    @BeforeEach
    void setUp() {
        fighter1 = Fighter.builder().userId("user-1").build();
        fighter2 = Fighter.builder().userId("user-2").build();

        fight = Fight.builder()
            .id("fight-1")
            .player1(fighter1)
            .player2(fighter2)
            .spectators(List.of())
            .build();

        userCharacter = new UserCharacterDTO();
        userCharacter.setCharacterId("char-1");
        userCharacter.setCharacterName("Warrior");
        userCharacter.setCharacterLevel(5);
        userCharacter.setCharacterATK(20);
        userCharacter.setCharacterDEF(10);
    }

    @Test
    @DisplayName("Should select fighter with character from database")
    void testSelectFighterWithCharacter() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.of(fight));
        when(getUserCharacterService.execute("user-1", "char-1")).thenReturn(Optional.of(userCharacter));

        selectFighterImpl.selectFigther("fight-1", "user-1", "char-1", "Player1");

        verify(combatRepository).save(fight);
        verify(fightWebSocketUpdater).selectFighter("fight-1", fight);
        assert(fighter1.getCharacterId().equals("char-1"));
        assert(fighter1.getCharacterName().equals("Warrior"));
    }

    @Test
    @DisplayName("Should select fighter with default character when not found in database")
    void testSelectFighterWithDefaultCharacter() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.of(fight));
        when(getUserCharacterService.execute("user-1", "char-1")).thenReturn(Optional.empty());

        selectFighterImpl.selectFigther("fight-1", "user-1", "char-1", "Player1");

        verify(combatRepository).save(fight);
        verify(fightWebSocketUpdater).selectFighter("fight-1", fight);
        assert(fighter1.getCharacterName().equals("char-1"));
    }

    @Test
    @DisplayName("Should position player one at left side")
    void testSelectFighterPositionPlayer1() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.of(fight));
        when(getUserCharacterService.execute("user-1", "char-1")).thenReturn(Optional.of(userCharacter));

        selectFighterImpl.selectFigther("fight-1", "user-1", "char-1", "Player1");

        assert(fighter1.getPosX() == 0);
        assert(fighter1.getPosY() == 280);
        assert(fighter1.getDirection() == Direction.RIGHT);
    }

    @Test
    @DisplayName("Should position player two at right side")
    void testSelectFighterPositionPlayer2() {
        fight = Fight.builder()
            .id("fight-1")
            .player1(fighter2)
            .player2(fighter1)
            .spectators(List.of())
            .build();
        when(combatRepository.findById("fight-1")).thenReturn(Optional.of(fight));
        when(getUserCharacterService.execute("user-1", "char-1")).thenReturn(Optional.of(userCharacter));

        selectFighterImpl.selectFigther("fight-1", "user-1", "char-1", "Player2");

        assert(fighter1.getPosX() == 750);
        assert(fighter1.getPosY() == 280);
        assert(fighter1.getDirection() == Direction.LEFT);
    }

    @Test
    @DisplayName("Should set health to 100 for all fighters")
    void testSelectFighterHealth() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.of(fight));
        when(getUserCharacterService.execute("user-1", "char-1")).thenReturn(Optional.of(userCharacter));

        selectFighterImpl.selectFigther("fight-1", "user-1", "char-1", "Player1");

        assert(fighter1.getHealth().getCurrentHealth() == 100);
        assert(fighter1.getHealth().getMaxHealth() == 100);
    }

    @Test
    @DisplayName("Should set initial state to IDLE")
    void testSelectFighterInitialState() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.of(fight));
        when(getUserCharacterService.execute("user-1", "char-1")).thenReturn(Optional.of(userCharacter));

        selectFighterImpl.selectFigther("fight-1", "user-1", "char-1", "Player1");

        assert(fighter1.getCurrentAction() == FighterAction.IDLE);
        assert(!fighter1.isBlocking());
    }

    @Test
    @DisplayName("Should throw exception when fight not found")
    void testSelectFighterFightNotFound() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.empty());

        try {
            selectFighterImpl.selectFigther("fight-1", "user-1", "char-1", "Player1");
        } catch (RuntimeException e) {
            assert(e.getMessage().contains("Fight not found"));
        }
    }

}
