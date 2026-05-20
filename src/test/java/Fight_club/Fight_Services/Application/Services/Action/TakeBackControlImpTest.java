package Fight_club.Fight_Services.Application.Services.Action;
 
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.*;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
 
import java.util.Optional;
import java.util.concurrent.TimeUnit;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
@DisplayName("TakeBackControlImp Tests")
class TakeBackControlImpTest {
 
    @Mock private FightWebSocketUpdater fightWebSocketUpdater;
    @Mock private CombatRepository combatRepository;
    @Mock private RedissonClient redisson;
    @Mock private RLock lock;
    @Mock private Fight fight;
    @Mock private HelpButton helpButton;
    @Mock private Fighter originalFighter;
    @Mock private Player helperAsSpectator;
    @Mock private Player originalAsSpectator;
 
    @InjectMocks
    private TakeBackControlImp takeBackControlImp;
 
    private static final String FIGHT_ID      = "fight-789";
    private static final String USER_ID       = "original-user";
    private static final String HELPER_ID     = "helper-user";
    private static final String ORIGINAL_ID   = "original-user";
 
    @BeforeEach
    void setUp() {
        when(redisson.getLock(anyString())).thenReturn(lock);
    }
 
    // ─── Lock not acquired ────────────────────────────────────────────────────
 
    @Nested
    @DisplayName("When lock cannot be acquired")
    class LockNotAcquired {
 
        @Test
        @DisplayName("Should do nothing when tryLock times out")
        void shouldDoNothingWhenLockTimesOut() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(false);
 
            assertDoesNotThrow(() -> takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID));
 
            verify(combatRepository, never()).findById(any());
        }
    }
 
    // ─── Fight not found ──────────────────────────────────────────────────────
 
    @Nested
    @DisplayName("When fight is not found")
    class FightNotFound {
 
        @Test
        @DisplayName("Should throw RuntimeException with fightId in message")
        void shouldThrowWhenFightNotFound() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(combatRepository.findById(FIGHT_ID)).thenReturn(Optional.empty());
 
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID));
 
            assertTrue(ex.getMessage().contains(FIGHT_ID));
        }
    }
 
    // ─── Full restore: helper is spectator, original is also spectator ────────
 
    @Nested
    @DisplayName("When both helper and original fighter are spectators")
    class BothAreSpectators {
 
        @BeforeEach
        void setUp() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(combatRepository.findById(FIGHT_ID)).thenReturn(Optional.of(fight));
            when(fight.getHelpButton()).thenReturn(helpButton);
            when(helpButton.getActivatedForUserId()).thenReturn(ORIGINAL_ID);
            when(helpButton.getClaimedByUserId()).thenReturn(HELPER_ID);
 
            when(fight.getSpectatorByUserId(HELPER_ID)).thenReturn(Optional.of(helperAsSpectator));
            when(fight.getSpectatorByUserId(ORIGINAL_ID)).thenReturn(Optional.of(originalAsSpectator));
            when(fight.getFighterByUserId(HELPER_ID)).thenReturn(originalFighter);
        }
 
        @Test
        @DisplayName("Should demote helper spectator back to SPECTATOR role")
        void shouldDemoteHelperToSpectator() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(helperAsSpectator).setPlayerType(PlayerType.SPECTATOR);
        }
 
        @Test
        @DisplayName("Should remove original fighter from spectators")
        void shouldRemoveOriginalFromSpectators() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(fight).removeSpectator(originalAsSpectator);
        }
 
        @Test
        @DisplayName("Should restore original fighter's userId")
        void shouldRestoreOriginalFighterUserId() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(originalFighter).setUserId(ORIGINAL_ID);
        }
 
        @Test
        @DisplayName("Should deactivate help button")
        void shouldDeactivateHelpButton() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(helpButton).deactivate();
        }
 
        @Test
        @DisplayName("Should save fight and broadcast WebSocket update")
        void shouldPersistAndBroadcast() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(combatRepository, times(2)).save(fight);
            verify(fightWebSocketUpdater).changeFighters(FIGHT_ID, fight);
        }
 
        @Test
        @DisplayName("Should release lock in finally block")
        void shouldReleaseLock() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(lock).unlock();
        }
    }
 
    // ─── Helper not in spectator list ─────────────────────────────────────────
 
    @Nested
    @DisplayName("When helper is not a spectator (edge case)")
    class HelperNotSpectator {
 
        @BeforeEach
        void setUp() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(combatRepository.findById(FIGHT_ID)).thenReturn(Optional.of(fight));
            when(fight.getHelpButton()).thenReturn(helpButton);
            when(helpButton.getActivatedForUserId()).thenReturn(ORIGINAL_ID);
            when(helpButton.getClaimedByUserId()).thenReturn(HELPER_ID);
 
            when(fight.getSpectatorByUserId(HELPER_ID)).thenReturn(Optional.empty());
            when(fight.getSpectatorByUserId(ORIGINAL_ID)).thenReturn(Optional.empty());
            when(fight.getFighterByUserId(HELPER_ID)).thenReturn(originalFighter);
        }
 
        @Test
        @DisplayName("Should skip helper role change when not found as spectator")
        void shouldSkipHelperRoleChange() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(helperAsSpectator, never()).setPlayerType(any());
        }
 
        @Test
        @DisplayName("Should skip removing original from spectators when not found")
        void shouldSkipRemovingOriginalSpectator() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(fight, never()).removeSpectator(any());
        }
 
        @Test
        @DisplayName("Should still restore userId and deactivate button")
        void shouldStillRestoreAndDeactivate() {
            takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID);
 
            verify(originalFighter).setUserId(ORIGINAL_ID);
            verify(helpButton).deactivate();
        }
    }
 
    // ─── InterruptedException ─────────────────────────────────────────────────
 
    @Nested
    @DisplayName("When InterruptedException is thrown")
    class Interruption {
 
        @Test
        @DisplayName("Should wrap InterruptedException and set interrupt flag")
        void shouldWrapInterruptedException() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenThrow(new InterruptedException());
 
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> takeBackControlImp.takeBackFigther(FIGHT_ID, USER_ID));
 
            assertTrue(ex.getMessage().contains(FIGHT_ID));
            assertTrue(Thread.interrupted());
        }
    }
}