package Fight_club.Fight_Services.Infrastructure.Inbound;

import lombok.Data;

@Data
public class UserRegisteredEvent {
    private String userId;
    private String username;
    private String email;
    private String avatarURL;
}
