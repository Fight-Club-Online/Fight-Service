package Fight_club.Fight_Services.Domain.models;

import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Fighter {
    
    private final String id;
    private  String userId;
    
    private final long characterId;
    private final String characterName;
    private final int characterLevel;
    private final int characterATK;
    private final int characterDEF;

    private Health health;
    private final List<Skill> skills; 

    private int posX;
    private int posY;
    private int velocityX;
    private int velocityY;
    private boolean isGrounded;
    private Direction direction;
    private Hitbox hitbox;

    private FighterAction currentAction;
    private boolean isBlocking;
    private int currentStunFrames;

    public Skill getSkillForAction(FighterAction action) {
        return this.skills.stream()
                .filter(s -> s.action().equals(action))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Action not found: " + action));
    }

    public void receiveAttack(Skill enemySkill) {
        if (!this.health.isAlive()) return;

        int damageToTake;
        
        if (this.isBlocking) {
            damageToTake = Math.max(1, enemySkill.baseDamage() / 10);
            this.currentStunFrames = enemySkill.blockStun();
        } else {
            damageToTake = Math.max(1, enemySkill.baseDamage() - (this.characterDEF / 5));
            this.currentStunFrames = enemySkill.hitStun();
            this.currentAction = FighterAction.IDLE;
        }
        
        this.health = this.health.takeDamage(damageToTake);
    }

    public void executeAction(FighterAction action) {
        if (this.currentStunFrames > 0) return;

        this.currentAction = action;
        this.isBlocking = (action == FighterAction.BLOCK);
        
        if (action == FighterAction.JUMP && this.isGrounded) {
            this.isGrounded = false;
            this.velocityY = -15; 
        }
    }

    public void updateState() {
        if (this.currentStunFrames > 0) {
            this.currentStunFrames--;
        }
    }

    public void updatePosition(int newX, int newY) {
        this.posX = newX;
        this.posY = newY;
    }

    public boolean isDefeated() {
        return !this.health.isAlive();
    }


}