package Fight_club.Fight_Services.Domain.models;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Fight {
    private final String id;
    private final Fighter player1;
    private final Fighter player2;
    private boolean isActive;

    public Fighter getFighterByUserId(String userId) {
        if (player1.getUserId().equals(userId)) {
            return player1;
        } else if (player2.getUserId().equals(userId)) {
            return player2;
        }
        throw new IllegalArgumentException("El usuario con ID " + userId + " no está en esta pelea.");
    }

    public Fighter getOpponentOf(String userId) {
        return player1.getUserId().equals(userId) ? player2 : player1;
    }
    
    public void finishFight() {
        this.isActive = false;
    }
}