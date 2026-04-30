package Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectFighterDTO {
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("characterId")
    private String characterId;
    
    @JsonProperty("username")
    private String username;
}
