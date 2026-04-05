package Fight_club.Fight_Services.Domain.models;

import Fight_club.Fight_Services.Domain.models.Enums.ButtomClaimedType;
import Fight_club.Fight_Services.Domain.models.Enums.ButtonStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HelpButton {
    private long buttonId;
    private boolean isVisible = false;
    private String fightId;
    private String activatedForUserId = "";
    private String claimedByUserId = "";
    private ButtonStatus status=ButtonStatus.INACTIVE;
    private ButtomClaimedType type=null;

    public void activate(String userId) {
        this.status = ButtonStatus.ACTIVE;
        this.activatedForUserId = userId;
    }

    public void deactivate() {
        this.status = ButtonStatus.INACTIVE;
        this.activatedForUserId = "";
        this.claimedByUserId = "";
        this.type = null;
        this.isVisible = false;

    }
}
