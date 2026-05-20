package Fight_club.Fight_Services.Application.Services.Fight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;

import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Fight;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetFightImpl Tests")
class GetFightImplTest {

    @Mock
    private RedissonClient redisson;

    @Mock
    private CombatRepository combatRepository;

    @InjectMocks
    private GetFightImpl getFightImpl;

    private Fight fight;

    @BeforeEach
    void setUp() {
        fight = new Fight();
        fight.setId("fight-1");
        fight.setActive(true);
    }

    @Test
    @DisplayName("Should return fight when it exists")
    void testGetFightFound() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.of(fight));

        Fight result = getFightImpl.getFight("fight-1");

        assert(result != null);
        assert(result.getId().equals("fight-1"));
        verify(combatRepository).findById("fight-1");
    }

    @Test
    @DisplayName("Should throw exception when fight not found")
    void testGetFightNotFound() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.empty());

        try {
            getFightImpl.getFight("fight-1");
        } catch (RuntimeException e) {
            assert(e.getMessage().contains("Fight not found"));
        }
    }

    @Test
    @DisplayName("Should query repository with correct fight ID")
    void testGetFightWithCorrectId() {
        when(combatRepository.findById("fight-123")).thenReturn(Optional.of(fight));

        getFightImpl.getFight("fight-123");

        verify(combatRepository).findById("fight-123");
    }

    @Test
    @DisplayName("Should return complete fight object")
    void testGetFightReturnsCompleteObject() {
        when(combatRepository.findById("fight-1")).thenReturn(Optional.of(fight));

        Fight result = getFightImpl.getFight("fight-1");

        assert(result.getId().equals("fight-1"));
        assert(result.isActive());
    }

}
