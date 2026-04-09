package Fight_club.Fight_Services.Domain.models;

import Fight_club.Fight_Services.Domain.models.Enums.Direction;
import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Fighter {
    
    private  String id;
    private  String userId;
    private boolean hasCharacter=false;



    private  long characterId;
    private  String characterName;
    private  int characterLevel;
    private  int characterATK;
    private  int characterDEF;

    private Health health;
    private List<Skill> skills;

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
        if (!this.health.isAlive() || !hasCharacter) return;

        int damageToTake;
        if (this.isBlocking) {
            damageToTake = Math.max(1, enemySkill.baseDamage() / 10);
        } else {
            damageToTake = Math.max(2, enemySkill.baseDamage() - (this.characterDEF / 3));
        }
        
        this.health = this.health.takeDamage(damageToTake);
    }

    public void executeAction(FighterAction action) {
        if (action == FighterAction.JUMP && !this.isGrounded) return;
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
        if(!hasCharacter) return true ;
        return !this.health.isAlive();
    }


}