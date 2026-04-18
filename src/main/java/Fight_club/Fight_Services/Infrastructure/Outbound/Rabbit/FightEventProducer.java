package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit;

import Fight_club.Fight_Services.Application.Services.FightHistoryService;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.FightFinishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig.FIGHT_EXCHANGE;
import static Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig.FIGHT_FINISHED_ROUTING_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class FightEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final FightHistoryService fightHistoryService;

    public void publishFightFinished(FightFinishedEvent event) {
        rabbitTemplate.convertAndSend(FIGHT_EXCHANGE, FIGHT_FINISHED_ROUTING_KEY, event);
        fightHistoryService.saveFightResult(event);
        log.info("[FIGHT] Evento publicado: fightId={} winner={} result={}",
                event.getFightId(), event.getWinnerUserId(), event.getResult());
    }
}