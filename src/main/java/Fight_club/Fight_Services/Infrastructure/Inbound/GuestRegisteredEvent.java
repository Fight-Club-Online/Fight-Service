package Fight_club.Fight_Services.Infrastructure.Inbound;

import java.time.Instant;

import lombok.Data;

@Data
public class GuestRegisteredEvent {
    private String userId;
    private String username;
    private Instant createdAt;
    private Instant guestExpiration;
}
