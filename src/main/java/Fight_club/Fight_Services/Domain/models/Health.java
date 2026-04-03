package Fight_club.Fight_Services.Domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Health {
    private final int currentHealth;
    private final int maxHealth;

    private Health(int currentHealth, int maxHealth) {
        if(maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be greater than 0");
        }
        this.currentHealth = Math.max(0, Math.min(currentHealth, maxHealth));
        this.maxHealth = maxHealth;
    }

    public static Health CompleteHealth(int maxHealth) {
        return new Health(maxHealth, maxHealth);
    }

    public Health takeDamage(int damage) {
        if(damage < 0) {
            return this;
        }
        int newHealth = Math.max(0, this.currentHealth - damage);
        return new Health(newHealth, this.maxHealth);
    }

    public boolean isAlive() {
        return this.currentHealth > 0;
    }
    
}
