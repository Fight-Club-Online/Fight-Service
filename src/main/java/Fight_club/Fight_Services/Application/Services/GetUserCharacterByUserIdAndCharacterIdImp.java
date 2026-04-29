package Fight_club.Fight_Services.Application.Services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO.UserCharacterDTO;
import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.UserCharacterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetUserCharacterByUserIdAndCharacterIdImp {

    private final UserCharacterRepository userCharacterRepository;

    public Optional<UserCharacterDTO> execute(String userId, String characterId) {
        return userCharacterRepository.findByUserIdAndCharacterId(userId, characterId);
    }
}