package Fight_club.Fight_Services.Application.Services.Fight;

import Fight_club.Fight_Services.Application.Ports.Input.AskHelpButtonUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.ClaimHelpButtonUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.ProcessCombatInputUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.SelectFighterUseCase;
import Fight_club.Fight_Services.Application.Ports.Input.TakeBackControlUseCase;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.FightCommandMessage;
import Fight_club.Fight_Services.Infrastructure.Outbound.Rabbit.Event.FightCommandType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig.FIGHT_COMMAND_EXCHANGE;
import static Fight_club.Fight_Services.Infrastructure.Config.RabbitConfig.fightCommandRoutingKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class FightCommandRoutingService {

    private final FightOwnershipService fightOwnershipService;
    private final RabbitTemplate rabbitTemplate;
    private final ProcessCombatInputUseCase processCombatInputUseCase;
    private final ClaimHelpButtonUseCase claimHelpButtonUseCase;
    private final AskHelpButtonUseCase askHelpButtonUseCase;
    private final TakeBackControlUseCase takeBackControlUseCase;
    private final SelectFighterUseCase selectFighterUseCase;

    public void handlePlayerInput(String fightId, String userId, String action) {
        dispatch(
                fightId,
                FightCommandType.PLAYER_INPUT,
                userId,
                action,
                null,
                null
        );
    }

    public void claimHelpButton(String fightId, String userId) {
        dispatch(fightId, FightCommandType.CLAIM_HELP, userId, null, null, null);
    }

    public void askHelpButton(String fightId, String userId) {
        dispatch(fightId, FightCommandType.ASK_HELP, userId, null, null, null);
    }

    public void takeBackFighter(String fightId, String userId) {
        dispatch(fightId, FightCommandType.TAKE_BACK, userId, null, null, null);
    }

    public void selectFighter(String fightId, String userId, String characterId, String username) {
        dispatch(
                fightId,
                FightCommandType.SELECT_FIGHTER,
                userId,
                null,
                characterId,
                username
        );
    }

    public void execute(FightCommandMessage command) {
        if (command == null || command.getType() == null) {
            log.warn("Ignoring invalid fight command: {}", command);
            return;
        }
        switch (command.getType()) {
            case PLAYER_INPUT -> {
                FighterAction parsedAction = parseAction(command.getAction());
                if (parsedAction == null) {
                    log.warn("Ignoring PLAYER_INPUT with invalid action '{}', command={}", command.getAction(), command);
                    return;
                }
                processCombatInputUseCase.handlePlayerInput(
                        command.getFightId(),
                        command.getUserId(),
                        parsedAction
                );
            }
            case CLAIM_HELP -> claimHelpButtonUseCase.claimHelpButton(command.getFightId(), command.getUserId());
            case ASK_HELP -> askHelpButtonUseCase.askHelpButton(command.getFightId(), command.getUserId());
            case TAKE_BACK -> takeBackControlUseCase.takeBackFigther(command.getFightId(), command.getUserId());
            case SELECT_FIGHTER -> selectFighterUseCase.selectFigther(
                    command.getFightId(),
                    command.getUserId(),
                    command.getCharacterId(),
                    command.getUsername()
            );
        }
    }

    private void dispatch(String fightId, FightCommandType type, String userId, String action, String characterId, String username) {
        log.info("Dispatching command: Mio?{},routingKey{},pelea{},accion{}",fightOwnershipService.isOwnedByCurrentNode(fightId),fightCommandRoutingKey(fightOwnershipService.slotForFight(fightId)),fightId,type);

        FightCommandMessage command = new FightCommandMessage(type, fightId, userId, action, characterId, username);
        if (fightOwnershipService.isOwnedByCurrentNode(fightId)) {
            execute(command);
            return;
        }
        rabbitTemplate.convertAndSend(
                FIGHT_COMMAND_EXCHANGE,
                fightCommandRoutingKey(fightOwnershipService.slotForFight(fightId)),
                command
        );
    }

    private FighterAction parseAction(String action) {
        if (action == null || action.isBlank()) {
            return null;
        }
        try {
            return FighterAction.valueOf(action);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
