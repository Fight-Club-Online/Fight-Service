package Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO;

import java.util.ArrayList;

import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Health;
import Fight_club.Fight_Services.Domain.models.Hitbox;
import Fight_club.Fight_Services.Domain.models.Skill;

public class UserCharacterMapper {

    public static Fighter toFighter(UserCharacterDTO userCharacter, boolean isPlayerOne) {
        if (userCharacter == null) {
            return createEmptyFighter(null);
        }

        // Crear skills del UserCharacter
        java.util.List<Skill> skills = new ArrayList<>();
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
        Health health = new Health(100, 100);

        // Crear hitbox
        Hitbox hitbox = new Hitbox();
        hitbox.setOffsetX(0);
        hitbox.setOffsetY(0);
        hitbox.setWidth(50);
        hitbox.setHeight(100);

        int posX = isPlayerOne ? 0 : 750;
        int posY = 280;
        Direction direction = isPlayerOne ? Direction.RIGHT : Direction.LEFT;

        return Fighter.builder()
            .id(userCharacter.getCharacterId())
                .userId(userCharacter.getUserId())
                .hasCharacter(true)
                .characterId(userCharacter.getCharacterId())
                .characterName(userCharacter.getCharacterName())
                .characterLevel(userCharacter.getCharacterLevel())
                .characterATK(userCharacter.getCharacterATK())
                .characterDEF(userCharacter.getCharacterDEF())
                .health(health)
                .skills(skills)
                .posX(posX)
                .posY(posY)
                .velocityX(0)
                .velocityY(0)
                .isGrounded(true)
                .direction(direction)
                .hitbox(hitbox)
                .currentAction(FighterAction.IDLE)
                .isBlocking(false)
                .currentStunFrames(0)
                .build();
    }

    public static Fighter toFighter(UserCharacterDTO userCharacter) {
        return toFighter(userCharacter, true);
    }

    public static Fighter createEmptyFighter(String userId) {
        return Fighter.builder()
                .id(null)
                .userId(userId)
                .hasCharacter(false)
                .characterId(null)
                .characterName("No Character")
                .characterLevel(0)
                .characterATK(0)
                .characterDEF(0)
                .health(Health.CompleteHealth(0))
                .skills(new ArrayList<>())
                .posX(0)
                .posY(0)
                .velocityX(0)
                .velocityY(0)
                .isGrounded(true)
                .direction(Direction.RIGHT)
                .hitbox(new Hitbox())
                .currentAction(FighterAction.IDLE)
                .isBlocking(false)
                .currentStunFrames(0)
                .build();
    }
}