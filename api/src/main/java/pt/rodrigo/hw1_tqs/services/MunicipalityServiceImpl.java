package pt.rodrigo.hw1_tqs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Collections;

@Service
public class MunicipalityServiceImpl implements MunicipalityService {

    private static final Logger log = LoggerFactory.getLogger(MunicipalityServiceImpl.class);
    private static final String API_URL = "https://geoapi.pt/municipios?json=true";
    private final RestTemplate restTemplate;

    public MunicipalityServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public class MunicipalityServiceException extends RuntimeException {
        public MunicipalityServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Override
    public List<String> getAllMunicipalities() {
        try {
            log.info("Fetching municipalities from: {}", API_URL);
            
            ResponseEntity<List<String>> response = restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
            );

            List<String> municipalities = response.getBody();
            log.info("Received {} municipalities", municipalities != null ? municipalities.size() : 0);
            
            if (municipalities == null || municipalities.isEmpty()) {
                return Collections.emptyList();
            }

            Collections.sort(municipalities);
            
            log.info("Returning {} municipality names", municipalities.size());
            return municipalities;
            
        } catch (Exception e) {
            throw new MunicipalityServiceException("Failed to fetch municipalities from external API", e);
        }
    }
}
