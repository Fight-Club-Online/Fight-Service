package Fight_club.Fight_Services.Application.Services.Character;

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

import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO.UserCharacterDTO;
import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.UserCharacterRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserCharacterByUserIdAndCharacterIdImp Tests")
class GetUserCharacterByUserIdAndCharacterIdImpTest {

    @Mock
    private UserCharacterRepository userCharacterRepository;

    @InjectMocks
    private GetUserCharacterByUserIdAndCharacterIdImp getUserCharacterService;

    private UserCharacterDTO userCharacterDTO;

    @BeforeEach
    void setUp() {
        userCharacterDTO = new UserCharacterDTO();
        userCharacterDTO.setUserId("user-1");
        userCharacterDTO.setCharacterId("char-1");
        userCharacterDTO.setCharacterName("Warrior");
        userCharacterDTO.setCharacterLevel(5);
        userCharacterDTO.setCharacterATK(20);
        userCharacterDTO.setCharacterDEF(10);
    }

    @Test
    @DisplayName("Should return UserCharacter when found")
    void testExecuteFound() {
        when(userCharacterRepository.findByUserIdAndCharacterId("user-1", "char-1"))
                .thenReturn(Optional.of(userCharacterDTO));

        Optional<UserCharacterDTO> result = getUserCharacterService.execute("user-1", "char-1");

        assert(result.isPresent());
        assert(result.get().getCharacterId().equals("char-1"));
        assert(result.get().getCharacterName().equals("Warrior"));
        verify(userCharacterRepository).findByUserIdAndCharacterId("user-1", "char-1");
    }

    @Test
    @DisplayName("Should return empty when UserCharacter not found")
    void testExecuteNotFound() {
        when(userCharacterRepository.findByUserIdAndCharacterId("user-1", "char-1"))
                .thenReturn(Optional.empty());

        Optional<UserCharacterDTO> result = getUserCharacterService.execute("user-1", "char-1");

        assert(result.isEmpty());
        verify(userCharacterRepository).findByUserIdAndCharacterId("user-1", "char-1");
    }

    @Test
    @DisplayName("Should query with correct userId and characterId")
    void testExecuteWithCorrectParameters() {
        when(userCharacterRepository.findByUserIdAndCharacterId("user-1", "char-1"))
                .thenReturn(Optional.of(userCharacterDTO));

        getUserCharacterService.execute("user-1", "char-1");

        verify(userCharacterRepository).findByUserIdAndCharacterId("user-1", "char-1");
    }

    @Test
    @DisplayName("Should handle different userId correctly")
    void testExecuteWithDifferentUserId() {
        when(userCharacterRepository.findByUserIdAndCharacterId("user-2", "char-1"))
                .thenReturn(Optional.empty());

        Optional<UserCharacterDTO> result = getUserCharacterService.execute("user-2", "char-1");

        assert(result.isEmpty());
        verify(userCharacterRepository).findByUserIdAndCharacterId("user-2", "char-1");
    }

    @Test
    @DisplayName("Should handle different characterId correctly")
    void testExecuteWithDifferentCharacterId() {
        when(userCharacterRepository.findByUserIdAndCharacterId("user-1", "char-2"))
                .thenReturn(Optional.empty());

        Optional<UserCharacterDTO> result = getUserCharacterService.execute("user-1", "char-2");

        assert(result.isEmpty());
        verify(userCharacterRepository).findByUserIdAndCharacterId("user-1", "char-2");
    }

    @Test
    @DisplayName("Should return complete UserCharacterDTO with all fields")
    void testExecuteReturnsCompleteDTO() {
        when(userCharacterRepository.findByUserIdAndCharacterId("user-1", "char-1"))
                .thenReturn(Optional.of(userCharacterDTO));

        Optional<UserCharacterDTO> result = getUserCharacterService.execute("user-1", "char-1");

        assert(result.get().getUserId().equals("user-1"));
        assert(result.get().getCharacterId().equals("char-1"));
        assert(result.get().getCharacterName().equals("Warrior"));
        assert(result.get().getCharacterLevel() == 5);
        assert(result.get().getCharacterATK() == 20);
        assert(result.get().getCharacterDEF() == 10);
    }

}
