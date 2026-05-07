package Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO.Mappers;

import Fight_club.Fight_Services.Domain.models.Fight;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO.FightSocketDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FightMapperDTO {
    FightSocketDTO toFightSocketDTO(Fight fight);
}
