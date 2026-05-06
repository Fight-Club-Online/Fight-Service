package Fight_club.Fight_Services.Application.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import Fight_club.Fight_Services.Application.Ports.Input.SelectFighterUseCase;
import Fight_club.Fight_Services.Domain.Repository.CombatRepository;
import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Domain.models.Health;
import Fight_club.Fight_Services.Domain.models.Hitbox;
import Fight_club.Fight_Services.Domain.models.Skill;
import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO.UserCharacterDTO;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.FightWebSocketUpdater;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@AllArgsConstructor
public class SelectFighterImpl implements SelectFighterUseCase {
    private final FightWebSocketUpdater fightWebSocketUpdater;
    private final CombatRepository combatRepository;
    private final RedissonClient redisson;
    private final GetUserCharacterByUserIdAndCharacterIdImp getUserCharacterService;

    @Override
    public void selectFigther(String fightId, String userId, String usercharacterid, String username) {
        log.info("=== INICIANDO selectFigther ===");
        log.info("fightId: {}, userId: {}, usercharacterid: {}", fightId, userId, usercharacterid);

        Fight fight = combatRepository.findById(fightId)
                .orElseThrow(() -> new RuntimeException("Fight not found: " + fightId));
        log.info("Fight encontrado: {}", fightId);

        Fighter fighter = fight.getFighterByUserId(userId);
        log.info("Fighter obtenido para userId: {}", userId);

        fighter.setId(usercharacterid);
        fighter.setUserId(userId);
        fighter.setHasCharacter(true);

        log.info("Buscando UserCharacter en BD con userId={}, usercharacterid={}", userId, usercharacterid);
        Optional<UserCharacterDTO> userCharacterOpt = getUserCharacterService.execute(userId, usercharacterid);

        if (userCharacterOpt.isPresent()) {
            UserCharacterDTO userCharacter = userCharacterOpt.get();
            log.info("✓ UserCharacter ENCONTRADO en BD");
            log.info("  - characterId: {}", userCharacter.getCharacterId());
            log.info("  - characterName: {}", userCharacter.getCharacterName());
            log.info("  - characterLevel: {}", userCharacter.getCharacterLevel());
            log.info("  - characterATK: {}", userCharacter.getCharacterATK());
            log.info("  - characterDEF: {}", userCharacter.getCharacterDEF());

            fighter.setCharacterId(userCharacter.getCharacterId());
            fighter.setCharacterName(userCharacter.getCharacterName());
            fighter.setCharacterLevel(userCharacter.getCharacterLevel());
            fighter.setCharacterATK(userCharacter.getCharacterATK());
            fighter.setCharacterDEF(userCharacter.getCharacterDEF());

            log.info("✓ Stats del Fighter cargados desde BD");
        } else {
            log.warn("✗ UserCharacter NO encontrado en BD - usando valores por defecto");
            // Fallback a valores hardcodeados si no se encuentra el personaje
            fighter.setCharacterId(usercharacterid);
            fighter.setCharacterName(usercharacterid != null && !usercharacterid.isBlank() ? usercharacterid : "Guerrero");
            fighter.setCharacterLevel(5);
            fighter.setCharacterATK(20);
            fighter.setCharacterDEF(10);

            log.info("  - characterId (default): {}", fighter.getCharacterId());
            log.info("  - characterName (default): {}", fighter.getCharacterName());
            log.info("  - characterLevel (default): 5");
            log.info("  - characterATK (default): 20");
            log.info("  - characterDEF (default): 10");
        }

        Health health = new Health(100, 100);
        fighter.setHealth(health);
        log.info("Health configurado: 100/100");

        List<Skill> skills = new ArrayList<>();
        skills.add(
                new Skill(
                        FighterAction.BASIC_ATTACK,
                        fighter.getCharacterATK(),
                        60,
                        5,
                        10,
                        15,
                        20,
                        5
                )
        );

        fighter.setSkills(skills);
        log.info("Skills configurados: BASIC_ATTACK");

        if (fight.isPlayerOne(userId)) {
            fighter.setPosX(0);
            fighter.setPosY(280);
            fighter.setDirection(Direction.RIGHT);
            log.info("Fighter posicionado como PLAYER1: (0, 280) dirección RIGHT");
        } else {
            fighter.setPosX(750);
            fighter.setPosY(280);
            fighter.setDirection(Direction.LEFT);
            log.info("Fighter posicionado como PLAYER2: (750, 280) dirección LEFT");
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
        log.info("Hitbox configurado: 50x100");

        fighter.setCurrentAction(FighterAction.IDLE);
        fighter.setBlocking(false);
        fighter.setCurrentStunFrames(0);
        fighter.setUsername(username != null && !username.isBlank() ? username : userId);
        log.info("Estado inicial: IDLE, no bloqueando, stun frames = 0");

        combatRepository.save(fight);
        log.info("✓ Fight guardado en repositorio");

        fightWebSocketUpdater.selectFighter(fightId, fight);
        log.info("✓ Notificación de selectFighter enviada por WebSocket");

        log.info("=== FINALIZANDO selectFigther ===");
        log.info("Fighter configurado: {} (Level {}, ATK {}, DEF {})", 
            fighter.getCharacterName(), 
            fighter.getCharacterLevel(), 
            fighter.getCharacterATK(), 
            fighter.getCharacterDEF());
    }
}
