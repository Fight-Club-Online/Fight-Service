package Fight_club.Fight_Services.Domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Hitbox {
    private int offsetX;
    private int offsetY;
    private int width;
    private int height;
}
