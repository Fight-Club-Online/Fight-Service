package Fight_club.Fight_Services.Domain.Model;

import Fight_club.Fight_Services.Domain.Model.Enums.Direction;

public class Fighter {
    private String id;
    private String userCharacterid;

        private long characterId;
        private int characterLevel;
        private String characterName;
        private String characterHp;
        private String characterATK;
        private String characterDEF;

    private String userId;
    private int currentHP;
    private int posX;
    private int posY;

    private Hitbox hitbox;
    private Direction direction;



}
