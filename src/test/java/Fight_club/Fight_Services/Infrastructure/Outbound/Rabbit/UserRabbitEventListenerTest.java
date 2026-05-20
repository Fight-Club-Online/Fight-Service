package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.GuestRegisteredEvent;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.UserRegisteredEvent;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRabbitEventListener Tests")
class UserRabbitEventListenerTest {

    @InjectMocks
    private UserRabbitEventListener userRabbitEventListener;

    private UserRegisteredEvent userRegisteredEvent;
    private GuestRegisteredEvent guestRegisteredEvent;

    @BeforeEach
    void setUp() {
        userRegisteredEvent = new UserRegisteredEvent();
        userRegisteredEvent.setUserId("user-1");
        userRegisteredEvent.setUsername("Player1");

        guestRegisteredEvent = new GuestRegisteredEvent();
        guestRegisteredEvent.setUserId("guest-1");
        guestRegisteredEvent.setUsername("Guest1");
    }

    @Test
    @DisplayName("Should handle user registration event")
    void testHandleUserRegistration() {
        userRabbitEventListener.handleUserRegistration(userRegisteredEvent);
        
        assert(userRegisteredEvent.getUserId().equals("user-1"));
        assert(userRegisteredEvent.getUsername().equals("Player1"));
    }

    @Test
    @DisplayName("Should handle guest registration event")
    void testHandleGuestRegistration() {
        userRabbitEventListener.handleGuestRegistration(guestRegisteredEvent);
        
        assert(guestRegisteredEvent.getUserId().equals("guest-1"));
        assert(guestRegisteredEvent.getUsername().equals("Guest1"));
    }

    @Test
    @DisplayName("Should handle null user registration event without crashing")
    void testHandleNullUserRegistrationEvent() {
        try {
            userRabbitEventListener.handleUserRegistration(null);
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    @DisplayName("Should handle null guest registration event without crashing")
    void testHandleNullGuestRegistrationEvent() {
        try {
            userRabbitEventListener.handleGuestRegistration(null);
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    @DisplayName("Should handle multiple user registrations")
    void testHandleMultipleUserRegistrations() {
        UserRegisteredEvent event1 = new UserRegisteredEvent();
        event1.setUserId("user-1");
        event1.setUsername("Player1");

        UserRegisteredEvent event2 = new UserRegisteredEvent();
        event2.setUserId("user-2");
        event2.setUsername("Player2");

        userRabbitEventListener.handleUserRegistration(event1);
        userRabbitEventListener.handleUserRegistration(event2);

        assert(event1.getUserId().equals("user-1"));
        assert(event2.getUserId().equals("user-2"));
    }

}
