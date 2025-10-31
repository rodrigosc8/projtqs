package pt.rodrigo.hw1_tqs.services;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.rodrigo.hw1_tqs.client.MunicipalityClient;
import pt.rodrigo.hw1_tqs.entities.Municipality;
import pt.rodrigo.hw1_tqs.repositories.MunicipalityRepository;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class MunicipalityServiceImpl implements MunicipalityService {

    static final Logger log = getLogger(lookup().lookupClass());

    private final MunicipalityRepository municipalityRepository;
    private final MunicipalityClient municipalityClient;

    public MunicipalityServiceImpl(MunicipalityRepository municipalityRepository, 
                                   MunicipalityClient municipalityClient) {
        this.municipalityRepository = municipalityRepository;
        this.municipalityClient = municipalityClient;
    }

    // Carrega municípios na inicialização da aplicação
    @PostConstruct
    public void init() {
        if (municipalityRepository.count() == 0) {
            log.info("Municipality cache is empty. Loading from API...");
            loadMunicipalitiesFromAPI();
        } else {
            log.info("Municipality cache already populated with {} entries", 
                     municipalityRepository.count());
        }
    }

    @Override
    public List<Municipality> getAllMunicipalities() {
        log.info("Fetching all municipalities from cache");
        return municipalityRepository.findAll();
    }

    @Override
    @Transactional
    public void loadMunicipalitiesFromAPI() {
        try {
            log.info("Loading municipalities from external API");
            
            List<Municipality> municipalities = municipalityClient.fetchMunicipalities();
            
            log.info("Saving {} municipalities to database cache", municipalities.size());
            
            // Salvar apenas se não existir (evitar duplicados)
            for (Municipality municipality : municipalities) {
                if (!municipalityRepository.existsByName(municipality.getName())) {
                    municipalityRepository.save(municipality);
                }
            }
            
            log.info("Successfully cached {} municipalities", municipalityRepository.count());
            
        } catch (Exception e) {
            log.error("Failed to load municipalities from API", e);
            throw new RuntimeException("Failed to load municipalities", e);
        }
    }

    @Override
    public Municipality getMunicipalityByName(String name) {
        log.info("Fetching municipality by name: {}", name);
        
        Optional<Municipality> municipality = municipalityRepository.findByName(name);
        
        if (municipality.isEmpty()) {
            log.warn("Municipality not found: {}", name);
            throw new IllegalArgumentException("Municipality not found: " + name);
        }
        
        return municipality.get();
    }
}
