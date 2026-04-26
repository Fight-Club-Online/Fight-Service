package Fight_club.Fight_Services.Application.Services;

import Fight_club.Fight_Services.Application.Ports.Input.SelectFighterUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.*;
import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import lombok.AllArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        fighter.setCharacterName(figtherId != null && !figtherId.isBlank() ? figtherId : "Guerrero");
        fighter.setCharacterLevel(5);
        fighter.setCharacterATK(20);
        fighter.setCharacterDEF(10);

        Health health = new Health(100, 100);
        fighter.setHealth(health);

        List<Skill> skills = new ArrayList<>();
        skills.add(
                new Skill(
                        FighterAction.BASIC_ATTACK,
                        75,
                        60,
                        5,
                        10,
                        15,
                        20,
                        5
                )
        );

        fighter.setSkills(skills);

        if(fight.isPlayerOne(userId)){
            fighter.setPosX(0);
            fighter.setPosY(280);
            fighter.setDirection(Direction.RIGHT);
        }else{
            fighter.setPosX(750);
            fighter.setPosY(280);
            fighter.setDirection(Direction.LEFT);

        }

        fighter.setVelocityX(0);
        fighter.setVelocityY(0);

        fighter.setGrounded(true);

        Hitbox hitbox = new Hitbox();
        hitbox.setOffsetX(0);
        hitbox.setOffsetY(0);
        hitbox.setWidth(50);
        hitbox.setHeight(100);
        fighter.setHitbox(hitbox);

        fighter.setCurrentAction(FighterAction.IDLE);
        fighter.setBlocking(false);
        fighter.setCurrentStunFrames(0);





        combatRepository.save(fight);
        fightWebSocketUpdater.selectFighter(fightId, fight);
    }
}
