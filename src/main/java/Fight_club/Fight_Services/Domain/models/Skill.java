package Fight_club.Fight_Services.Domain.models;

import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;

public record Skill ( 
    FighterAction action,
    int baseDamage,
    int cooldown,
    int startUpFrames,
    int activeFrames,
    int recoveryFrames,
    int hitStun,
    int blockStun
) {
    public Skill {
    
        if (baseDamage < 0) throw new IllegalArgumentException("Base damage cannot be negative");
        if (startUpFrames < 0) throw new IllegalArgumentException("Startup frames cannot be negative");
        if (activeFrames < 1) throw new IllegalArgumentException("Active frames must be at least 1");
    }
}