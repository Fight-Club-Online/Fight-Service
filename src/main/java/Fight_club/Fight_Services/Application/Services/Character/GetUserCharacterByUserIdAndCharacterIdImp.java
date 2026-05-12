package Fight_club.Fight_Services.Application.Services.Character;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.DTO.UserCharacterDTO;
import Fight_club.Fight_Services.Infrastructure.Outbound.Persistence.UserCharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserCharacterByUserIdAndCharacterIdImp {

    private final UserCharacterRepository userCharacterRepository;

    public Optional<UserCharacterDTO> execute(String userId, String characterId) {
        log.info("=== BUSCANDO UserCharacter ===");
        log.info("userId: {}", userId);
        log.info("characterId: '{}' (tipo: String)", characterId);
        
        // Log: Ver todos los caracteres del usuario ESPECÍFICO
        List<UserCharacterDTO> allForUser = userCharacterRepository.findAllByUserId(userId);
        log.info("Total de caracteres para userId '{}': {}", userId, allForUser.size());
        
        if (!allForUser.isEmpty()) {
            log.info("Caracteres encontrados para este usuario:");
            allForUser.forEach(userChar -> {
                log.info("  - characterId: '{}' (tipo: {})", 
                    userChar.getCharacterId(), 
                    userChar.getCharacterId() != null ? userChar.getCharacterId().getClass().getSimpleName() : "null");
                log.info("    characterName: {}", userChar.getCharacterName());
                log.info("    characterLevel: {}", userChar.getCharacterLevel());
            });
        } else {
            log.warn("⚠️ USUARIO SIN PERSONAJES");
            log.info("Verificando si existen UserCharacters EN TODA LA BD...");
            // Intenta obtener todos los UserCharacter para diagnosticar
            try {
                List<UserCharacterDTO> allInDB = userCharacterRepository.findAll();
                log.info("Total de UserCharacters en la BD: {}", allInDB.size());
                if (!allInDB.isEmpty()) {
                    log.info("Primeros 5 UserCharacters en la BD:");
                    allInDB.stream().limit(5).forEach(uc -> {
                        log.info("  - userId: '{}', characterId: '{}', name: {}", 
                            uc.getUserId(), uc.getCharacterId(), uc.getCharacterName());
                    });
                } else {
                    log.warn("❌ LA BD ESTÁ VACÍA - NO HAY NINGÚN USERCHARACTER");
                }
            } catch (Exception e) {
                log.error("Error al listar todos los UserCharacters: {}", e.getMessage());
            }
        }
        
        // Intentar buscar exactamente como viene
        log.info("Buscando con userId='{}', characterId='{}'", userId, characterId);
        Optional<UserCharacterDTO> result = userCharacterRepository.findByUserIdAndCharacterId(userId, characterId);
        
        if (result.isPresent()) {
            log.info("✓ ENCONTRADO");
            return result;
        }
        
        log.warn("✗ NO ENCONTRADO");
        log.info("=== FIN BÚSQUEDA ===");
        
        return result;
    }
}