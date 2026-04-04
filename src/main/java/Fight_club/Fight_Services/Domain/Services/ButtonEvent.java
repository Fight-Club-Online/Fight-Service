package Fight_club.Fight_Services.Domain.Services;

import Fight_club.Fight_Services.Domain.models.HelpButton;


public interface ButtonEvent {
    void activate(HelpButton button, int health, int maxHealth,String userId);
}
