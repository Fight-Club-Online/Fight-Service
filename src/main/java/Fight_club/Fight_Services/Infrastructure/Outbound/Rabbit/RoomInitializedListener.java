package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit;

import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.ButtonStatus;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.HelpButton;
import Fight_club.Fight_Services.Domain.models.Player;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.Mapper.ToFigther;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.Mapper.ToPlayer;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.RoomInitializedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig.ROOM_QUEUE;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoomInitializedListener {

    private final CombatRepository combatRepository;
    private final RestTemplate restTemplate;

    @Value("${voice.chat.url:http://localhost:300}")
    private String voiceChatUrl;

    @RabbitListener(queues = ROOM_QUEUE)
    public void handleRoomInitialized(RoomInitializedEvent roomInitializedEvent) {
        log.info("Received message: {}", roomInitializedEvent);
        roomInitializedEvent.getPlayers().forEach(p ->
                log.info("Player -> id: {}, type: {}", p.getUserId(), p.getPlayerType())
        );

        long random = ThreadLocalRandom.current().nextLong();

        List<Player> specs = roomInitializedEvent.getPlayers().stream()
                .filter(p -> p.getPlayerType().equals(PlayerType.SPECTATOR))
                .map(ToPlayer::toPlayer)
                .toList();

        List<Fighter> figthers = roomInitializedEvent.getPlayers().stream()
                .filter(p -> p.getPlayerType().equals(PlayerType.PLAYER))
                .map(ToFigther::toFighter)
                .toList();

        log.info("Spectators count: {}", specs.size());
        log.info("Fighters count: {}", figthers.size());

        if (figthers.size() != 2) {
            throw new RuntimeException("El numero de figthers debe ser 2");
        }

        HelpButton helpButton = HelpButton.builder()
                .buttonId(roomInitializedEvent.getRoomId() + random)
                .isVisible(false)
                .fightId(String.valueOf(roomInitializedEvent.getRoomId()))
                .activatedForUserId("")
                .claimedByUserId("")
                .status(ButtonStatus.INACTIVE)
                .type(null)
                .build();

        Fight f = Fight.builder()
                .id(String.valueOf(roomInitializedEvent.getRoomId()))
                .spectators(specs)
                .player1(figthers.get(0))
                .player2(figthers.get(1))
                .isActive(false)
                .helpButton(helpButton)
                .build();

        combatRepository.save(f);
        log.info("Combat saved in repository: {}", f.getId());

        activarVoiceChat(f.getId(), roomInitializedEvent);
    }

    private void activarVoiceChat(String fightId, RoomInitializedEvent roomInitializedEvent) {
        try {
            String url = voiceChatUrl + "/api/iniciar-partida";

            List<Map<String, String>> players = roomInitializedEvent.getPlayers().stream()
                    .map(p -> {
                        Map<String, String> player = new HashMap<>();
                        player.put("userId", p.getUserId());
                        player.put("playerType", p.getPlayerType().name());
                        return player;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> body = new HashMap<>();
            body.put("fightId", fightId);
            body.put("roomId", roomInitializedEvent.getRoomId());
            body.put("players", players);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, String.class);
            log.info("Voice chat habilitado. fightId={} roomId={}", fightId, roomInitializedEvent.getRoomId());

        } catch (Exception e) {
            log.error("No se pudo conectar con el microservicio de voz: {}", e.getMessage());
        }
    }
}