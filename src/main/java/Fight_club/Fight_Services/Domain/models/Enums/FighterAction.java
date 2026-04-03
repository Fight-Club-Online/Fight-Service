package Fight_club.Fight_Services.Domain.models.Enums;

public enum FighterAction {
    IDLE,              // Estado de reposo reset despues de cuando se da un golpe
    MOVE_LEFT,
    MOVE_RIGHT,
    JUMP,
    BLOCK,
    BASIC_ATTACK,      
    SPECIAL_ATTACK,
    HURT,              
    DEAD               
}