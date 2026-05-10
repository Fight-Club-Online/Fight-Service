package Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO;

public record FightSocketDTO(
        String id,
        FigtherSocketDTO player1,
        FigtherSocketDTO player2,
        boolean active
) {
}
