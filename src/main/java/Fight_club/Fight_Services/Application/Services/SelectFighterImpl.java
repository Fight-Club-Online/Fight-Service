package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Input.SelectFighterUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Health;
import Fight_club.Fight_Services.Domain.models.Hitbox;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import lombok.AllArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class SelectFighterImpl implements SelectFighterUseCase {
    private final FightWebSocketUpdater fightWebSocketUpdater;
    private final CombatRepository combatRepository;
    private final RedissonClient redisson;

    @Override
    public void selectFigther(String fightId, String userId, String figtherId) {
        Fight fight = combatRepository.findById(fightId)
                .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));
        Fighter fighter =  fight.getFighterByUserId(userId);

        fighter.setId(figtherId);
        fighter.setUserId(userId);
        fighter.setHasCharacter(true);

        fighter.setCharacterId(1L);
        fighter.setCharacterName("Guerrero");
        fighter.setCharacterLevel(5);
        fighter.setCharacterATK(20);
        fighter.setCharacterDEF(10);

        // Health
        Health health = new Health(100, 100);
        fighter.setHealth(health);

        fighter.setSkills(new ArrayList<>());

        // Posición inicial
        fighter.setPosX(0);
        fighter.setPosY(0);
        fighter.setVelocityX(0);
        fighter.setVelocityY(0);

        fighter.setGrounded(true);
        fighter.setDirection(Direction.RIGHT);

        // Hitbox
        Hitbox hitbox = new Hitbox();
        hitbox.setOffsetX(0);
        hitbox.setOffsetY(0);
        hitbox.setWidth(50);
        hitbox.setHeight(100);
        fighter.setHitbox(hitbox);

        // Estado inicial
        fighter.setCurrentAction(FighterAction.IDLE);
        fighter.setBlocking(false);
        fighter.setCurrentStunFrames(0);





        //guardar con nuevos datos del pelad y otros
        combatRepository.save(fight);
        //para mandar al front via ws
        fightWebSocketUpdater.selectFighter(fightId, fight);
    }
}
