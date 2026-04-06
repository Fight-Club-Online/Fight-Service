package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event;

import java.time.Instant;

import lombok.Data;

@Data
public class GuestRegisteredEvent {
    private String userId;
    private String username;
    private Instant createdAt;
    private Instant guestExpiration;
}
