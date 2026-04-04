package Fight_club.Fight_Services.Domain.models;


import Fight_club.Fight_Services.Domain.models.Enums.ButtonStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class Fight {
    private final String id;
    private final Fighter player1;
    private final Fighter player2;
    private boolean isActive;
    private final HelpButton helpButton;
    private final List<Player> spectators;

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

    public Optional<Player> getSpectatorByUserId(String userId) {
        return spectators.stream().filter(s -> s.getUserId().equals(userId)).findFirst();
    }

    public void finishFight() {
        this.isActive = false;
    }
}