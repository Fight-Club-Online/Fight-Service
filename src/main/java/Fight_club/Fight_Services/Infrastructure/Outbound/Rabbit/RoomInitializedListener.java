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
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.RoomPlayerEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig.ROOM_QUEUE;
import static Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.Mapper.ToPlayer.toPlayer;

@Component
@Slf4j
@AllArgsConstructor
public class RoomInitializedListener {

    private final CombatRepository combatRepository;
    private final RestTemplate restTemplate;

    @RabbitListener(queues = ROOM_QUEUE)
    public void handleRoomInitialized(RoomInitializedEvent roomInitializedEvent) {
        log.info("Received message: {}", roomInitializedEvent);
        roomInitializedEvent.getPlayers().forEach(p ->
                log.info("Player -> id: {}, type: {}", p.getUserId(), p.getPlayerType())
        );

        long random = ThreadLocalRandom.current().nextLong();

        List<Player> specs = roomInitializedEvent.getPlayers().stream()
                .filter(p->p.getPlayerType().equals(PlayerType.SPECTATOR))
                .map(ToPlayer::toPlayer).toList();
        List<Fighter> figthers = roomInitializedEvent.getPlayers().stream()
                .filter(p->p.getPlayerType().equals(PlayerType.PLAYER))
                .map(ToFigther::toFighter).toList();
        log.info("Spectators count: {}", specs.size());
        log.info("Fighters count: {}", figthers.size());
        if(figthers.size() != 2){
            throw new RuntimeException("El numero de figthers debe ser 2");
        }
        HelpButton helpButton = HelpButton.builder()
                .buttonId(roomInitializedEvent.getRoomId()+random)
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
        activarVoiceChat(roomInitializedEvent.getRoomId());
    }
    private void activarVoiceChat(long roomId){
        try{
                String url = "mongodb+srv://pacoandres03_db_user:admin@blueteam.biz5ysx.mongodb.net/VOICE-CHAT?retryWrites=true&w=majority";
                restTemplate.postForEntity(url, null, String.class);
                log.info("✅ Voice Chat habilitado para la sala: {}", roomId);
        }catch (Exception e){
                log.error("❌ No se pudo conectar con el microservicio de Voz: {}", e.getMessage());
        }
    }
}
