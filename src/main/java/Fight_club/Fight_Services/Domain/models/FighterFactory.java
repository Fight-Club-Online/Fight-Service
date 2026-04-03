package Fight_club.Fight_Services.Domain.models;

import Fight_club.Fight_Services.Domain.models.Enums.FighterAction;
import java.util.List;

public class FighterFactory {

    public static List<Skill> createBaseSkills() {
        // (Ataque Básico)
        Skill jab = new Skill(
            FighterAction.BASIC_ATTACK, 
            10,  
            0,   
            4,   
            2,   
            10,  
            15,  
            5    
        );

        // (Ataque Especial)
        Skill hook = new Skill(
            FighterAction.SPECIAL_ATTACK, 
            25,  
            2000,
            12,  
            4,   
            20,  
            30,  
            10   
        );

        return List.of(jab, hook);
    }
}