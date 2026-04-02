package Fight_club.Fight_Services.Domain.Model;

import Fight_club.Fight_Services.Domain.Model.Enums.PlayerType;
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