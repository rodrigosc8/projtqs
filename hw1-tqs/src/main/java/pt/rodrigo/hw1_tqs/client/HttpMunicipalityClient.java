package pt.rodrigo.hw1_tqs.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pt.rodrigo.hw1_tqs.entities.Municipality;

import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class HttpMunicipalityClient implements MunicipalityClient {

    static final Logger log = getLogger(lookup().lookupClass());

    // API criada por colega do curso (Pedro Martins)
    // Contém 283 municípios portugueses
    private static final String MUNICIPALITY_API_URL = 
        "https://gist.githubusercontent.com/pedroMPMartins/315ed0c2601554e94a9dea07e76f3536/raw/d276cc3e21b8fb5f9b2e3a4b81ea74c8ea8bf5c8/municipalities-pt.json";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public HttpMunicipalityClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Municipality> fetchMunicipalities() {
        try {
            log.info("Fetching municipalities from: {}", MUNICIPALITY_API_URL);
            
            // Fetch como String para evitar problemas com content-type
            String json = restTemplate.getForObject(MUNICIPALITY_API_URL, String.class);
            
            if (json == null || json.trim().isEmpty()) {
                log.error("Empty response from municipalities API");
                throw new RuntimeException("Empty response from API");
            }
            
            // Parse JSON manualmente
            List<Municipality> result = objectMapper.readValue(
                json, 
                new TypeReference<List<Municipality>>() {}
            );
            
            log.info("Successfully parsed {} municipalities", result.size());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to fetch or parse municipalities", e);
            throw new RuntimeException("Unable to load municipalities", e);
        }
    }
}
