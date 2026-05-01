package Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_characters")
public class UserCharacterDTO {
    
    @Id
    private String characterId;
    
    @Field("userId")
    private String userId;
    
    @Field("characterName")
    private String characterName;
    
    @Field("characterLevel")
    private int characterLevel;
    
    @Field("characterHp")
    private int characterHp;
    
    @Field("characterATK")
    private int characterATK;
    
    @Field("characterDEF")
    private int characterDEF;
}