package Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO.Mappers;

import Fight_club.Fight_Services.Domain.models.Fighter;
import Fight_club.Fight_Services.Infrastructure.Outbound.WebSocket.DTO.FigtherSocketDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FighterSocketMapperDTO {
    FigtherSocketDTO toDto(Fighter fighter);
}
