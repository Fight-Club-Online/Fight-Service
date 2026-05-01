package Fight_club.Fight_Services.Infrastructure.Outbound.Persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO.UserCharacterDTO;

@Repository
public interface UserCharacterRepository extends MongoRepository<UserCharacterDTO, String> {
    
    @Query("{ 'userId': ?0, '_id': ?1 }")
    Optional<UserCharacterDTO> findByUserIdAndCharacterId(String userId, String characterId);
    
    @Query("{ 'userId': ?0 }")
    List<UserCharacterDTO> findAllByUserId(String userId);
}