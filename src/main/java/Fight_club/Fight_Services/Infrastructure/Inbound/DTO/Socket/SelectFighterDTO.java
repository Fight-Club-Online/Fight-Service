package Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectFighterDTO {
    private String userId;    // Coincide con 'odUserId'
    private String characterId;
    private String username;
}
