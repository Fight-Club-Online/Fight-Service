package Fight_club.Fight_Services.Application.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO.UserCharacterDTO;
import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.UserCharacterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetUserCharactersByUserIdImp {

    private final UserCharacterRepository userCharacterRepository;

    public List<UserCharacterDTO> execute(String userId) {
        return userCharacterRepository.findAllByUserId(userId);
    }
}
