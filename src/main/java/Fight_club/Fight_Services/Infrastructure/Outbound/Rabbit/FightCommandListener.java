package Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit;

import Fight_club.Fight_Services.Application.Services.Fight.FightCommandRoutingService;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.FightCommandMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig.FIGHT_COMMAND_QUEUE;

@Component
@RequiredArgsConstructor
public class FightCommandListener {

    private final FightCommandRoutingService fightCommandRoutingService;

    @RabbitListener(queues = "#{@" + FIGHT_COMMAND_QUEUE + ".name}")
    public void onCommand(FightCommandMessage command) {
        fightCommandRoutingService.execute(command);
    }
}
