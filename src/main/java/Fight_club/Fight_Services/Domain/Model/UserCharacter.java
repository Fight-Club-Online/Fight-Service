package Fight_club.Fight_Services.Domain.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCharacter {
    private String id;
        private long characterId;
        private int characterLevel;
        private String characterName;
        private String characterHp;
        private String characterATK;
        private String characterDEF;
    private String user;
}

