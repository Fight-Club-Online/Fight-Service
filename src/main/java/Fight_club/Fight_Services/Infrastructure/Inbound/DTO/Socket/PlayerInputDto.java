package Fight_club.Fight_Services.Infrastructure.Inbound.DTO.Socket;

import lombok.Data;

@Data
public class PlayerInputDto {
    private String userId;
    private String action;
}