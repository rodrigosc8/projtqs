package pt.rodrigo.hw1_tqs.controllers;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.rodrigo.hw1_tqs.entities.Municipality;
import pt.rodrigo.hw1_tqs.services.MunicipalityService;

import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/api/municipalities")
public class MunicipalityController {

    static final Logger log = getLogger(lookup().lookupClass());

    private final MunicipalityService municipalityService;

    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    // GET /api/municipalities - Obter todos os municípios (do cache)
    @GetMapping
    public ResponseEntity<List<Municipality>> getAllMunicipalities() {
        log.info("GET /api/municipalities - Fetching all municipalities");
        try {
            List<Municipality> municipalities = municipalityService.getAllMunicipalities();
            return new ResponseEntity<>(municipalities, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching municipalities: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST /api/municipalities/reload - Recarregar municípios da API externa
    @PostMapping("/reload")
    public ResponseEntity<String> reloadMunicipalities() {
        log.info("POST /api/municipalities/reload - Reloading municipalities from API");
        try {
            municipalityService.loadMunicipalitiesFromAPI();
            return new ResponseEntity<>("Municipalities reloaded successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error reloading municipalities: {}", e.getMessage());
            return new ResponseEntity<>("Failed to reload municipalities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /api/municipalities/{name} - Obter município por nome
    @GetMapping("/{name}")
    public ResponseEntity<Municipality> getMunicipalityByName(@PathVariable String name) {
        log.info("GET /api/municipalities/{} - Fetching municipality", name);
        try {
            Municipality municipality = municipalityService.getMunicipalityByName(name);
            return new ResponseEntity<>(municipality, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Municipality not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
