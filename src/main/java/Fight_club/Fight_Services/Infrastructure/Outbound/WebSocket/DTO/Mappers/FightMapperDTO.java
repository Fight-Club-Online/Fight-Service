package Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO.Mappers;

import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO.FightSocketDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = FighterSocketMapperDTO.class)
public interface FightMapperDTO {
    @Mapping(source = "player1", target = "player1")
    @Mapping(source = "player2", target = "player2")
    @Mapping(source = "active", target = "active")
    FightSocketDTO toFightSocketDTO(Fight fight);
}
