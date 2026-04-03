package Fight_club.Fight_Services.Domain.models;


import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    private String userId;
    private PlayerType playerType;
}