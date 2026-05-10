package Fight_club.Fight_Services.Domain.models;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Fight {
    private  String id;
    private  Fighter player1;
    private  Fighter player2;
    private boolean active;
    private  HelpButton helpButton;
    private  List<Player> spectators;

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

    public void addSpectator(Player player) {
        spectators.add(player);
    }

    public void removeSpectator(Player player) {
        spectators.remove(player);
    }
    public void finishFight() {
        this.active = false;
    }


    public boolean isPlayerOne(String userId) {
        return player1.getUserId().equals(userId);
    }
}