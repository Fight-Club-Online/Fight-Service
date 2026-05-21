package Fight_club.Fight_Services.Application.Services.Action;
 
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.ButtomClaimedType;
import Fight_club.Fight_Services.Domain.models.Enums.ButtonStatus;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Health;
import Fight_club.Fight_Services.Domain.models.HelpButton;
import Fight_club.Fight_Services.Domain.models.Player;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
 
@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimHelpButtonImp Tests")
class ClaimHelpButtonImpTest {
 
    @Mock private FightWebSocketUpdater fightWebSocketUpdater;
    @Mock private CombatRepository combatRepository;
    @Mock private RedissonClient redisson;
    @Mock private RLock lock;
    @Mock private Fight fight;
    @Mock private HelpButton helpButton;
    @Mock private Fighter fighter;
    @Mock private Health health;
    @Mock private Player spectatorPlayer;
 
    @InjectMocks
    private ClaimHelpButtonImp claimHelpButtonImp;
 
    private static final String FIGHT_ID          = "fight-456";
    private static final String CLAIMER_ID        = "claimer-user";
    private static final String ACTIVATED_FOR_ID  = "original-user";
 
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
 
            assertDoesNotThrow(() -> claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID));
 
            verify(combatRepository, never()).findById(any());
            verify(combatRepository, never()).save(any());
        }
    }
 
    // ─── Fight not found ──────────────────────────────────────────────────────
 
    @Nested
    @DisplayName("When fight is not found")
    class FightNotFound {
 
        @Test
        @DisplayName("Should throw RuntimeException")
        void shouldThrowWhenFightNotFound() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(combatRepository.findById(FIGHT_ID)).thenReturn(Optional.empty());
 
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID));
 
            assertTrue(ex.getMessage().contains(FIGHT_ID));
        }
    }
 
 
    @Nested
    @DisplayName("When HelpButton is already claimed")
    class AlreadyClaimed {
 
        @BeforeEach
        void setUp() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(combatRepository.findById(FIGHT_ID)).thenReturn(Optional.of(fight));
            when(fight.getHelpButton()).thenReturn(helpButton);
        }
 
        @Test
        @DisplayName("Should return early when status is CLAIMED")
        void shouldReturnEarlyOnClaimedStatus() {
            when(helpButton.getStatus()).thenReturn(ButtonStatus.CLAIMED);
            when(helpButton.getClaimedByUserId()).thenReturn(null);
 
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(helpButton, never()).setClaimedByUserId(any());
            verify(combatRepository, never()).save(any());
        }

    }
 
    // ─── Claimed by spectator ─────────────────────────────────────────────────
 
    @Nested
    @DisplayName("When claimer is a spectator")
    class ClaimedBySpectator {
 
        @BeforeEach
        void setUp() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(combatRepository.findById(FIGHT_ID)).thenReturn(Optional.of(fight));
            when(fight.getHelpButton()).thenReturn(helpButton);
            when(helpButton.getStatus()).thenReturn(ButtonStatus.INACTIVE);
            when(helpButton.getClaimedByUserId()).thenReturn(null);
            when(helpButton.getActivatedForUserId()).thenReturn(ACTIVATED_FOR_ID);
 
            // Claimer is found as spectator
            when(fight.getSpectatorByUserId(CLAIMER_ID)).thenReturn(Optional.of(spectatorPlayer));
            when(fight.getFighterByUserId(ACTIVATED_FOR_ID)).thenReturn(fighter);
            when(fighter.getUserId()).thenReturn(ACTIVATED_FOR_ID);
        }
 
        @Test
        @DisplayName("Should set claimer as claimedByUserId on HelpButton")
        void shouldSetClaimedByUserId() {
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(helpButton).setClaimedByUserId(CLAIMER_ID);
        }
 
        @Test
        @DisplayName("Should mark HelpButton status as CLAIMED")
        void shouldSetStatusToClaimed() {
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(helpButton).setStatus(ButtonStatus.CLAIMED);
        }
 
        @Test
        @DisplayName("Should set button type to SPECTATOR")
        void shouldSetTypeToSpectator() {
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(helpButton).setType(ButtomClaimedType.SPECTATOR);
        }
 
        @Test
        @DisplayName("Should update spectator player role to HELPER")
        void shouldChangeSpectatorToHelper() {
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(spectatorPlayer).setPlayerType(PlayerType.HELPER);
        }
 
        @Test
        @DisplayName("Should add updated player back to fight spectators")
        void shouldAddUpdatedSpectatorToFight() {
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(fight).addSpectator(any(Player.class));
        }
 
        @Test
        @DisplayName("Should hide HelpButton after claiming")
        void shouldHideHelpButton() {
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(helpButton).setVisible(false);
        }
 
        @Test
        @DisplayName("Should persist fight and broadcast WebSocket update")
        void shouldSaveAndBroadcast() {
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(combatRepository, times(2)).save(fight);
            verify(fightWebSocketUpdater).changeFighters(FIGHT_ID, fight);
        }
    }
 
    // ─── Claimed by opponent fighter ──────────────────────────────────────────
 
    @Nested
    @DisplayName("When claimer is an opponent fighter")
    class ClaimedByOpponent {
 
        @BeforeEach
        void setUp() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenReturn(true);
            when(lock.isHeldByCurrentThread()).thenReturn(true);
            when(combatRepository.findById(FIGHT_ID)).thenReturn(Optional.of(fight));
            when(fight.getHelpButton()).thenReturn(helpButton);
            when(helpButton.getStatus()).thenReturn(ButtonStatus.INACTIVE);
            when(helpButton.getClaimedByUserId()).thenReturn(null);
 
            // Claimer is NOT a spectator
            when(fight.getSpectatorByUserId(CLAIMER_ID)).thenReturn(Optional.empty());
            when(fight.getFighterByUserId(CLAIMER_ID)).thenReturn(fighter);
            when(fighter.getHealth()).thenReturn(health);
        }
 
        @Test
        @DisplayName("Should heal opponent by 25% of max health")
        void shouldHealOpponentBy25Percent() {
            when(health.getCurrentHealth()).thenReturn(60);
            when(health.getMaxHealth()).thenReturn(100);
 
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            // 60 + (100 * 0.25) = 85
            verify(health).setCurrentHealth(85);
        }
 
        @Test
        @DisplayName("Should set button type to OPPONENT")
        void shouldSetTypeToOpponent() {
            when(health.getCurrentHealth()).thenReturn(50);
            when(health.getMaxHealth()).thenReturn(100);
 
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(helpButton).setType(ButtomClaimedType.OPPONENT);
        }
 
        @Test
        @DisplayName("Should deactivate HelpButton")
        void shouldDeactivateHelpButton() {
            when(health.getCurrentHealth()).thenReturn(50);
            when(health.getMaxHealth()).thenReturn(100);
 
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(helpButton).deactivate();
        }
 
        @Test
        @DisplayName("Should hide HelpButton")
        void shouldHideHelpButton() {
            when(health.getCurrentHealth()).thenReturn(50);
            when(health.getMaxHealth()).thenReturn(100);
 
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(helpButton).setVisible(false);
        }
 
        @Test
        @DisplayName("Should not try to add spectator to fight")
        void shouldNotAddSpectator() {
            when(health.getCurrentHealth()).thenReturn(50);
            when(health.getMaxHealth()).thenReturn(100);
 
            claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID);
 
            verify(fight, never()).addSpectator(any());
        }
    }
 
    // ─── InterruptedException ─────────────────────────────────────────────────
 
    @Nested
    @DisplayName("When InterruptedException is thrown")
    class Interruption {
 
        @Test
        @DisplayName("Should wrap InterruptedException in RuntimeException")
        void shouldWrapInterruptedException() throws InterruptedException {
            when(lock.tryLock(5, 10, TimeUnit.SECONDS)).thenThrow(new InterruptedException());
 
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> claimHelpButtonImp.claimHelpButton(FIGHT_ID, CLAIMER_ID));
 
            assertTrue(ex.getMessage().contains(FIGHT_ID));
            assertTrue(Thread.interrupted());
        }
    }
}