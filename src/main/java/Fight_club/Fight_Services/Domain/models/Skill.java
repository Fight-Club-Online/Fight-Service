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
        baseDamage = Math.max(0, baseDamage);
        startUpFrames = Math.max(0, startUpFrames);
        activeFrames = Math.max(1, activeFrames);
    }
}