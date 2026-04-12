package Fight_club.Fight_Services.Infrastructure.Outbound.Http;

import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Enums.PlayerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceChatNotifier {

    @Value("${voice.chat.base-url:http://localhost:3030}")
    private String voiceChatBaseUrl;

    private final RestTemplateBuilder restTemplateBuilder;

    public void notifyFightStarted(Fight fight) {
        if (fight == null || fight.getPlayer1() == null || fight.getPlayer2() == null) {
            return;
        }

        List<Map<String, String>> fighters = List.of(
                toVoicePlayer(fight.getPlayer1()),
                toVoicePlayer(fight.getPlayer2())
        );

        Map<String, Object> payload = Map.of(
                "fightId", fight.getId(),
                "players", fighters
        );

        callVoiceChat("/api/iniciar-partida", payload);
    }

    public void notifyFightFinished(String fightId) {
        if (fightId == null || fightId.isBlank()) {
            return;
        }

        Map<String, Object> payload = Map.of("fightId", fightId);
        callVoiceChat("/api/finalizar-partida", payload);
    }

    private Map<String, String> toVoicePlayer(Fighter fighter) {
        return Map.of(
                "userId", fighter.getUserId(),
                "username", fighter.getUserId(),
                "playerType", PlayerType.PLAYER.name()
        );
    }

    private void callVoiceChat(String path, Map<String, Object> payload) {
        String url = voiceChatBaseUrl + path;
        try {
            RestTemplate restTemplate = restTemplateBuilder.build();
            ResponseEntity<String> response = restTemplate.postForEntity(url, payload, String.class);
            log.info("Voice chat notified: {} -> status {}", path, response.getStatusCode().value());
        } catch (Exception ex) {
            log.warn("Could not notify voice chat {}: {}", path, ex.getMessage());
        }
    }
}
